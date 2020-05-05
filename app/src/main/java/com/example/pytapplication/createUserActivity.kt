package com.example.pytapplication

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.core.content.ContextCompat.startActivity
import com.example.pytapplication.models.Post
import com.example.pytapplication.models.User
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class createUserActivity : AppCompatActivity() {


    private lateinit var firestoreDB: FirebaseFirestore
    private var newUsername : User? = null
    private var mAuth: FirebaseAuth? = null
    //lateinit var auth : FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_create_user)
        firestoreDB = FirebaseFirestore.getInstance()
     //   newUsername = User()
    //Button how creates a new user
        mAuth = FirebaseAuth.getInstance()
        val createNewUser = findViewById<Button>(R.id.New_User_Button)
        createNewUser.setOnClickListener {
            registerUser ()
        }
    }

    //New user function
    private fun registerUser () {
        val emailTxt = findViewById(R.id.new_user_Email) as EditText
        val passwordTxt = findViewById(R.id.new_user_Password) as EditText
        val nameTxt = findViewById(R.id.new_user_Username) as EditText

        var email = emailTxt.text.toString()
        var password = passwordTxt.text.toString()
        var name = nameTxt.text.toString()


        if (!email.isEmpty() && !password.isEmpty() && !name.isEmpty()) {
            mAuth?.createUserWithEmailAndPassword(email, password)
                ?.addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        val user = mAuth?.currentUser
                        val uid = user!!.uid
                        //name = newUsername?.username.toString()
                        val username = User(name)
                        //val nuser = mapOf((newUsername?.username) to name)
                        firestoreDB = FirebaseFirestore.getInstance()
                        firestoreDB.collection("users").document(uid).set(username)
                        startActivity(Intent(this, LoginActivity::class.java))
                        Toast.makeText(this, "Successfully registered :)", Toast.LENGTH_LONG).show()
                    }else {
                        Toast.makeText(this, "Error registering, try again later :(", Toast.LENGTH_LONG).show()
                    }
                }
        }else {
            Toast.makeText(this,"Please fill up the Credentials :|", Toast.LENGTH_LONG).show()
        }
println(name)
    }

}