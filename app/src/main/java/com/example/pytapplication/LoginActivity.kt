package com.example.pytapplication

import android.content.Intent
import android.nfc.Tag
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import kotlinx.android.synthetic.main.activity_login.*

class LoginActivity : AppCompatActivity() {

    val TAG = "MyMessage"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)
        val auth = FirebaseAuth.getInstance()
        if (auth.currentUser != null) {
            getPostActivity()
        }
        //Log in Button, return Toast if email/password are empty
        btnLogin.setOnClickListener {
            btnLogin.isEnabled = false
            val email = etEmail.text.toString()
            val password = etPassword.text.toString()
            if(email.isBlank() || password.isBlank()){
                Toast.makeText(this, "email/password cannot be empty", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            //Firebase authentication check
            auth.signInWithEmailAndPassword(email,password).addOnCompleteListener { task ->
                btnLogin.isEnabled = true
                if (task.isSuccessful){
                    Toast.makeText(this,"success", Toast.LENGTH_SHORT).show()
                    getPostActivity()
                }else {
                   Log.i(TAG, "goToPostActivity", task.exception)
                    Toast.makeText(this,"authentication Failed", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }
    //intent to PostsActivity
    private fun getPostActivity() {
       Log.i(TAG, "goToPostActivity")
        val intent = Intent(this, PostsActivity::class.java)
        startActivity(intent)
        finish()
    }


}
