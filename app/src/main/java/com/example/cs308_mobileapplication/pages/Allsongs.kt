package com.example.cs308_mobileapplication.pages

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.LinearLayout
import android.widget.Spinner
import android.widget.TextView
import androidx.activity.ComponentActivity
import com.example.cs308_mobileapplication.R
import com.example.cs308_mobileapplication.data.RateSongResponse
import com.example.cs308_mobileapplication.data.RatingData
import com.example.cs308_mobileapplication.data.Song
import com.example.cs308_mobileapplication.network.RetrofitClient
import com.example.cs308_mobileapplication.utils.getUserToken
import com.example.cs308_mobileapplication.utils.parseSongs
import com.google.gson.JsonObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class Allsongs : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.allsongsnew)
        val mainMenuButton = findViewById<Button>(R.id.mainMenuButton)
        mainMenuButton.setOnClickListener{
            val toMainPage = Intent(this, Mainpage::class.java)
            startActivity(toMainPage)
        }
        var userId : String = getUserToken(this@Allsongs).toString()
        RetrofitClient.instance.getSongs(userId).enqueue(object : Callback<JsonObject> {
            override fun onResponse(call: Call<JsonObject>, response: Response<JsonObject>) {
                if (response.isSuccessful) {
                    val jsonResponse = response.body().toString()
                    val songList = parseSongs(jsonResponse)
                    Log.d("XDD", songList.get(1).title)
                    addSongsToView(songList) // Call this method to add views
                }
            }

            override fun onFailure(call: Call<JsonObject>, t: Throwable) {
                Log.e("Allsongs", "Network error: ${t.message}")
            }
        })
    }

    private fun addSongsToView(songs: List<Song>) {
        val container: LinearLayout = findViewById(R.id.allSongsContainer)
        for (i in songs.indices) {
            val song = songs[i]
            val songView = LayoutInflater.from(this).inflate(R.layout.song_view, container, false)
            val nameTextView: TextView = songView.findViewById(R.id.name)
            val artistsTextView: TextView = songView.findViewById(R.id.artists)
            val albumTextView: TextView = songView.findViewById(R.id.album)
            val genreTextView: TextView = songView.findViewById(R.id.genre)
            val ratingSpinner: Spinner = songView.findViewById(R.id.rating)

            // Set the song details
            nameTextView.text = song.title
            artistsTextView.text = song.performer.joinToString { performer -> performer.name }
            albumTextView.text = song.album?.name ?: "No Album"
            genreTextView.text = song.genre
            setupRatingSpinner(ratingSpinner, song.userRating)
            ratingSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
                override fun onItemSelected(parent: AdapterView<*>, view: View, position: Int, id: Long) {
                    val selectedRating = parent.getItemAtPosition(position).toString().toIntOrNull()
                    selectedRating?.let {
                        // Call the Retrofit method to rate the song
                        rateSong(song.id, it)
                    }
                }

                override fun onNothingSelected(parent: AdapterView<*>) {}
            }
            container.addView(songView)

            if (i < songs.size - 1) {
                val dividerView = LayoutInflater.from(this)
                    .inflate(R.layout.divider, container, false)
                container.addView(dividerView)
            }
        }
    }

    private fun rateSong(songId: String, rating: Int) {
        val authToken = getUserToken(this) // Retrieve the stored auth token
        val ratingData = RatingData(rating)

        if (authToken != null) {
            RetrofitClient.instance.rateSong(songId, authToken, ratingData).enqueue(object :
                Callback<RateSongResponse> {
                override fun onResponse(call: Call<RateSongResponse>, response: Response<RateSongResponse>) {
                    if (response.isSuccessful) {

                    } else {

                    }
                }

                override fun onFailure(call: Call<RateSongResponse>, t: Throwable) {
                    // Handle network failure or systemic error
                }
            })
        }

    }
    private fun setupRatingSpinner(spinner: Spinner, rating: Int?) {
        val ratingAdapter = ArrayAdapter.createFromResource(
            this,
            R.array.rating_numbers,
            android.R.layout.simple_spinner_item
        ).also { adapter ->
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            spinner.adapter = adapter
        }

        rating?.let {
            val ratingPosition = ratingAdapter.getPosition(it.toString())
            spinner.setSelection(ratingPosition, true)
        }
    }
}