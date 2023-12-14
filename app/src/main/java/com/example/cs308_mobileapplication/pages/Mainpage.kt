package com.example.cs308_mobileapplication.pages

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.activity.ComponentActivity
import com.example.cs308_mobileapplication.R
import com.example.cs308_mobileapplication.utils.clearUserId
import com.example.cs308_mobileapplication.utils.clearUserToken
class Mainpage : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?){
        super.onCreate(savedInstanceState)
        setContentView(R.layout.mainpage)
        val mySongsButton = findViewById<Button>(R.id.mySongsButton)
        val addSongsButton = findViewById<Button>(R.id.addSongButton)
        val logOutButton = findViewById<Button>(R.id.logOutButton)
        val allSongsButton = findViewById<Button>(R.id.allSongsButton)
        val allUsersButton = findViewById<Button>(R.id.allUsersButton)
        val friendListButton = findViewById<Button>(R.id.friendsButton)
        val topTenButton = findViewById<Button>(R.id.topTenButton)
        val recommendationsButton = findViewById<Button>(R.id.recommendButton)

        recommendationsButton.setOnClickListener {
            val toRecommendations = Intent(this, Recommendations::class.java)
            startActivity(toRecommendations)
        }
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
            clearUserId(this@Mainpage)
            startActivity(toLogin)
        }

        allSongsButton.setOnClickListener {
            val toAllSongs = Intent(this, Allsongs::class.java)
            startActivity(toAllSongs)
        }
        allUsersButton.setOnClickListener {
            val toAllUser = Intent(this, Alluser::class.java)
            startActivity(toAllUser)
        }
        friendListButton.setOnClickListener {
            val toFriendList: Intent = Intent(this, FriendList::class.java)
            startActivity(toFriendList)
        }

        topTenButton.setOnClickListener {
            val toSortbygenre = Intent(this, Sortbygenre::class.java)
            startActivity(toSortbygenre)
        }


    }



}