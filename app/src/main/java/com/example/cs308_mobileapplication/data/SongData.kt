package com.example.cs308_mobileapplication.data

data class SongData(
    val title: String,
    val genre: String?,
    val album: String,
    val performer: List<String>,
    val rating: Int?
)