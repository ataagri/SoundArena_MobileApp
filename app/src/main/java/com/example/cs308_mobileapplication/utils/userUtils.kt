package com.example.cs308_mobileapplication.utils

import android.content.Context

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