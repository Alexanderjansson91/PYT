package com.example.pytapplication

import android.content.DialogInterface
import android.content.Intent
import android.graphics.Color
import android.nfc.Tag
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.util.Patterns
import android.view.Menu
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.ActionBar
import androidx.appcompat.app.AlertDialog
import com.google.firebase.auth.FirebaseAuth
import kotlinx.android.synthetic.main.activity_forgot_password.*
import kotlinx.android.synthetic.main.activity_login.*
import kotlinx.android.synthetic.main.activity_posts.*

class LoginActivity : AppCompatActivity() {

    val TAG = "MyMessage"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)
        val auth = FirebaseAuth.getInstance()
        if (auth.currentUser != null) {
            getPostActivity()
        }

        //Clicklistner on my "new User Button" button
        val newUserButton = findViewById<Button>(R.id.new_Profil)
        newUserButton.setOnClickListener {
            goToRegisterPage()
        }

        //Log in Button, return Toast if email/password are empty
        btnLogin.setOnClickListener {
            btnLogin.isEnabled = false
            val email = etEmail.text.toString()
            val password = etPassword.text.toString()
            if (email.isBlank() || password.isBlank()) {
                Toast.makeText(this, "email/password cannot be empty", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            //Firebase authentication check
            auth.signInWithEmailAndPassword(email, password).addOnCompleteListener { task ->
                btnLogin.isEnabled = true
                if (task.isSuccessful) {
                    Toast.makeText(this, "success", Toast.LENGTH_SHORT).show()
                    getPostActivity()
                } else {
                    Log.i(TAG, "goToPostActivity", task.exception)
                    Toast.makeText(this, "authentication Failed", Toast.LENGTH_SHORT).show()
                }
            }
        }
        //Clicklistner on my "forgot password" button
        val forgotPassword = findViewById<Button>(R.id.forgot_Password_button)
        forgotPassword.setOnClickListener {
            goToforgotPasswordPage()
        }
    }

    //go to forgot password page
    fun goToforgotPasswordPage() {
        val intent = Intent(this, ForgotPasswordActivity::class.java)
        startActivity(intent)
    }

    //go to register page
    fun goToRegisterPage() {
        val intent = Intent(this, createUserActivity::class.java)
        startActivity(intent)
    }

    //intent to PostsActivity
    private fun getPostActivity() {
        Log.i(TAG, "goToPostActivity")
        val intent = Intent(this, PostsActivity::class.java)
        startActivity(intent)
        finish()
    }
}
