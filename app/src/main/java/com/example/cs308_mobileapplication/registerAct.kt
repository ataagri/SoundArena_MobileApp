package com.example.cs308_mobileapplication

import android.content.Intent
import android.os.Bundle
import android.widget.TextView
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.example.cs308_mobileapplication.ui.theme.CS308_MobileApplicationTheme
import com.google.android.material.textfield.TextInputEditText

class RegisterAct : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.registeract)
        //Sign In Button Switches to Login Page
        val signInText = findViewById<TextView>(R.id.HaveAcc)
        signInText.setOnClickListener {
            val toSignIn = Intent(this, LoginAct::class.java)
            startActivity(toSignIn)
        }

    }
}

class LoginAct : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.loginact)

        val signUpText = findViewById<TextView>(R.id.SignUp)
        signUpText.setOnClickListener {
            val toSignup = Intent(this, RegisterAct::class.java)
            startActivity(toSignup)
        }


    }

}


