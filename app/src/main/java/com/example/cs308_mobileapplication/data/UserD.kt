package com.example.cs308_mobileapplication.data

import com.google.gson.annotations.SerializedName

data class UserD(
    @SerializedName("_id")
    val id: String,
    val email: String,
    val friends: List<String>,
)