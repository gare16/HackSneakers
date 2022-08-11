package com.apps.hacksneakers.view

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.apps.hacksneakers.R
import com.bumptech.glide.Glide
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import kotlinx.android.synthetic.main.activity_profile.*

class ProfileActivity : AppCompatActivity() {
    private lateinit var firebaseAuth : FirebaseAuth
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_profile)
        firebaseAuth = FirebaseAuth.getInstance()
        AddCheck()

        fab_add.setOnClickListener {
            startActivity(Intent(this, AddShoesActivity::class.java))
            finish()
        }

        editProfile.setOnClickListener {
            startActivity(Intent(this, EditProfileActivity::class.java))
            finish()
        }

        btn_logout.setOnClickListener {
            firebaseAuth.signOut()

            startActivity(Intent(this@ProfileActivity, LoginActivity::class.java))
            finish()
        }

    }

    private fun AddCheck() {
        val firebaseUser = firebaseAuth.currentUser!!
        val ref = FirebaseDatabase.getInstance().getReference("Users")
        ref.child(firebaseUser.uid)
            .addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    val email = "${snapshot.child("email").value}"
                    val name = "${snapshot.child("name").value}"
                    val profileImage = "${snapshot.child("profileImage").value}"
                    val userType1 = "${snapshot.child("userType").value}"
                    val userType = snapshot.child("userType").value

                    username.text = name
                    email_profile.text = email
                    status.text = userType1

                    try {
                        Glide.with(this@ProfileActivity)
                            .load(profileImage)
                            .placeholder(R.drawable.user_demo)
                            .into(circleImageView)

                    }catch (e: Exception){

                    }

                    if (userType == "Customer"){
                        fab_add.hide()
                    }else if (userType == "Admin"){
                        fab_add.show()
                    }
                }
                override fun onCancelled(error: DatabaseError) {

                }
            })
    }


}