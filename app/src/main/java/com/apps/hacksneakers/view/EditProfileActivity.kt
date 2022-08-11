package com.apps.hacksneakers.view

import android.app.Activity
import android.app.ProgressDialog
import android.content.ContentValues
import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.view.Menu
import android.widget.PopupMenu
import android.widget.Toast
import androidx.activity.result.ActivityResult
import androidx.activity.result.ActivityResultCallback
import androidx.activity.result.contract.ActivityResultContracts
import com.apps.hacksneakers.R
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage
import kotlinx.android.synthetic.main.activity_add_shoes.*
import kotlinx.android.synthetic.main.activity_edit_profile.*

class EditProfileActivity : AppCompatActivity() {
    private lateinit var firebaseAuth: FirebaseAuth
    private lateinit var progressDialog: ProgressDialog
    private var imgUrl: Uri?= null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_edit_profile)

        progressDialog = ProgressDialog(this)
        progressDialog.setTitle("Please Wait!")
        progressDialog.setCanceledOnTouchOutside(false)

        firebaseAuth = FirebaseAuth.getInstance()

        img_update_profile.setOnClickListener{
            setAttachPicture()
        }
        btn_update.setOnClickListener {
            validateData()
        }
    }

    private var name = ""

    private fun validateData() {
        name = name_profile.text.toString().trim()

        if (name.isEmpty()){
            Toast.makeText(this,
            "Masukkan Nama..", Toast.LENGTH_SHORT)
                .show()
        }else{
            if (imgUrl == null){
                updateProfile("")
            }else{
                uploadImage()
            }
        }
    }

    private fun uploadImage() {
        progressDialog.setMessage("Uploading Image..")
        progressDialog.show()

        val filePathAndName = "profileImage/"+firebaseAuth.uid
        val reference = FirebaseStorage.getInstance().getReference(filePathAndName)
        reference.putFile(imgUrl!!)
            .addOnSuccessListener { taskSnapshot ->
                val uriTask: Task<Uri> = taskSnapshot.storage.downloadUrl
                while (!uriTask.isSuccessful);
                val uploadedImageUrl = "${uriTask.result}"

                updateProfile(uploadedImageUrl)

            }
            .addOnFailureListener {
                progressDialog.dismiss()
                Toast.makeText(this,
                    "Failed to Upload image..",Toast.LENGTH_SHORT)
                    .show()
            }
    }

    private fun updateProfile(uploadedImageUrl: String) {
        progressDialog.setMessage("Updating Profile ...")

        val hashMap: HashMap<String, Any> = HashMap()
        hashMap["name"] = "${name}"

        if (imgUrl !== null){
            hashMap["profileImage"] = uploadedImageUrl
        }

        val reference = FirebaseDatabase.getInstance().getReference("Users")
        reference.child(firebaseAuth.uid!!)
            .updateChildren(hashMap)
            .addOnSuccessListener {
                progressDialog.dismiss()
                Toast.makeText(this,
                    "Profile Terlah diperbarui..",
                    Toast.LENGTH_SHORT)
                    .show()

                name_profile.text.clear()
            }
            .addOnFailureListener{e ->
                progressDialog.dismiss()
                Toast.makeText(this,
                    "Gagal Memperbarui Profile ${e.message}",
                    Toast.LENGTH_SHORT)
                    .show()
            }
    }


    private fun setAttachPicture() {
        val popupMenu = PopupMenu(this, img_item)
        popupMenu.menu.add(Menu.NONE, 0, 0, "Camera")
        popupMenu.menu.add(Menu.NONE, 1, 1, "Gallery")
        popupMenu.show()

        popupMenu.setOnMenuItemClickListener { item ->
            val id = item.itemId
            if (id == 0){
                pickImageCamera()
            }
            else if (id == 1 ){
                pickImageGallery()
            }
            true
        }
    }

    private fun pickImageGallery() {
        val intent = Intent(Intent.ACTION_PICK)
        intent.type = "image/*"
        galleryActivityResultLauncher.launch(intent)
    }

    private fun pickImageCamera() {
        val values = ContentValues()
        values.put(MediaStore.Images.Media.TITLE, "Temp_Title")
        values.put(MediaStore.Images.Media.DESCRIPTION, "Temp_Description")

        imgUrl = contentResolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values)

        val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        intent.putExtra(MediaStore.EXTRA_OUTPUT, imgUrl)
        cameraActivityResultLauncher.launch(intent)

    }
    private val cameraActivityResultLauncher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult(),
        ActivityResultCallback<ActivityResult>{ result ->
            if (result.resultCode == Activity.RESULT_OK){
                val data = result.data

                img_item.setImageURI(imgUrl)
            }else{
                Toast.makeText(this,
                    "Cancelled",Toast.LENGTH_SHORT)
                    .show()
            }
        }
    )
    private val galleryActivityResultLauncher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult(),
        ActivityResultCallback<ActivityResult>{ result ->
            if (result.resultCode == Activity.RESULT_OK){
                val data = result.data
                imgUrl = data!!.data
                img_item.setImageURI(imgUrl)
            }else{
                Toast.makeText(this,
                    "Cancelled",Toast.LENGTH_SHORT)
                    .show()
            }

        }
    )
}