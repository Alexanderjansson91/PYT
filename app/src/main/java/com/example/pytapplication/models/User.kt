package com.example.pytapplication.models

import com.google.firebase.firestore.PropertyName

data class User(
    var username: String = " ",
    @get:PropertyName("profileimage_url") @set:PropertyName("profileimage_url") var imageuUrl: String = ""

    )