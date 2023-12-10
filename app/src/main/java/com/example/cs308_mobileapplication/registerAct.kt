package com.example.cs308_mobileapplication

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.os.PersistableBundle
import android.util.Log
import android.util.Patterns
import android.view.View
import android.widget.Button
import android.widget.RatingBar
import android.widget.Spinner
import android.widget.TextView
import androidx.activity.ComponentActivity
import androidx.compose.foundation.interaction.PressInteraction
import androidx.core.view.get
import com.google.android.material.button.MaterialButton
import com.google.android.material.textfield.TextInputEditText
import okhttp3.OkHttpClient
import okhttp3.ResponseBody
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
import kotlin.math.log


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

    @GET("/")
    fun getSongs(): Call<List<Song>>


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
    val name: String,
    val performer: String,
    val album: String
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
fun saveUserId(context: Context, userToken: String){
    val sharedPreferences = context.getSharedPreferences("MySharedPref",Context.MODE_PRIVATE)
    val editor = sharedPreferences.edit()
    editor.putString("token","Bearer " + userToken)
    editor.apply()
}

fun getUserId(context: Context): String? {
    val sharedPreferences = context.getSharedPreferences("MySharedPref", Context.MODE_PRIVATE)
    return sharedPreferences.getString("token", null)
}

fun clearUserId(context: Context) {
    val sharedPreferences = context.getSharedPreferences("MySharedPref", Context.MODE_PRIVATE)
    val editor = sharedPreferences.edit()
    editor.remove("userId")
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
                        saveUserId(this@LoginAct, it.token)
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
            clearUserId(this@Mainpage)
            startActivity(toLogin)
        }

        allSongsButton.setOnClickListener {
            val toAllSongs = Intent(this, Allsongs::class.java)
            startActivity(toAllSongs)
        }

    }



}

class Mysongs : ComponentActivity(){

    override fun onCreate(savedInstanceState: Bundle?){
        super.onCreate(savedInstanceState)
        setContentView(R.layout.mysongs)

        val mainMenuButton = findViewById<Button>(R.id.mainMenuButton)

        mainMenuButton.setOnClickListener{
            val toMainpage = Intent(this, Mainpage::class.java)
            startActivity(toMainpage)
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
            val userToken = getUserId(this@Entermanually).toString()

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

class Allsongs : ComponentActivity(){
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.allsongs)
        RetrofitClient.instance.getSongs().enqueue(object : Callback<List<Song>>{
            override fun onResponse(call: Call<List<Song>>, response: Response<List<Song>>) {
                if (response.isSuccessful) {

                    val songs = response.body()
                    songs?.let {

                        Log.d("AddSongSuccess", "Song added successfully: ${songs}")
                    }

                }else{
                    val errorResponse = response.errorBody()?.string()
                    Log.e("Error", "Error Listing Songs: $errorResponse")
                }
            }

            override fun onFailure(call: Call<List<Song>>, t: Throwable) {
                // Handle failure
            }
        })

    }
}






