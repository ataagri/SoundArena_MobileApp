package com.example.cs308_mobileapplication

import android.content.Intent
import android.os.Bundle
import android.util.Patterns
import android.view.View
import android.widget.TextView
import androidx.activity.ComponentActivity
import com.google.android.material.button.MaterialButton
import com.google.android.material.textfield.TextInputEditText
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.Body
import retrofit2.http.POST
import retrofit2.http.PUT


//Creating Retrofit INSTANCE
interface UserService {
    @PUT("signup")
    fun registerUser(@Body user: User): Call<SignupResponse>

    @POST("login")
    fun loginUser(@Body user: User): Call<LoginResponse>


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


data class LoginResponse(
    val token: String,
    val userId: String
)

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
    }

}


