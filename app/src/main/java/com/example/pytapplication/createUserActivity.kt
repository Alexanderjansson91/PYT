package com.example.pytapplication

import android.app.Activity
import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.*
import androidx.core.content.ContextCompat.startActivity
import com.example.pytapplication.models.Post
import com.example.pytapplication.models.User
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import kotlinx.android.synthetic.main.activity_create_post.*
import java.util.*

private const val PICK_PHOTO_CODE = 1337
private var URI: Uri? = null
private lateinit var firestoreDB: FirebaseFirestore
private lateinit var storageRef: StorageReference
private const val EXTRA_USERNAME = "EXTRA_USERNAME"
private const val TAG = "CreateUserActivity"

class createUserActivity : AppCompatActivity() {


    private lateinit var firestoreDB: FirebaseFirestore
    private var newUsername: User? = null
    private var mAuth: FirebaseAuth? = null
    var imageButton: Button? = null
    //lateinit var auth : FirebaseAuth


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_create_user)

        firestoreDB = FirebaseFirestore.getInstance()
        storageRef = FirebaseStorage.getInstance().reference

        //Button how creates a new user
        mAuth = FirebaseAuth.getInstance()
        val createNewUser = findViewById<Button>(R.id.New_User_Button)
        createNewUser.setOnClickListener {
            registerUser()
        }

        //Button to add a cover to your profile
        val imageButton = findViewById(R.id.add_Profile_Image_Btn) as Button
        imageButton.setOnClickListener {
            Log.i(TAG, "Open up image picker on device")
            var imagePickerIntent = Intent(Intent.ACTION_GET_CONTENT)
            imagePickerIntent.type = "image/*"
            if (imagePickerIntent.resolveActivity(packageManager) != null) {
                startActivityForResult(imagePickerIntent, PICK_PHOTO_CODE)
            }
        }
    }

    //New user function
    private fun registerUser() {

        val emailTxt = findViewById(R.id.new_user_Email) as EditText
        val passwordTxt = findViewById(R.id.new_user_Password) as EditText
        val nameTxt = findViewById(R.id.new_user_Username) as EditText
        var email = emailTxt.text.toString()
        var password = passwordTxt.text.toString()
        var name = nameTxt.text.toString()

        //upload Image To firebase storage
        imageButton?.isEnabled = false
        var photoDownloadUrl: String? = null
        val photoUploadUri = URI as Uri
        val photoreference = storageRef.child("profileimages/-photo.jpg")
        photoreference.putFile(photoUploadUri)
        photoreference.downloadUrl.addOnSuccessListener { photoUploadTask ->
            photoDownloadUrl = photoUploadTask.toString()


            if (!email.isEmpty() && !password.isEmpty() && !name.isEmpty()) {
                mAuth?.createUserWithEmailAndPassword(email, password)
                    ?.addOnCompleteListener { task ->
                        if (task.isSuccessful) {
                            val user = mAuth?.currentUser
                            val uid = user!!.uid
                            val username = User(name, photoDownloadUrl!!)
                            firestoreDB.collection("users").document(uid).set(username)
                            startActivity(Intent(this, LoginActivity::class.java))
                            Toast.makeText(
                                this,
                                "Successfully registered :)",
                                Toast.LENGTH_LONG
                            ).show()
                        } else {
                            Toast.makeText(
                                this,
                                "Error registering, try again later :(",
                                Toast.LENGTH_LONG
                            ).show()
                        }
                    }
            } else {
                Toast.makeText(this, "Please fill up the Credentials :|", Toast.LENGTH_LONG)
                    .show()
            }
            println(name)
        }
    }

    //Notify when user have selected a Image
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        val imageProfile = findViewById<ImageView>(R.id.profile_image)
        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == PICK_PHOTO_CODE) {
                URI = data?.data
                imageProfile.setImageURI(URI)
                Log.i(TAG, "photoUri $URI")
            }
        }
    }
}