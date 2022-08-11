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

class AddShoesActivity : AppCompatActivity() {
    private lateinit var firebaseAuth: FirebaseAuth
    private lateinit var progressDialog: ProgressDialog
    private var imgUrl: Uri?= null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_shoes)

        progressDialog = ProgressDialog(this)
        progressDialog.setTitle("Please Wait!")
        progressDialog.setCanceledOnTouchOutside(false)

        firebaseAuth = FirebaseAuth.getInstance()

        img_item.setOnClickListener {
            setAttachPicture()
        }
        btn_add.setOnClickListener {
            validateData()
        }
    }

    private var name = ""
    private var type = ""
    private var price = ""
    private var info = ""

    private fun validateData() {
        name = name_item.text.toString().trim()
        type = type_item.text.toString().trim()
        price = price_item.text.toString().trim()
        info = info_item.text.toString().trim()


        if (name.isEmpty()){
            Toast.makeText(this,
            "Masukan Nama Barang.", Toast.LENGTH_SHORT)
                .show()
        }else if (type.isEmpty()){
            Toast.makeText(this,
                "Masukan Type Barang.", Toast.LENGTH_SHORT)
                .show()
        }else if (price.isEmpty()){
            Toast.makeText(this,
                "Masukan Harga Barang.", Toast.LENGTH_SHORT)
                .show()
        }else if (info.isEmpty()){
            Toast.makeText(this,
                "Masukan Informasi Barang.", Toast.LENGTH_SHORT)
                .show()
        }else{
            if (imgUrl == null){
                addItems("")
            }else{
                addItemsWithImg()
            }
        }
    }

    private fun addItemsWithImg() {
        progressDialog.setMessage("Uploading Image..")
        progressDialog.show()

        val filePathAndName = "img/"+firebaseAuth.uid
        val reference = FirebaseStorage.getInstance().getReference(filePathAndName)
        reference.putFile(imgUrl!!)
            .addOnSuccessListener { taskSnapshot ->
                val uriTask: Task<Uri> = taskSnapshot.storage.downloadUrl
                while (!uriTask.isSuccessful);
                val uploadedImageUrl = "${uriTask.result}"

                addItems(uploadedImageUrl)

            }
            .addOnFailureListener {
                progressDialog.dismiss()
                Toast.makeText(this,
                "Failed to Upload image..",Toast.LENGTH_SHORT)
                    .show()
            }
    }

    private fun addItems(uploadedImageUrl : String) {
        progressDialog.setMessage("Menambahkan Barang ...")
        progressDialog.show()

        val timestamp = System.currentTimeMillis()
        val hashMap: HashMap<String, Any> = HashMap()

        hashMap["id"] = "$timestamp"
        hashMap["name"] = name
        hashMap["type"] = type
        hashMap["price"] = price
        hashMap["info"] = info
        hashMap["uid"] = "${firebaseAuth.uid}"


        if (imgUrl !== null){
            hashMap["img"] = uploadedImageUrl
        }

        val ref = FirebaseDatabase.getInstance().getReference("Shoe")
        ref.child("$timestamp")
            .setValue(hashMap)
            .addOnSuccessListener {
                progressDialog.dismiss()
                Toast.makeText(this,
                    "Barang Telah di tambahkan..",
                    Toast.LENGTH_SHORT)
                    .show()

                name_item.text.clear()
                type_item.text.clear()
                info_item.text.clear()
                price_item.text.clear()
            }
            .addOnFailureListener{e ->
                progressDialog.dismiss()
                Toast.makeText(this,
                    "Gagal menambahkan Barang ${e.message}",
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
        ActivityResultCallback<ActivityResult>{result ->
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