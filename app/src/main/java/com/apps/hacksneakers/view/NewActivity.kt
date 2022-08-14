package com.apps.hacksneakers.view

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Toast
import com.apps.hacksneakers.R
import com.apps.hacksneakers.uitel.getProgessDrawable
import com.apps.hacksneakers.uitel.loadImage
import com.bumptech.glide.Glide
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import kotlinx.android.synthetic.main.activity_new.*
import kotlinx.android.synthetic.main.activity_profile.*

class NewActivity : AppCompatActivity() {
    private lateinit var firebaseAuth: FirebaseAuth
    var shoeId = ""
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_new)
        shoeId = intent.getStringExtra("shoeId")!!

        firebaseAuth = FirebaseAuth.getInstance()
        checkMember()
        btn_update_shoe.setOnClickListener {
            startActivity(Intent(this, EditShoeActivity::class.java))
            finish()
        }
        btn_delete_shoe.setOnClickListener {
            checkDetailData()
        }


        /**get Data*/
        val shoeIntent = intent
        val shoeName = shoeIntent.getStringExtra("name")
        val shoeInfo = shoeIntent.getStringExtra("info")
        val shoeImg = shoeIntent.getStringExtra("img")
        val shoeType = shoeIntent.getStringExtra("type")
        val shoePrice = shoeIntent.getStringExtra("price")

        /**call text and images*/
        name.text = shoeName
        type.text = shoeType
        info.text = "Stock barang : $shoeInfo"
        price.text = "Rp : $shoePrice"
        img.loadImage(shoeImg, getProgessDrawable(this))
    }

    private fun checkDetailData() {
        MaterialAlertDialogBuilder(this, R.style.ThemeOverlay_App_MaterialAlertDialog)
            .setTitle("Deleting Data")
            .setMessage("Are you sure want to delete this data ?")
            .setPositiveButton("Yes"){a, s->
                deleteCurrentData()
            }
            .setNegativeButton("Cancel"){a, s->
            }
            .show()
    }

    private fun deleteCurrentData() {
        val ref = FirebaseDatabase.getInstance().getReference("Shoe")
        ref.child(shoeId)
            .removeValue()
            .addOnSuccessListener {
                Toast.makeText(this, "Data has been deleted.", Toast.LENGTH_SHORT).show()

                startActivity(Intent(this, MainActivity::class.java))
                finish()
            }
            .addOnFailureListener{
                Toast.makeText(this, "Something went wrong, ${it.message}", Toast.LENGTH_SHORT).show()
            }
    }

    private fun checkMember() {
        val firebaseUser = firebaseAuth.currentUser!!
        val ref = FirebaseDatabase.getInstance().getReference("Users")
        ref.child(firebaseUser.uid)
            .addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    val userType = snapshot.child("userType").value

                    if (userType == "Customer"){
                        btn_update_shoe.visibility = View.GONE
                    }
                }
                override fun onCancelled(error: DatabaseError) {

                }
            })
    }
}