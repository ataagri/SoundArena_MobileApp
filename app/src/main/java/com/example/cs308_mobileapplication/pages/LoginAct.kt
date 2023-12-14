package com.example.cs308_mobileapplication.pages

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.TextView
import androidx.activity.ComponentActivity
import com.example.cs308_mobileapplication.R
import com.example.cs308_mobileapplication.data.LoginResponse
import com.example.cs308_mobileapplication.data.User
import com.example.cs308_mobileapplication.network.RetrofitClient
import com.example.cs308_mobileapplication.utils.getUserId
import com.example.cs308_mobileapplication.utils.saveUserId
import com.example.cs308_mobileapplication.utils.saveUserToken
import com.google.android.material.button.MaterialButton
import com.google.android.material.textfield.TextInputEditText
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

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

        call.enqueue(object: Callback<LoginResponse> {
            override fun onResponse(call: Call<LoginResponse>, response: Response<LoginResponse>){
                if(response.isSuccessful){
                    response.body()?.let {
                        saveUserToken(this@LoginAct, it.token)
                        saveUserId(this@LoginAct, it.userId)
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