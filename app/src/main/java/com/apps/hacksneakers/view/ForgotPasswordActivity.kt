package com.apps.hacksneakers.view

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import com.apps.hacksneakers.R
import com.google.firebase.auth.FirebaseAuth
import kotlinx.android.synthetic.main.activity_forgot_password.*

class ForgotPasswordActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_forgot_password)

        btn_forgot_password.setOnClickListener {
            val email: String = email_forgot_password.text.toString().trim { it <= ' ' }
            if (email.isEmpty()) {
                Toast.makeText(this,
                "Please enter your email",Toast.LENGTH_SHORT)
                    .show()
            }else{
                FirebaseAuth.getInstance().sendPasswordResetEmail(email).addOnCompleteListener { task ->
                    if (task.isSuccessful){
                        Toast.makeText(this,
                            "Email sent, please check your email.",Toast.LENGTH_LONG)
                            .show()
                        finish()
                    }else{
                        Toast.makeText(this,
                            task.exception!!.message.toString(),Toast.LENGTH_LONG)
                            .show()
                    }
                }
            }
        }
    }
}