package com.example.pytapplication

import android.content.AbstractThreadedSyncAdapter
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.widget.Button
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.pytapplication.models.Post
import com.example.pytapplication.models.User
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import kotlinx.android.synthetic.main.activity_posts.*

private const val TAG = "message"
private const val EXTRA_USERNAME = "EXTRA_USERNAME"
open class PostsActivity : AppCompatActivity() {

    private var signedInUser: User? = null
    private lateinit var firestoreDB : FirebaseFirestore
    private lateinit var posts : MutableList<Post>
    private lateinit var adapter: Postadapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_posts)
        val recyclerView = findViewById<RecyclerView>(R.id.recyclerViewPosts)
        posts = mutableListOf()
        adapter = Postadapter(this, posts)
        recyclerView.adapter =adapter
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
        }

    }

    //referns fo my menu
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


}
