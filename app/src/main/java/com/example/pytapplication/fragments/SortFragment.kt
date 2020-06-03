package com.example.pytapplication.fragments

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.pytapplication.Postadapter
import com.example.pytapplication.PostsActivity
import com.example.pytapplication.R

import com.example.pytapplication.models.Post
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import kotlinx.android.synthetic.main.activity_posts.*


/**
 * A simple [Fragment] subclass.
 */
private const val TAG = "sortFragment"

class SortFragment : Fragment() {

    var post : Post? = null
    lateinit var  firestoreDB : FirebaseFirestore
    val options = arrayOf("Pick a Option","Genre" )
    //private lateinit var posts : MutableList<Post>
    //private var recyclerView : RecyclerView? = null
    private var recyclerView : RecyclerView? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        Log.i(TAG, "Post ${post}")
        // Inflate the layout for this fragment
        val t= inflater.inflate(R.layout.fragment_sort2, container, false)

        recyclerView = t.findViewById(R.id.recyclerViewPosts)


        firestoreDB = FirebaseFirestore.getInstance()
        val spinner = t.findViewById<Spinner?>(R.id.sort_Spinner)
        val closeFragment = t.findViewById<Button>(R.id.Close_fragment)

        //close fragment
        closeFragment.setOnClickListener {
            fragmentManager?.beginTransaction()?.remove(this)?.commit()
        }

        spinner?.adapter = activity?.applicationContext?.let { ArrayAdapter(it, R.layout.style_spinner, options) } as SpinnerAdapter
        spinner?.onItemSelectedListener = object :AdapterView.OnItemSelectedListener{

            override fun onNothingSelected(parent: AdapterView<*>?) {
                println("error")
            }

            //selected chooice from the spinner and change word on button
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                val type = parent?.getItemAtPosition(position).toString()
                if (type.equals("Genre")){
                    closeFragment.text = "Done"
                    sortPosts()
                }       else{
                    closeFragment.text = "Hide"
                }
            }
        }
        return t
    }

    //Sort Posts by genre
    fun sortPosts(){
        firestoreDB.collection("posts").orderBy("genre").get().addOnSuccessListener {
            val postList = it.toObjects(Post::class.java)
            val posts = mutableListOf<Post>()
            posts.addAll(postList)
            (activity as PostsActivity).updatedata(posts)
            //recyclerView?.adapter?.notifyDataSetChanged()
            Log.i(TAG, "recyclerView ${recyclerView}")
            for (post in postList){
                Log.i(TAG, "Post ${post}")
            }
        }
    }
}


