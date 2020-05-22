package com.example.pytapplication

import android.app.Activity
import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.*
import androidx.core.content.ContextCompat
import com.example.pytapplication.models.Post
import com.example.pytapplication.models.User
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import kotlinx.android.synthetic.main.activity_create_post.*

private const val TAG = "CreatePostActivity"
private const val PICK_PHOTO_CODE = 1337
private const val PICK_AUDIO_CODE = 1336
private var URI: Uri? = null
private var signedInUser: User? = null
private lateinit var firestoreDB: FirebaseFirestore
private lateinit var storageRef: StorageReference
private const val EXTRA_USERNAME = "EXTRA_USERNAME"

class CreatePostActivity : AppCompatActivity() {

    lateinit var option : Spinner
    lateinit var genreResult : TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_create_post)
        spinner ()


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

        //Button to set audiofile to yout post
        val soundbtn = findViewById<Button>(R.id.soundfile)
        soundbtn.setOnClickListener {
            Log.i(TAG, "Open up audio picker on device")
            var soundPickerIntent = Intent(Intent.ACTION_GET_CONTENT)
            soundPickerIntent.type = "audio/*"
            if (soundPickerIntent.resolveActivity(packageManager) != null){
                startActivityForResult(soundPickerIntent, PICK_AUDIO_CODE)
            }
        }


        //button how upload the post and receive handleSubmitButtonClick ()
        val uploadPost = findViewById<Button>(R.id.uploadPostBtn)
        uploadPost.setOnClickListener {
            handleSubmitButtonClick ()
        }
    }

    //Funtions how handlde the submit
    private fun handleSubmitButtonClick() {
        var textViewNameArtist = findViewById<TextView>(R.id.nameArtist)
        var textViewNameTrack = findViewById<TextView>(R.id.nameTrack)
        var songUrl = findViewById<TextView>(R.id.uriSong)
        genreResult = findViewById(R.id.spinnerResult)
        var spotify = findViewById<EditText>(R.id.spotify_URL)
        var soundcloud = findViewById<EditText>(R.id.soundcloud_URL)
        var facebook = findViewById<EditText>(R.id.facebook_URL)
        var instagram = findViewById<EditText>(R.id.instagram_URL)
        if (URI == null) {
            Toast.makeText(this, "No audio is selected", Toast.LENGTH_SHORT).show()
            return
        }
        if (textViewNameArtist == null) {
            Toast.makeText(this, "Artist name is not selected", Toast.LENGTH_SHORT).show()
            return
        }
        if (signedInUser == null) {
            Toast.makeText(this, "No user", Toast.LENGTH_SHORT).show()
            return
        }
        //upload audio To firebase storage
        uploadPostBtn.isEnabled = false
        var audioDownloadUrl: String? = null
                    uploadPostBtn.isEnabled = true
                    val audioUploadUri = URI as Uri
                    val Audioreference = storageRef.child("audios/${System.currentTimeMillis()}-audio.wav")
                    Audioreference.putFile(audioUploadUri)
                        .addOnCompleteListener {
                            Audioreference.downloadUrl.addOnSuccessListener {
                                audioDownloadUrl = it.toString()
                                val post = Post(
                                    textViewNameArtist.text.toString(),
                                    textViewNameTrack.text.toString(),
                                    System.currentTimeMillis(),
                                    signedInUser,
                                    audioDownloadUrl!!,
                                    genreResult.text.toString(),
                                    spotify.text.toString(),
                                    instagram.text.toString(),
                                    facebook.text.toString(),
                                    soundcloud.text.toString()
                                )
                                firestoreDB.collection("posts").add(post)
                                Toast.makeText(this, "Success", Toast.LENGTH_SHORT).show()
                                val ProfileIntent = Intent(this, ProfileActivity::class.java)
                                //ProfileIntent.setAction(Intent.ACTION_SEND_MULTIPLE)
                                ProfileIntent.putExtra(EXTRA_USERNAME, signedInUser?.username)
                                startActivity(ProfileIntent)
                                finish()
                            }
                        }
                    }


    //Notify when user have selected a Image
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        val uriSong = findViewById<TextView>(R.id.uriSong)
        if (resultCode == Activity.RESULT_OK){
         if (requestCode == PICK_AUDIO_CODE){
            URI = data?.data
                uriSong.text = URI.toString()
                Log.i(TAG, "VideoUri $URI")
             }
            else {
                Toast.makeText(this,"file picker action cancelled", Toast.LENGTH_SHORT).show()
            }
        }
    }

    fun spinner (){
        option = findViewById(R.id.genreSpinner)
        genreResult = findViewById(R.id.spinnerResult)
        val options = arrayOf("Pop","Rock","trap","Hiphop","rap","house","Electronic")
        option.adapter = ArrayAdapter<String>(this, android.R.layout.simple_expandable_list_item_1, options)

        option.onItemSelectedListener = object  :AdapterView.OnItemSelectedListener{
            override fun onNothingSelected(parent: AdapterView<*>?) {
                genreResult.text = "Please selec an genre"
            }

            override fun onItemSelected( parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                genreResult.text = options.get(position)
            }
        }
    }
}



