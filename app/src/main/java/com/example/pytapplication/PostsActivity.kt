package com.example.pytapplication

import android.content.Intent
import android.media.MediaPlayer
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.widget.Button
import android.widget.ImageButton
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.pytapplication.models.Post
import com.example.pytapplication.models.User
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query

private const val TAG = "message"
private const val EXTRA_USERNAME = "EXTRA_USERNAME"
open class PostsActivity : AppCompatActivity() {

    private var signedInUser: User? = null
    private lateinit var firestoreDB : FirebaseFirestore
    private lateinit var posts : MutableList<Post>
    private lateinit var postSong : Post
    private lateinit var adapter: Postadapter
    private lateinit var mp : MediaPlayer
    private var totalTime : Int =0


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_posts)

        //postSong = Post()
        //mp = MediaPlayer.create(this, postSong.audioUrl.toInt())

        mp = MediaPlayer.create(this,R.raw.skickabilder)
        mp.isLooping= true
        mp.setVolume(0.5f,0.5f)
        totalTime = mp.duration

        val recyclerView = findViewById<RecyclerView>(R.id.recyclerViewPosts)
        recyclerView.addItemDecoration(DividerItemDecoration(this, LinearLayoutManager.VERTICAL))
        playBtnClick()
        posts = mutableListOf()
        adapter = Postadapter(this, posts)
        recyclerView.adapter = adapter
        recyclerView.layoutManager =  LinearLayoutManager(this)
        firestoreDB = FirebaseFirestore.getInstance()

        //Get Current User
        firestoreDB.collection("users")
            .document(FirebaseAuth.getInstance().currentUser?.uid as String)
            .get()
            .addOnSuccessListener {usersnaphot ->
                signedInUser = usersnaphot.toObject(User::class.java)
                Log.i(TAG, "Signed in  user $signedInUser")
            }
            .addOnFailureListener { exception ->
                Log.i(TAG, "fail fetching user", exception)
            }

        var postsReference = firestoreDB
            .collection("posts")
            //.limit(20)
            .orderBy("creation_time_ms", Query.Direction.DESCENDING)

        //Set username to the actionbar
        val username = (intent.getStringExtra(EXTRA_USERNAME))
        if(username != null){
            postsReference = postsReference.whereEqualTo("user.username", username)
            supportActionBar?.title = username
        }

        //query for all my post
        postsReference.addSnapshotListener { snapshot, exception ->
            if (exception != null || snapshot == null) {
                Log.e(TAG,"Exception when querying posts", exception)
                return@addSnapshotListener
            }
            val postList = snapshot.toObjects(Post::class.java)
            posts.clear()
            posts.addAll(postList)
            adapter.notifyDataSetChanged()
            for (post in postList){
                Log.i(TAG, "Post ${post}")
            }
        }
        // Intent to CreatePostActivity
        val addBtn = findViewById<FloatingActionButton>(R.id.createNewPostBtn)
        addBtn.setOnClickListener {
            val intent = Intent(this, CreatePostActivity::class.java)
            startActivity(intent)
            println(posts)
        }
    }

    //my top menu(profile)
    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_posts, menu)
        return super.onCreateOptionsMenu(menu)
    }

    //Intent to profileActivity
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if(item.itemId == R.id.menu_profile){
            val intent = Intent(this, ProfileActivity::class.java)
            intent.putExtra(EXTRA_USERNAME,signedInUser?.username)
            startActivity(intent)
        }

        return super.onOptionsItemSelected(item)
    }
     fun playBtnClick() {
         if (mp.isPlaying) {
             mp.start()
             Log.e(TAG,"Hejdu")
         }else{
             mp.pause()
         }

          /*  if (mp.isPlaying) {
                mp.pause()
                recyclerView.setBackgroundResource(R.drawable.play)
            } else {
                mp.start()
                recyclerView.setBackgroundResource(R.drawable.stop)
            }*/

   }

    fun shareSong(){

            val myIntent = Intent(Intent.ACTION_SEND)
            myIntent.type = "type/palin"
            val shareBody = "You are body"
            val shareSub = "You subject here"
            myIntent.putExtra(Intent.EXTRA_SUBJECT, shareBody)
            myIntent.putExtra(Intent.EXTRA_SUBJECT, shareSub)
            startActivity(Intent.createChooser(myIntent,"share"))
            println("hej")

        }
    }
