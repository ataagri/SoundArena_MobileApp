package com.example.cs308_mobileapplication.pages

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.widget.Button
import android.widget.LinearLayout
import android.widget.TextView
import androidx.activity.ComponentActivity
import com.example.cs308_mobileapplication.R
import com.example.cs308_mobileapplication.data.AddFriendResponse
import com.example.cs308_mobileapplication.data.UserD
import com.example.cs308_mobileapplication.data.UserResponse
import com.example.cs308_mobileapplication.data.friendMailBody
import com.example.cs308_mobileapplication.network.RetrofitClient
import com.example.cs308_mobileapplication.utils.getUserToken
import com.google.gson.Gson
import com.google.gson.JsonObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class Alluser: ComponentActivity(){
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.allusers)
        val mainMenuButton = findViewById<Button>(R.id.mainMenuButton)
        mainMenuButton.setOnClickListener {
            val toMainPage = Intent(this, Mainpage::class.java)
            startActivity(toMainPage)
        }
        RetrofitClient.instance.getUsers().enqueue(object : Callback<JsonObject> {
            override fun onResponse(call: Call<JsonObject>, response: Response<JsonObject>) {
                if (response.isSuccessful) {
                    // Log the successful response
                    val gson = Gson()
                    val userResponse = gson.fromJson(response.body().toString(), UserResponse::class.java)
                    val usersList = userResponse.users
                    addUserstoView(usersList)


                    usersList.forEach { user ->
                        Log.d("User", "User: $user")
                    }
                } else {
                    Log.e("ResponseError", "Error Code: ${response.code()}")
                }
            }

            override fun onFailure(call: Call<JsonObject>, t: Throwable) {
                // Log the failure
                Log.e("ResponseFailure", "Error: ${t.message}")
            }        })
    }

    private fun addUserstoView(users: List<UserD>) {
        val container: LinearLayout = findViewById(R.id.allUsersContainer)
        for (i in users.indices) {
            val user = users[i]
            val userView = LayoutInflater.from(this).inflate(R.layout.user_view, container, false)
            val emailText = userView.findViewById<TextView>(R.id.userEmail)
            val friendButton = userView.findViewById<Button>(R.id.addFriendButton)

            // Set the song details
            emailText.text = user.email
            val friendEmailBody = friendMailBody(user.email)
            friendButton.setOnClickListener {
                RetrofitClient.instance.addFriend(getUserToken(this).toString(),friendEmailBody).enqueue(object :
                    Callback<AddFriendResponse> {
                    override fun onResponse(
                        call: Call<AddFriendResponse>,
                        response: Response<AddFriendResponse>
                    ) {
                        //XD
                    }

                    override fun onFailure(call: Call<AddFriendResponse>, t: Throwable) {
                        //XD
                    }
                })
            }
            container.addView(userView)
        }
    }
}