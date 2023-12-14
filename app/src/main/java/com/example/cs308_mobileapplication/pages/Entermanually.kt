package com.example.cs308_mobileapplication.pages

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.Spinner
import android.widget.TextView
import androidx.activity.ComponentActivity
import com.example.cs308_mobileapplication.R
import com.example.cs308_mobileapplication.data.AddSongResponse
import com.example.cs308_mobileapplication.data.SongData
import com.example.cs308_mobileapplication.network.RetrofitClient
import com.example.cs308_mobileapplication.utils.getUserToken
import com.google.android.material.textfield.TextInputEditText
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class Entermanually : ComponentActivity(){

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.entermanually)

        val mainPageButton = findViewById<Button>(R.id.mainMenuButton)
        mainPageButton.setOnClickListener {
            val toMainpage = Intent(this, Mainpage::class.java)
            startActivity(toMainpage)
        }

        val songNameBox = findViewById<TextInputEditText>(R.id.songName)
        val artistNameBox = findViewById<TextInputEditText>(R.id.artistName)
        val albumNameBox = findViewById<TextInputEditText>(R.id.albumName)
        val genreSpinner = findViewById<Spinner>(R.id.genreSpinner)
        val ratingSpinner = findViewById<Spinner>(R.id.ratingSpinner)
        val addSongButton = findViewById<Button>(R.id.addSong)
        val songError = findViewById<TextView>(R.id.nameError)
        val artistError = findViewById<TextView>(R.id.artistError)
        val albumError = findViewById<TextView>(R.id.albumError)
        val genreError = findViewById<TextView>(R.id.genreError)
        var addSongBool: Boolean = true


        addSongButton.setOnClickListener {
            addSongBool = true
            songError.visibility = View.GONE
            artistError.visibility = View.GONE
            albumError.visibility = View.GONE
            genreError.visibility = View.GONE

            var songName = songNameBox.text.toString()
            if (songName == ""){
                songError.visibility = View.VISIBLE
                addSongBool = false

            }
            var artistName = artistNameBox.text.toString()
            if(artistName == ""){
                artistError.visibility = View.VISIBLE
                addSongBool = false
            }
            var albumName = albumNameBox.text.toString()
            if(albumName == ""){
                albumError.visibility = View.VISIBLE
                addSongBool = false
            }
            var genre = genreSpinner.selectedItem.toString()
            if (genre == "Select Genre"){
                genreError.visibility = View.VISIBLE
                addSongBool = false
            }
            var rating: String = ratingSpinner.selectedItem.toString()
            var postRating: Int?
            if(rating == "Rate"){
                postRating = null
            }else{
                postRating = rating.toInt()
            }
            val userToken = getUserToken(this@Entermanually).toString()

            if(addSongBool){
                var songData = SongData(
                    title = songName,
                    performer = artistName.split(", "),
                    album = albumName,
                    rating = postRating,
                    genre = genre
                )
                RetrofitClient.instance.postAddSong(userToken,songData).enqueue(object :
                    Callback<AddSongResponse> {
                    override fun onResponse(call: Call<AddSongResponse>, response: Response<AddSongResponse>) {
                        if (response.isSuccessful) {

                            val addSongResponse = response.body()
                            addSongResponse?.let {

                                Log.d("AddSongSuccess", "Song added successfully: ${it.message}")
                            }

                        } else {

                            val errorResponse = response.errorBody()?.string()
                            Log.e("AddSongError", "Error adding song: $errorResponse")
                        }
                    }

                    override fun onFailure(call: Call<AddSongResponse>, t: Throwable) {
                        // no internet
                        Log.e("AddSongFailure", "Network failure or systemic error: ${t.message}")
                    }

                })
            }



        }
    }
}