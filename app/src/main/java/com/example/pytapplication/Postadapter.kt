package com.example.pytapplication

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.media.AudioManager
import android.media.MediaPlayer
import android.net.Uri
import android.os.Handler
import android.os.Message
import android.text.format.DateUtils
import android.text.method.TextKeyListener.clear
import android.transition.AutoTransition
import android.transition.TransitionManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.cardview.widget.CardView
import androidx.core.content.ContextCompat.startActivity
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.pytapplication.models.Post
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference


lateinit var activity: Activity
var selectedPosition: Int = -1
private const val TAG = "MyMessage"
//val postActivity = PostsActivity()
private var postActivity: PostsActivity? = null
private lateinit var mp: MediaPlayer
private var totalTime: Int = 0
private var post: Post? = null
private lateinit var storageRef: StorageReference

class Postadapter(val context: Context, val posts: List<Post>) :
    RecyclerView.Adapter<Postadapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(context).inflate(R.layout.item_post, parent, false)
        storageRef = FirebaseStorage.getInstance().reference

        return ViewHolder(view)

    }

    //Size of recyclerView
    override fun getItemCount() = posts.size

    //Bind post to my recyclerView
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(posts[position])

        if (selectedPosition == position) {
            holder.itemView.setBackgroundColor(Color.parseColor("#52027171"))
            holder.btn?.visibility = View.VISIBLE

        } else {
            holder.itemView.setBackgroundColor(Color.parseColor("#ffffff"))
            holder.btn?.visibility = View.GONE

        }
        holder.itemView.setOnClickListener {

            if (holder.itemView.isPressed) {
                playMusic()
                selectedPosition = position
                notifyDataSetChanged()
                Toast.makeText(context, "You play # ${position + 1}", Toast.LENGTH_SHORT).show()

            } else if (holder.itemView.equals(false)) {
                stopMusic()
            }
        }
    }

    //inner class for my viewhodler
    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val btn = itemView.findViewById<Button?>(R.id.show_Song_Info)
        val textViewProfilName = itemView.findViewById<TextView>(R.id.postUsername)
        val textViewTrack = itemView.findViewById<TextView>(R.id.postTrackname)
        val textViewName = itemView.findViewById<TextView>(R.id.postArtistname)
        val textViewTime = itemView.findViewById<TextView>(R.id.postTime)
        var PostImage = itemView.findViewById<ImageView>(R.id.postImage)
        val genreTextView = itemView.findViewById<TextView>(R.id.genre)
        val spotifyLinkBtn = itemView.findViewById<ImageButton?>(R.id.button_Spotify)
        val seekBar = itemView.findViewById<SeekBar>(R.id.seekBar)
        val shareSongBtn = itemView.findViewById<ImageButton>(R.id.share_Button)
        //val playMusicButton = itemView.findViewById<ImageButton>(R.id.play_Button)

        //function to bind a post
        fun bind(post: Post) {
            textViewProfilName.text = post.user?.username
            textViewTrack.text = post.trackname
            textViewName.text = post.artistname
            genreTextView.text = post.genre
            Glide.with(context).load(post.imageuUrl).into(PostImage)
            textViewTime.text = DateUtils.getRelativeTimeSpanString(post.creationTimems)



            spotifyLinkBtn?.setOnClickListener {
                var url: String?
                url = post?.spotify.toString()
                val i = Intent(Intent.ACTION_VIEW)
                i.data = Uri.parse(url)
                startActivity(context, i, null)
                println(url)
            }
            shareSongBtn.setOnClickListener {
                val sendIntent = Intent()
                sendIntent.action = Intent.ACTION_SEND
                sendIntent.putExtra(
                    Intent.EXTRA_TEXT,
                    "Hey Check out this Great app:"
                )
                sendIntent.type = "text/plain"
                startActivity(context, sendIntent, null)
            }
            //Glide.with(context).load(post.audioUrl).into(playMusicButton)
        }

        init {

            itemView.setOnClickListener {
                val position: Int = adapterPosition
                Toast.makeText(itemView.context, "You play # ${position + 1}", Toast.LENGTH_SHORT).show()
                playMusic()
            }

            val viewSongbtn = itemView.findViewById<Button?>(R.id.show_Song_Info)
            val expandableLayout = itemView.findViewById<View>(R.id.expandableLayout)
            val cardView = itemView.findViewById<CardView>(R.id.cardView)

            viewSongbtn?.setOnClickListener {
                if (expandableLayout.visibility == View.GONE) {
                    TransitionManager.beginDelayedTransition(cardView, AutoTransition())
                    expandableLayout.visibility = View.VISIBLE
                    viewSongbtn.text = "Hide"
                } else {
                    TransitionManager.beginDelayedTransition(cardView, AutoTransition())
                    expandableLayout.visibility = View.GONE
                    viewSongbtn.text = "Show"
                }
            }
        }
    }

    fun playMusic() {

               val storage = FirebaseStorage.getInstance()
        storage.reference.child("audios/1589459245317-audio.wav").downloadUrl.addOnSuccessListener({
            val mediaPlayer = MediaPlayer()
            mediaPlayer.setDataSource(it.toString())
            mediaPlayer.setOnPreparedListener { player ->
                player.start()
            }
            mediaPlayer.prepareAsync()
        })

    }
    fun stopMusic() {
      mp.pause()
    }
}

