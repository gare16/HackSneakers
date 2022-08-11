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
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.FirebaseDatabase
import kotlinx.android.synthetic.main.activity_sign_up.*
import java.util.regex.Pattern

class SignUpActivity : AppCompatActivity() {
    private lateinit var firebaseAuth: FirebaseAuth

    @SuppressLint("ResourceAsColor")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sign_up)

        firebaseAuth = FirebaseAuth.getInstance()

        btnSignUp.setOnClickListener {
            validateData()
        }

        //Span TV to LoginActivity
        val text = "Already have Account? Click Here."
        val spannableString = SpannableString(text)
        val clickableSpan1: ClickableSpan = object : ClickableSpan() {
            override fun onClick(p0: View) {
                startActivity(Intent(this@SignUpActivity, LoginActivity::class.java))
            }
        }

        spannableString.setSpan(clickableSpan1, 22, 33, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
        haveAccount.setText(spannableString, TextView.BufferType.SPANNABLE)
        haveAccount.setTextColor(R.color.primary)
        haveAccount.movementMethod = LinkMovementMethod.getInstance()
    }

    private var name = ""
    private var email = ""
    private var password = ""

    private fun validateData() {
        name = name_sign_up.text.toString().trim()
        email = email_sign_up.text.toString().trim()
        password = password_sign_up.text.toString().trim()

        if (name.isEmpty()){
            Toast.makeText(this,
            "Please Enter your Name.",
            Toast.LENGTH_SHORT)
                .show()
        }else if(!Patterns.EMAIL_ADDRESS.matcher(email).matches()){
            Toast.makeText(this,
                "Invalid Email.",
                Toast.LENGTH_SHORT)
                .show()
        }else if(password.isEmpty()){
            Toast.makeText(this,
                "Please Enter your Password.",
                Toast.LENGTH_SHORT)
                .show()
        }else{
            createUser()
        }
    }



    private fun createUser() {

        firebaseAuth.createUserWithEmailAndPassword(email, password)
            .addOnSuccessListener {
                updateUserInfo()
            }
            .addOnFailureListener {
                Toast.makeText(this,
                    "Failed create account.",
                    Toast.LENGTH_SHORT)
                    .show()
            }
    }

    private fun updateUserInfo() {

        val timestamp = System.currentTimeMillis()
        val uid = firebaseAuth.uid
        val hashMap : HashMap<String, Any?> = HashMap()

        hashMap["uid"] = uid
        hashMap["email"] = email
        hashMap["name"] = name
        hashMap["profileImage"] = ""
        hashMap["userType"] = "Customer"
        hashMap["timestamp"] = timestamp

        val ref = FirebaseDatabase.getInstance().getReference("Users")
        ref.child(uid!!)
            .setValue(hashMap)
            .addOnSuccessListener {
                Toast.makeText(this,
                    "Account Created..",
                    Toast.LENGTH_SHORT)
                    .show()

                startActivity(Intent(this, MainActivity::class.java))
                finish()
            }
            .addOnFailureListener{
                Toast.makeText(this,
                    "Failed saving user info.",
                    Toast.LENGTH_SHORT)
                    .show()
            }

    }
}