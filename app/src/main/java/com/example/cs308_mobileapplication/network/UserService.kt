package com.example.cs308_mobileapplication.network

import com.example.cs308_mobileapplication.data.AddFriendResponse
import com.example.cs308_mobileapplication.data.AddSongResponse
import com.example.cs308_mobileapplication.data.GenreRequest
import com.example.cs308_mobileapplication.data.LikeSongResponse
import com.example.cs308_mobileapplication.data.LoginResponse
import com.example.cs308_mobileapplication.data.RateSongResponse
import com.example.cs308_mobileapplication.data.RatingData
import com.example.cs308_mobileapplication.data.SignupResponse
import com.example.cs308_mobileapplication.data.SongData
import com.example.cs308_mobileapplication.data.User
import com.example.cs308_mobileapplication.data.friendMailBody
import com.google.gson.JsonObject
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path

//Creating Retrofit INSTANCE
interface UserService {
    @PUT("signup")
    fun registerUser(@Body user: User): Call<SignupResponse>

    @POST("login")
    fun loginUser(@Body user: User): Call<LoginResponse>

    @POST("admin/add-song")
    fun postAddSong(
        @Header("Authorization") authToken: String,
        @Body songData: SongData
    ): Call<AddSongResponse>

    @GET("/songs")
    fun getSongs(
        @Header("Authorization") authToken: String
    ): Call<JsonObject>


    @GET("/getliked-songs/{userId}")
    fun getLikedSongs(
        @Path("userId") userId: String,
        @Header("Authorization") authToken: String
    ): Call<JsonObject>


    @PUT("rate-song/{songId}")
    fun rateSong(
        @Path("songId") songId: String,
        @Header("Authorization") authToken: String,
        @Body ratingData: RatingData
    ): Call<RateSongResponse>

    @POST("like-song/{songId}")
    fun likeSong(
        @Path("songId") songId : String,
        @Header("Authorization") authToken: String
    ): Call<LikeSongResponse>

    @GET("/getusers")
    fun getUsers(): Call<JsonObject>

    @POST("/addfriend")
    fun addFriend(
        @Header("Authorization")authToken: String,
        @Body friendMail: friendMailBody
    ): Call<AddFriendResponse>

    @POST("/favorite-song-genre")
    fun getFavouriteSongsByGenre(
        @Header("Authorization") authToken: String,
        @Body requestBody: GenreRequest
    ): Call<JsonObject>

    @GET("/recommendation")
    fun recommendSongs(
        @Header("Authorization") authToken: String
    ): Call<JsonObject>


}