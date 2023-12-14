package com.example.cs308_mobileapplication.data

import com.google.gson.annotations.SerializedName

data class Song(
    @SerializedName("_id")
    val id: String,
    val title: String,
    val performer: List<Performer>,
    val album: Album?,
    val genre: String,
    val userRating: Int?
)