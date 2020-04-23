package com.example.pytapplication.models

import com.google.firebase.firestore.PropertyName

data class Post(
    var artistname: String ="",
    var trackname: String ="",
    @get:PropertyName("image_url") @set:PropertyName("image_url") var imageuUrl: String = "",
    @get:PropertyName("creation_time_ms") @set:PropertyName("creation_time_ms") var creationTimems: Long = 0,
    var user: User? = null

)