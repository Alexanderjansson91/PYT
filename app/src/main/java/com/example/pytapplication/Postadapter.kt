package com.example.pytapplication

import android.app.Activity
import android.content.Context
import android.graphics.Color
import android.text.format.DateUtils
import android.transition.AutoTransition
import android.transition.TransitionManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.Glide.init
import com.example.pytapplication.models.Post
import kotlinx.android.synthetic.main.item_post.*


lateinit var activity: Activity
var selectedPosition: Int = -1
private const val TAG = "MyMessage"

class Postadapter(val context: Context,val posts: List<Post>) :
    RecyclerView.Adapter<Postadapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(context).inflate(R.layout.item_post, parent, false)
        return ViewHolder(view)

    }
    //Size of recyclerView
    override fun getItemCount() = posts.size

    //Bind post to my recyclerView
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(posts[position])

        if (selectedPosition == position) {
            holder.itemView.setBackgroundColor(Color.parseColor("#52027171"))
            holder.btn?.visibility=View.VISIBLE

        }else {
            holder.itemView.setBackgroundColor(Color.parseColor("#ffffff"))
            holder.btn?.visibility=View.GONE

        }
        holder.itemView.setOnClickListener {
            selectedPosition = position
            notifyDataSetChanged()
            Toast.makeText(context,"You play # ${position + 1}", Toast.LENGTH_SHORT).show()
        }
    }

    //inner class for my viewhodler
    inner class ViewHolder (itemView: View) : RecyclerView.ViewHolder(itemView) {
                val btn = itemView.findViewById<Button?>(R.id.show_Song_Info)
            //val textViewProfilName = itemView.findViewById<TextView>(R.id.postUsername)
            val textViewTrack = itemView.findViewById<TextView>(R.id.postTrackname)
            val textViewName = itemView.findViewById<TextView>(R.id.postArtistname)
            val textViewTime = itemView.findViewById<TextView>(R.id.postTime)
            var PostImage = itemView.findViewById<ImageView>(R.id.postImage)
            //val genreTextView = itemView.findViewById<TextView>(R.id.genre)
            //val playMusicButton = itemView.findViewById<ImageButton>(R.id.play_Button)

        //function to bind a post
        fun bind(post: Post){
            //textViewProfilName.text = post.user?.username
            textViewTrack.text = post.trackname
            textViewName.text = post.artistname
            //genreTextView.text = post.genre
            Glide.with(context).load(post.imageuUrl).into(PostImage)
            textViewTime.text = DateUtils.getRelativeTimeSpanString(post.creationTimems)
            //Glide.with(context).load(post.audioUrl).into(playMusicButton)
        }

        init {
            itemView.setOnClickListener {
                val position: Int = adapterPosition
                Toast.makeText(itemView.context,"You play # ${position + 1}", Toast.LENGTH_SHORT).show()
            }

            val viewSongbtn = itemView.findViewById<Button?>(R.id.show_Song_Info)
            val expandableLayout = itemView.findViewById<View>(R.id.expandableLayout)
            val cardView = itemView.findViewById<CardView>(R.id.cardView)

            viewSongbtn?.setOnClickListener {
                if (expandableLayout.visibility == View.GONE) {
                    TransitionManager.beginDelayedTransition(cardView, AutoTransition())
                    expandableLayout.visibility = View.VISIBLE
                    viewSongbtn.text = "Exit"
                } else {
                    TransitionManager.beginDelayedTransition(cardView, AutoTransition())
                    expandableLayout.visibility = View.GONE
                    viewSongbtn.text = "View"
                }
            }
        }
    }
}