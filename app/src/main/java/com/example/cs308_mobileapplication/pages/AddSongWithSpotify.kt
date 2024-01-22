package com.example.cs308_mobileapplication.pages

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.TextView
import androidx.activity.ComponentActivity
import com.example.cs308_mobileapplication.R
import com.example.cs308_mobileapplication.data.Song
import com.example.cs308_mobileapplication.network.RetrofitClient
import com.google.android.material.textfield.TextInputEditText
import okhttp3.MediaType
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


class AddSongWithSpotify: ComponentActivity(){
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.spotifyadd)

        val failureText = findViewById<TextView>(R.id.failureAdd)
        val successText = findViewById<TextView>(R.id.successAdd)
        val songBox = findViewById<TextInputEditText>(R.id.spotifyTextBox)
        val addButton = findViewById<Button>(R.id.addSpotifyButton)
        val backButton = findViewById<Button>(R.id.backButton)
        backButton.setOnClickListener {
            val toAddSong = Intent(this, Addsongs::class.java)
            startActivity(toAddSong)
        }
        addButton.setOnClickListener {
            failureText.visibility = View.GONE
            successText.visibility = View.GONE
            val songName = songBox.text.toString()
            if (songName.isNotEmpty()) {
                addSong(songName)
            } else {
                // Handle empty input
                failureText.visibility = View.VISIBLE
            }
        }
    }
    private fun addSong(songName: String) {
        val failureText = findViewById<TextView>(R.id.failureAdd)
        val successText = findViewById<TextView>(R.id.successAdd)
        val json = JSONObject()
        json.put("trackName", songName)
        val requestBody = json.toString().toRequestBody("application/json".toMediaTypeOrNull())
        RetrofitClient.instance.searchAndAddTrack(requestBody).enqueue(object : Callback<Song> {
            override fun onResponse(call: Call<Song>, response: Response<Song>) {
                if (response.isSuccessful && response.code() == 201) {
                    Log.d("AddSongWithSpotify", "Song added successfully")
                    successText.visibility = View.VISIBLE
                    failureText.visibility = View.GONE
                } else {
                    Log.d("AddSongWithSpotify", "Failed to add song: ${response.errorBody()?.string()}")
                    failureText.visibility = View.VISIBLE
                    successText.visibility = View.GONE
                }
            }

            override fun onFailure(call: Call<Song>, t: Throwable) {
                //XD
            }
        })
    }

}