package com.apps.hacksneakers.view

import android.annotation.SuppressLint
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.SpannableString
import android.text.Spanned
import android.text.TextUtils
import android.text.method.LinkMovementMethod
import android.text.style.ClickableSpan
import android.util.Patterns
import android.view.View
import android.widget.TextView
import android.widget.Toast
import com.apps.hacksneakers.R
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import kotlinx.android.synthetic.main.activity_login.*

class LoginActivity : AppCompatActivity() {

    private lateinit var firebaseAuth: FirebaseAuth

    @SuppressLint("ResourceAsColor")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)
        firebaseAuth = FirebaseAuth.getInstance()

        //Sign Up Span Clickable
        val text = "If you dont have an Account, Sign Up"
        val spannableString = SpannableString(text)
        val clickableSpan1: ClickableSpan = object : ClickableSpan() {
            override fun onClick(p0: View) {
                startActivity(Intent(this@LoginActivity, SignUpActivity::class.java))
            }
        }

        spannableString.setSpan(clickableSpan1, 29, 36, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
        sign_up.setText(spannableString, TextView.BufferType.SPANNABLE)
        sign_up.setTextColor(R.color.primary)
        sign_up.movementMethod = LinkMovementMethod.getInstance()

        forgotPassword.setOnClickListener {
            startActivity(Intent(this, ForgotPasswordActivity::class.java))
        }

        sign_in.setOnClickListener {
            validateData()
        }
    }

    private var email = ""
    private var password = ""

    private fun validateData() {
        email = email_sign_in.text.toString().trim()
        password = password_sign_in.text.toString().trim()

        if(!Patterns.EMAIL_ADDRESS.matcher(email).matches()){
            Toast.makeText(this,
                "Invalid Email.",
                Toast.LENGTH_SHORT)
                .show()
        }else if (password.isEmpty()){
            Toast.makeText(this,
                "Please Enter your Password.",
                Toast.LENGTH_SHORT)
                .show()
        }else{
            loginUser()
        }
    }

    private fun loginUser() {
        firebaseAuth.signInWithEmailAndPassword(email, password)
            .addOnSuccessListener {
                checkUser()
            }
            .addOnFailureListener {
                Toast.makeText(this,
                    "Login Failed.",
                    Toast.LENGTH_SHORT)
                    .show()
            }
    }

    private fun checkUser() {
        val firebaseUser = firebaseAuth.currentUser!!
        val ref = FirebaseDatabase.getInstance().getReference("Users")
        ref.child(firebaseUser.uid)
            .addListenerForSingleValueEvent(object : ValueEventListener{
                override fun onDataChange(snapshot: DataSnapshot) {
                    val userType = snapshot.child("userType").value
                    if (userType == "Customer"){
                        startActivity(Intent(this@LoginActivity, MainActivity::class.java))
                        finish()
                    }else if (userType == "Admin"){
                        startActivity(Intent(this@LoginActivity, MainActivity::class.java))
                        finish()
                    }
                }

                override fun onCancelled(error: DatabaseError) {

                }
            })
    }

    override fun onStart() {
        super.onStart()
        if (firebaseAuth.currentUser != null) {
            val intent = Intent(this@LoginActivity, MainActivity::class.java)
            startActivity(intent)
        }
    }
}