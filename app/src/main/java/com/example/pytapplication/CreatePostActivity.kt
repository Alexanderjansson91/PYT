package com.example.pytapplication

import android.app.Activity
import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import com.example.pytapplication.models.Post
import com.example.pytapplication.models.User
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import kotlinx.android.synthetic.main.activity_create_post.*

private const val TAG = "CreatePostActivity"
private const val PICK_PHOTO_CODE = 1337
private var photoURI: Uri? = null
private var signedInUser: User? = null
private lateinit var firestoreDB: FirebaseFirestore
private lateinit var storageRef: StorageReference
private const val EXTRA_USERNAME = "EXTRA_USERNAME"



class CreatePostActivity : AppCompatActivity() {



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_create_post)


        storageRef = FirebaseStorage.getInstance().reference
        firestoreDB = FirebaseFirestore.getInstance()

        //Check user
        firestoreDB.collection("users")
            .document(FirebaseAuth.getInstance().currentUser?.uid as String)
            .get()
            .addOnSuccessListener { userSnapshot ->
                signedInUser = userSnapshot.toObject(User::class.java)
                Log.i(TAG, "Signed in user: $signedInUser")
            }
            .addOnFailureListener { exception ->
                Log.i(TAG, "Failure fetching signed User", exception)
            }
        val imageButton = findViewById<Button>(R.id.imagePost)

        //Button to set cover to you post
        imageButton.setOnClickListener {
            Log.i(TAG, "Open up image picker on device")
            var imagePickerIntent = Intent(Intent.ACTION_GET_CONTENT)
            imagePickerIntent.type = "image/*"
            if (imagePickerIntent.resolveActivity(packageManager) != null){
                startActivityForResult(imagePickerIntent, PICK_PHOTO_CODE)
            }
        }
        //button how to upload the post and receive handleSubmitButtonClick ()
        val uploadPost = findViewById<Button>(R.id.uploadPostBtn)
        uploadPost.setOnClickListener {
            handleSubmitButtonClick ()
        }
    }

    // Funtions how handlde the submit
    private fun handleSubmitButtonClick(){
        var textViewNameArtist = findViewById<TextView>(R.id.nameArtist)
        var textViewNameTrack = findViewById<TextView>(R.id.nameTrack)

        if (photoURI == null) {
            Toast.makeText(this, "No photo is selected", Toast.LENGTH_SHORT).show()
            return
        }
        if (textViewNameArtist == null){
            Toast.makeText(this, "Artist name is not selected", Toast.LENGTH_SHORT).show()
            return
        }
        if (signedInUser == null){
            Toast.makeText(this, "No user", Toast.LENGTH_SHORT).show()
                    return
        }

        //upload photo To firebase storage
        uploadPostBtn.isEnabled = false
        val photoUploadUri = photoURI as Uri
        val photoreference = storageRef.child("images/${System.currentTimeMillis()}-photo.jpg")
        photoreference.putFile(photoUploadUri)
            .continueWithTask { photoUploadTask ->
                Log.i(TAG,"upload bytes: ${ photoUploadTask.result?.bytesTransferred}")
                //retrieve image URL
                photoreference.downloadUrl
            //Create a post object o posts collection
            }.continueWithTask { downloadUrlkTask ->
                val post = Post(
                textViewNameArtist.text.toString(),
                    textViewNameTrack.text.toString(),
                downloadUrlkTask.result.toString(),
                System.currentTimeMillis(),
                    signedInUser
                    )
                firestoreDB.collection("posts").add(post)

            //Complete button how give the user a result and Intent to ProfileActivity
            }.addOnCompleteListener { postCreationTask ->
                uploadPostBtn.isEnabled = true
            if (!postCreationTask.isSuccessful){
                Log.e(TAG,"exception during Firebase operations")
                Toast.makeText(this,"Failed to save post", Toast.LENGTH_SHORT).show()
            }
                //textViewNameArtist.text.clear()
                //ImageView.setImageResource(0)
                Toast.makeText(this,"Success", Toast.LENGTH_SHORT).show()
                val ProfileIntent = Intent(this,ProfileActivity::class.java)
                ProfileIntent.putExtra(EXTRA_USERNAME, signedInUser?.username)
                startActivity(ProfileIntent)
                finish()
            }

    }

    //Notify when user have selected a Image
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == PICK_PHOTO_CODE){
            if (resultCode == Activity.RESULT_OK){
                photoURI = data?.data
                imageView.setImageURI(photoURI)
                Log.i(TAG, "photoUri $photoURI")
            }else {
                Toast.makeText(this,"Image picker action cancelled", Toast.LENGTH_SHORT).show()
            }
        }
    }
}
