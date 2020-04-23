package com.example.pytapplication

import android.content.Context
import android.text.format.DateUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.view.menu.ActionMenuItemView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.pytapplication.models.Post

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
    }

    //inner class for my viewhodler
    inner class ViewHolder (itemView: View) : RecyclerView.ViewHolder(itemView) {

            val textViewProfilName = itemView.findViewById<TextView>(R.id.postUsername)
            val textViewTrack = itemView.findViewById<TextView>(R.id.postTrackname)
            val textViewName = itemView.findViewById<TextView>(R.id.postArtistname)
            val textViewTime = itemView.findViewById<TextView>(R.id.postTime)
            var PostImage = itemView.findViewById<ImageView>(R.id.postImage)

        //function to bind a post
        fun bind(post: Post){
            textViewProfilName.text = post.user?.username
            textViewTrack.text = post.trackname
            textViewName.text = post.artistname
            Glide.with(context).load(post.imageuUrl).into(PostImage)
            textViewTime.text = DateUtils.getRelativeTimeSpanString(post.creationTimems)
        }
}

}