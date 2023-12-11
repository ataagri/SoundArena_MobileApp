package com.example.cs308_mobileapplication

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.util.Patterns
import android.view.LayoutInflater
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.LinearLayout
import android.widget.Spinner
import android.widget.TextView
import androidx.activity.ComponentActivity
import com.google.android.material.button.MaterialButton
import com.google.android.material.textfield.TextInputEditText
import com.google.gson.Gson
import com.google.gson.JsonObject
import com.google.gson.annotations.SerializedName
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
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

}
object RetrofitClient {
    private const val SERVER_URL = "http://10.0.2.2:3000/"

    private val loggingInterceptor = HttpLoggingInterceptor().apply {
        level = HttpLoggingInterceptor.Level.BODY
    }

    private val client = OkHttpClient.Builder()
        .addInterceptor(loggingInterceptor)
        .build()

    val instance: UserService by lazy {
        val retrofit = Retrofit.Builder()
            .baseUrl(SERVER_URL)
            .client(client)
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        retrofit.create(UserService::class.java)
    }
}

data class User(
    val email: String,
    val password: String
    // No changes needed here
)

data class SignupResponse(
    val userId: String
)
data class Song(
    @SerializedName("_id")
    val id: String,
    val title: String,
    val performer: List<Performer>,
    val album: Album,
    val genre: String,
    val userRating: Int?
)


data class Performer(
    val name: String
)

data class Album(
    val name: String
)

data class RatingData(
    val rating: Int
)

data class RateSongResponse(
    val message: String,
    val song: Song
)
data class LikeSongResponse(
    val message: String
)



data class LoginResponse(
    val token: String,
    val userId: String
)
data class SongData(
    val title: String,
    val genre: String,
    val album: String,
    val performer: List<String>,
    val rating: Int? = null,
)
data class AddSongResponse(
    val message: String
)
fun saveUserToken(context: Context, userToken: String){
    val sharedPreferences = context.getSharedPreferences("UserPrefs", Context.MODE_PRIVATE)
    val editor = sharedPreferences.edit()
    editor.putString("token", "Bearer $userToken")
    editor.apply()
}

fun getUserToken(context: Context): String? {
    val sharedPreferences = context.getSharedPreferences("UserPrefs", Context.MODE_PRIVATE)
    return sharedPreferences.getString("token", null)
}

fun saveUserId(context: Context, userId: String){
    val sharedPreferences = context.getSharedPreferences("UserPrefs", Context.MODE_PRIVATE)
    val editor = sharedPreferences.edit()
    editor.putString("Id", userId)
    editor.apply()
}

fun getUserId(context: Context): String?{
    val sharedPreferences = context.getSharedPreferences("UserPrefs", Context.MODE_PRIVATE)
    return sharedPreferences.getString("Id", null) // Corrected key here
}

fun clearUserToken(context: Context) {
    val sharedPreferences = context.getSharedPreferences("UserPrefs", Context.MODE_PRIVATE)
    val editor = sharedPreferences.edit()
    editor.remove("token")
    editor.apply()
}

fun clearUserId(context: Context){
    val sharedPreferences = context.getSharedPreferences("UserPrefs", Context.MODE_PRIVATE)
    val editor = sharedPreferences.edit()
    editor.remove("Id")
    editor.apply()
}



