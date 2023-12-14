package com.example.cs308_mobileapplication.utils

import android.util.Log
import com.example.cs308_mobileapplication.data.Song
import com.google.gson.Gson
import com.google.gson.JsonObject

fun parseSongs(jsonResponse: String): List<Song> {
    val gson = Gson()
    try {
        val jsonObject = gson.fromJson(jsonResponse, JsonObject::class.java)
        val jsonSongsArray = jsonObject.getAsJsonArray("songs")  // Extract the "songs" array

        val songs = mutableListOf<Song>()
        jsonSongsArray.forEach { jsonElement ->
            val song = gson.fromJson(jsonElement, Song::class.java)
            songs.add(song)
        }

        return songs
    } catch (e: Exception) {
        Log.e("parseSongs", "Error parsing songs: ${e.message}")
        return emptyList()  // Return an empty list in case of error
    }
}

fun parseFavouriteSongs(jsonResponse: String): List<Song> {
    val gson = Gson()
    try {
        val jsonObject = gson.fromJson(jsonResponse, JsonObject::class.java)
        val jsonSongsArray = jsonObject.getAsJsonArray("favoriteSongs")  // Extract the "songs" array

        val songs = mutableListOf<Song>()
        jsonSongsArray.forEach { jsonElement ->
            val song = gson.fromJson(jsonElement, Song::class.java)
            songs.add(song)
        }

        return songs
    } catch (e: Exception) {
        Log.e("parseSongs", "Error parsing songs: ${e.message}")
        return emptyList()  // Return an empty list in case of error
    }


}

fun parseRecommendedSongs(jsonResponse: String): List<Song> {
    val gson = Gson()
    try {
        val jsonObject = gson.fromJson(jsonResponse, JsonObject::class.java)
        val jsonSongsArray = jsonObject.getAsJsonArray("recommendedSongs")  // Extract the "songs" array

        val songs = mutableListOf<Song>()
        jsonSongsArray.forEach { jsonElement ->
            val song = gson.fromJson(jsonElement, Song::class.java)
            songs.add(song)
        }

        return songs
    } catch (e: Exception) {
        Log.e("parseSongs", "Error parsing songs: ${e.message}")
        return emptyList()  // Return an empty list in case of error
    }


}