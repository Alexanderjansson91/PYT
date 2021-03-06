package com.example.pytapplication.models

import com.google.firebase.firestore.PropertyName

data class Post(
    var artistname: String ="",
    var trackname: String ="",
    //@get:PropertyName("image_url") @set:PropertyName("image_url") var imageuUrl: String = "",
    @get:PropertyName("creation_time_ms") @set:PropertyName("creation_time_ms") var creationTimems: Long = 0,
    var user: User? = null,
    @get:PropertyName("audio_url") @set:PropertyName("audio_url") var audioUrl: String = " ",
    var genre: String ="",
    var spotify: String = "",
    var instagram: String = "",
    var facebook: String = "",
    var soundcloud: String = ""
)