class RegisterAct : ComponentActivity() {
    fun isValidEmail(email: String): Boolean {
        return email.isNotEmpty() && Patterns.EMAIL_ADDRESS.matcher(email).matches()
    }
    fun isValidPassword(password: String): Boolean {
        if (password.length < 8 || password.length > 15) {
            return false
        }
        if (!password.substring(1).contains(Regex("[!@#$%^&*(),.?\":{}|<>]"))) {
            return false
        }
        if (!password.contains(Regex("[A-Z]"))) {
            return false
        }
        if (!password.contains(Regex("[a-z]"))) {
            return false
        }
        if (!password.contains(Regex("[0-9]"))) {
            return false
        }
        return true
    }



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.registeract)
        //Sign In Button Switches to Login Page
        val signInText = findViewById<TextView>(R.id.HaveAcc)
        signInText.setOnClickListener {
            val toSignIn = Intent(this, LoginAct::class.java)
            startActivity(toSignIn)
        }
        val regEmailBox = findViewById<TextInputEditText>(R.id.RegisterEmailTextBox)
        val regPassBox = findViewById<TextInputEditText>(R.id.RegisterPassTextBox)
        val regConfirmPassBox = findViewById<TextInputEditText>(R.id.ConfirmPassTextBox)
        val regButton = findViewById<MaterialButton>(R.id.RegisterButton)
        val invalidMail = findViewById<TextView>(R.id.InvalidRegEmail)
        val invalidPassword = findViewById<TextView>(R.id.InvalidRegPassword)
        val passMatchError = findViewById<TextView>(R.id.passMatchError)
        val userExistError = findViewById<TextView>(R.id.UsedEmail)

        regButton.setOnClickListener {
            var email = regEmailBox.text.toString()
            var iPassword = regPassBox.text.toString()
            var cPassword = regConfirmPassBox.text.toString()
            var regBool = true
            invalidMail.visibility = View.GONE
            invalidPassword.visibility = View.GONE
            passMatchError.visibility = View.GONE
            userExistError.visibility = View.GONE
            if (!isValidEmail(email)) {
                invalidMail.visibility = View.VISIBLE
                regBool = false
            }
            if (!isValidPassword(iPassword)){
                regBool = false
                invalidPassword.visibility = View.VISIBLE
            }
            if (iPassword != cPassword){
                passMatchError.visibility = View.VISIBLE
                regBool = false
            }

            if(regBool){
                val userService = RetrofitClient.instance
                val call = userService.registerUser(User(email,iPassword))

                call.enqueue(object : Callback<SignupResponse> {
                    override fun onResponse(call: Call<SignupResponse>, response: Response<SignupResponse>){
                        if(response.isSuccessful){
                            val toSignIn = Intent  (this@RegisterAct, LoginAct::class.java)
                            toSignIn.putExtra("registeredMail", email)
                            startActivity(toSignIn)
                        }else{
                            userExistError.visibility = View.VISIBLE
                        }
                    }
                    override fun onFailure(call: Call<SignupResponse>, t: Throwable) {
                        // Handle error
                    }
                })
            }

        }


    }
}

class LoginAct : ComponentActivity() {
    private lateinit var noUser: TextView
    private lateinit var wrongPass: TextView
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.loginact)

        val emailBox = findViewById<TextInputEditText>(R.id.LoginEmailTextBox)
        val passwordBox = findViewById<TextInputEditText>(R.id.LoginPassTextBox)
        val signUpText = findViewById<TextView>(R.id.SignUp)
        val loginButton = findViewById<MaterialButton>(R.id.LoginButton)
        noUser = findViewById<TextView>(R.id.InvalidLogEmail)
        wrongPass = findViewById<TextView>(R.id.InvalidLogPassword)


        val registeredMail = intent.getStringExtra("registeredMail")
        if(registeredMail != null){
            emailBox.setText(registeredMail)
        }

        loginButton.setOnClickListener {
            var email = emailBox.text.toString()
            var password = passwordBox.text.toString()

           noUser.visibility = View.GONE
           wrongPass.visibility = View.GONE

            loginUser(email,password)
        }



        signUpText.setOnClickListener {
            val toSignup = Intent(this, RegisterAct::class.java)
            startActivity(toSignup)
        }


    }

    private fun loginUser(email: String, password:String ){
        val user = User(email, password)
        val call = RetrofitClient.instance.loginUser(user)

        call.enqueue(object: Callback<LoginResponse>{
            override fun onResponse(call: Call<LoginResponse>, response: Response<LoginResponse>){
                if(response.isSuccessful){
                    response.body()?.let {
                        saveUserToken(this@LoginAct, it.token)
                        saveUserId(this@LoginAct,it.userId)
                        Log.d("XDDDDDDDD", getUserId(this@LoginAct).toString())
                    }
                    val toMainPage = Intent(this@LoginAct, Mainpage::class.java)
                    startActivity(toMainPage)
                }else{
                    val errorBody = response.errorBody()?.string()

                    val jsonObject = JSONObject(errorBody)
                    val errorMessage = jsonObject.getString("message")

                    when {
                        "email could not be found" in errorMessage -> {
                            noUser.visibility = View.VISIBLE
                            wrongPass.visibility = View.GONE
                        }
                        "Wrong password" in errorMessage -> {
                            wrongPass.visibility = View.VISIBLE
                            noUser.visibility = View.GONE


                        }
                }
            }
            }
            override fun onFailure(call: Call<LoginResponse>, t: Throwable) {
                //No connection
            }
        })

    }

}

class Mainpage : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?){
        super.onCreate(savedInstanceState)
        setContentView(R.layout.mainpage)
        val mySongsButton = findViewById<Button>(R.id.mySongsButton)
        val addSongsButton = findViewById<Button>(R.id.addSongButton)
        val logOutButton = findViewById<Button>(R.id.logOutButton)
        val allSongsButton = findViewById<Button>(R.id.allSongsButton)

        mySongsButton.setOnClickListener{
            val toMySongs = Intent(this, Mysongs::class.java)
            startActivity(toMySongs)
        }

