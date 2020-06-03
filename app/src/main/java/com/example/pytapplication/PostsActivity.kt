package com.example.pytapplication

import android.content.ClipData
import android.content.Context
import android.content.Intent
import android.media.MediaPlayer
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.transition.AutoTransition
import android.transition.TransitionManager
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.*
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.pytapplication.fragments.SortFragment
import com.example.pytapplication.models.Post
import com.example.pytapplication.models.User
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.core.OrderBy
import com.google.firebase.storage.FirebaseStorage
import kotlinx.android.synthetic.main.activity_posts.*
import kotlinx.android.synthetic.main.item_post.*
import java.util.*

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
        bottomNavigation()

        val recyclerView = findViewById<RecyclerView>(R.id.recyclerViewPosts)
        recyclerView.addItemDecoration(DividerItemDecoration(this, LinearLayoutManager.VERTICAL))

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

    //Bottom navigation
    fun bottomNavigation (){

        bottomnavigation.setOnNavigationItemSelectedListener { item ->
            when (item.itemId) {
                R.id.home -> {
                    val intent = Intent(this, PostsActivity::class.java)
                    startActivity(intent)
                    return@setOnNavigationItemSelectedListener true
                }
                R.id.add -> {
                    val intent = Intent(this, CreatePostActivity::class.java)
                    startActivity(intent)
                    return@setOnNavigationItemSelectedListener true
                }
                R.id.filter -> sortFragment()
            }
            false

        }
    }

    //Sort fragment
    fun sortFragment() {
        val sortFragment = SortFragment()
        val transaktion = supportFragmentManager.beginTransaction()
        transaktion.add(R.id.fl_wrapper, sortFragment, "sortFragment")
        transaktion.commit()

    }

    //Updates the list after the user makes a selection in the fragment
    fun updatedata (sortedPosts:MutableList<Post>){
        posts.clear()
        posts.addAll(sortedPosts)
        adapter.notifyDataSetChanged()
    }
}


