package com.example.cs308_mobileapplication.pages

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.widget.Button
import android.widget.LinearLayout
import android.widget.SearchView
import android.widget.TextView
import androidx.activity.ComponentActivity
import com.example.cs308_mobileapplication.R
import com.example.cs308_mobileapplication.data.UserD
import com.example.cs308_mobileapplication.data.UserResponse
import com.example.cs308_mobileapplication.network.RetrofitClient
import com.example.cs308_mobileapplication.utils.getUserId
import com.google.gson.Gson
import com.google.gson.JsonObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class FriendList : ComponentActivity(){
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.friendlist)
        val mainMenuButton = findViewById<Button>(R.id.mainMenuButton)
        val searchBar = findViewById<SearchView>(R.id.friendSearchButton)
        searchBar.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                query?.let { filterUsers(it) }
                return true
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                newText?.let { filterUsers(it) }
                return true
            }
        })
        mainMenuButton.setOnClickListener {
            val toMainpage: Intent = Intent(this, Mainpage::class.java)
            startActivity(toMainpage)
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
        val container: LinearLayout = findViewById(R.id.friendsContainer)
        container.removeAllViews()
        for (i in users.indices) {
            if(users[i].id == getUserId(this)){
                for(friend  in users[i].friends){
                    val userView = LayoutInflater.from(this)
                        .inflate(R.layout.friendview, container, false)
                    val emailText = userView.findViewById<TextView>(R.id.friendEmail)
                    for(user in users){
                        if(user.id == friend){
                            emailText.text = user.email
                        }
                    }
                    container.addView(userView)
                }

            }

            }

        }

    private fun filterUsers(query: String) {
        RetrofitClient.instance.getUsers().enqueue(object : Callback<JsonObject> {
            override fun onResponse(call: Call<JsonObject>, response: Response<JsonObject>) {
                if (response.isSuccessful) {
                    val gson = Gson()
                    val userResponse = gson.fromJson(response.body().toString(), UserResponse::class.java)
                    val allUsers = userResponse.users
                    val currentUserFriends = allUsers.find { it.id == getUserId(this@FriendList) }?.friends ?: listOf()

                    val filteredFriends = if (query.isEmpty()) {
                        currentUserFriends
                    } else {
                        currentUserFriends.filter { friendId ->
                            allUsers.find { it.id == friendId }?.email?.contains(query, ignoreCase = true) == true
                        }
                    }

                    displayFriends(filteredFriends, allUsers)
                }
            }

            override fun onFailure(call: Call<JsonObject>, t: Throwable) {
                // Handle failure
            }
        })
    }

    private fun displayFriends(friendIds: List<String>, allUsers: List<UserD>) {
        val container: LinearLayout = findViewById(R.id.friendsContainer)
        container.removeAllViews()

        friendIds.forEach { friendId ->
            allUsers.find { it.id == friendId }?.let { user ->
                val userView = LayoutInflater.from(this).inflate(R.layout.friendview, container, false)
                val emailText = userView.findViewById<TextView>(R.id.friendEmail)
                emailText.text = user.email
                container.addView(userView)
            }
        }
    }

}