        addSongsButton.setOnClickListener {
            val toAddSong = Intent(this, Addsongs::class.java)
            startActivity(toAddSong)
        }

        logOutButton.setOnClickListener {
            val toLogin = Intent(this, LoginAct::class.java)
            clearUserToken(this@Mainpage)
            startActivity(toLogin)
        }

        allSongsButton.setOnClickListener {
            val toAllSongs = Intent(this, Allsongs::class.java)
            startActivity(toAllSongs)
        }

    }



}

class Mysongs : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.mysongs)
        val mainMenuButton = findViewById<Button>(R.id.mainMenuButton)
        mainMenuButton.setOnClickListener {
            val toMainPage = Intent(this,Mainpage::class.java)
            startActivity(toMainPage)
        }
        var userId : String = getUserToken(this@Mysongs).toString()
        RetrofitClient.instance.getSongs(userId).enqueue(object : Callback<JsonObject> {
            override fun onResponse(call: Call<JsonObject>, response: Response<JsonObject>) {
                if (response.isSuccessful) {
                    val jsonResponse = response.body().toString()
                    val songList = parseSongs(jsonResponse)
                    Log.d("XDD",songList.get(1).title)
                    addSongsToView(songList) // Call this method to add views
                }
            }

            override fun onFailure(call: Call<JsonObject>, t: Throwable) {
                Log.e("Allsongs", "Network error: ${t.message}")            }
        })
    }

    private fun addSongsToView(songs: List<Song>) {
        val container: LinearLayout = findViewById(R.id.mySongsContainer)
        for (i in songs.indices) {
            if (songs[i].userRating != null) {
                val song = songs[i]
                val songView =
                    LayoutInflater.from(this).inflate(R.layout.song_view, container, false)
                val nameTextView: TextView = songView.findViewById(R.id.name)
                val artistsTextView: TextView = songView.findViewById(R.id.artists)
                val albumTextView: TextView = songView.findViewById(R.id.album)
                val genreTextView: TextView = songView.findViewById(R.id.genre)
                val ratingSpinner: Spinner = songView.findViewById(R.id.rating)

                // Set the song details
                nameTextView.text = song.title
                artistsTextView.text = song.performer.joinToString { performer -> performer.name }
                albumTextView.text = song.album.name
                genreTextView.text = song.genre
                setupRatingSpinner(ratingSpinner, song.userRating)
                ratingSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
                    override fun onItemSelected(
                        parent: AdapterView<*>,
                        view: View,
                        position: Int,
                        id: Long
                    ) {
                        val selectedRating =
                            parent.getItemAtPosition(position).toString().toIntOrNull()
                        selectedRating?.let {
                            // Call the Retrofit method to rate the song
                            rateSong(song.id, it)
                        }
                    }

                    override fun onNothingSelected(parent: AdapterView<*>) {}
                }
                container.addView(songView)

                if (i < songs.size - 1) {
                    val dividerView =
                        LayoutInflater.from(this).inflate(R.layout.divider, container, false)
                    container.addView(dividerView)
                }
            }
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
    private fun rateSong(songId: String, rating: Int) {
        val authToken = getUserToken(this) // Retrieve the stored auth token
        val ratingData = RatingData(rating)

        if (authToken != null) {
            RetrofitClient.instance.rateSong(songId, authToken, ratingData).enqueue(object : Callback<RateSongResponse> {
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
    }

class Addsongs : ComponentActivity(){

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.addsongs)

        val enterManuallyButton = findViewById<Button>(R.id.manualButton)

        enterManuallyButton.setOnClickListener {
            val toEnterManually = Intent(this, Entermanually::class.java)
            startActivity(toEnterManually)
        }
    }
}

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
                RetrofitClient.instance.postAddSong(userToken,songData).enqueue(object : Callback<AddSongResponse>{
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
                    Log.d("XDD",songList.get(1).title)
                    addSongsToView(songList) // Call this method to add views
                }
            }

            override fun onFailure(call: Call<JsonObject>, t: Throwable) {
                Log.e("Allsongs", "Network error: ${t.message}")            }
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
            albumTextView.text = song.album.name
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
                val dividerView = LayoutInflater.from(this).inflate(R.layout.divider, container, false)
                container.addView(dividerView)
            }
        }
    }

    private fun rateSong(songId: String, rating: Int) {
        val authToken = getUserToken(this) // Retrieve the stored auth token
        val ratingData = RatingData(rating)

        if (authToken != null) {
            RetrofitClient.instance.rateSong(songId, authToken, ratingData).enqueue(object : Callback<RateSongResponse> {
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
