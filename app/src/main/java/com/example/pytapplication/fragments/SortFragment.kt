package com.example.pytapplication.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*

import com.example.pytapplication.R
import com.example.pytapplication.models.Post
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query

/**
 * A simple [Fragment] subclass.
 */
class SortFragment : Fragment() {

    var post : Post? = null
    lateinit var  firestoreDB : FirebaseFirestore
    val options = arrayOf("Pop","Rock","trap","Hiphop","rap","house","Electronic" )

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val t= inflater.inflate(R.layout.fragment_sort2, container, false)
        firestoreDB = FirebaseFirestore.getInstance()
        val spinner = t.findViewById<Spinner?>(R.id.sort_Spinner)
        val close = t.findViewById<ImageButton>(R.id.close_Sort_Fragment)
        close.setOnClickListener {
            fragmentManager?.beginTransaction()?.remove(this)?.commit()
        }
        spinner?.adapter = activity?.applicationContext?.let { ArrayAdapter(it, R.layout.support_simple_spinner_dropdown_item, options) } as SpinnerAdapter
        spinner?.onItemSelectedListener = object :AdapterView.OnItemSelectedListener{

            override fun onNothingSelected(parent: AdapterView<*>?) {
                println("error")
            }
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                //firestoreDB.collection("posts").orderBy("genre")
                firestoreDB.collection("posts")
                    .whereEqualTo("genre",id)
                    .get().addOnSuccessListener {
                        println(it.documents)
                    }
                val type = parent?.getItemAtPosition(position).toString()
                Toast.makeText(activity,type, Toast.LENGTH_LONG).show()
            }
        }
        return t
    }
}


