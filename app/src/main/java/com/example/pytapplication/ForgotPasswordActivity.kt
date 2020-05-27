package com.example.pytapplication

import android.content.DialogInterface
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Patterns
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import com.google.firebase.auth.FirebaseAuth


class ForgotPasswordActivity : AppCompatActivity() {

    lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_forgot_password)

        val forgotPasswordBtn = findViewById<Button>(R.id.reset_password_btn)

        //Clicklistner on my "Reset button"
        forgotPasswordBtn.setOnClickListener {
            val username = findViewById<EditText>(R.id.email_reset_Password)
            forgotPassword(username)
        }

    }

    //Function how reset password and send and reset link you valid user.
    fun forgotPassword(username: EditText?) {

        val auth = FirebaseAuth.getInstance()
        if (username?.text.toString().isEmpty()) {
            return
        }
        if (!Patterns.EMAIL_ADDRESS.matcher(username?.text.toString()).matches()) {
            return
        }

        auth.sendPasswordResetEmail(username?.text.toString())
            .addOnCompleteListener {
                if (it.isSuccessful) {
                    Toast.makeText(this, "Email sent, check inbox", Toast.LENGTH_SHORT).show()
                } else {
                    Toast.makeText(this, "Something ent wrong", Toast.LENGTH_SHORT).show()

                }
            }
        }
    }


