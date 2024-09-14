package com.example.attendance

import android.content.Context
import android.content.SharedPreferences

class StorageManager(private val context: Context) {

    private val sharedPreferences: SharedPreferences =
        context.getSharedPreferences("user_prefs", Context.MODE_PRIVATE)

    fun saveUserId(id: Int){
        val editor = sharedPreferences.edit()
        editor.putInt("USER_ID", id)
        editor.apply() // Save asynchronously
    }
    fun getUserId(): Int {
        return sharedPreferences.getInt("USER_ID", 0)
    }

    fun clearUserId() {
        val editor = sharedPreferences.edit()
        editor.remove("USER_ID")
        editor.apply()
    }
    fun saveToken(token: String) {
        val editor = sharedPreferences.edit()
        editor.putString("TOKEN_KEY", token)
        editor.apply() // Save asynchronously
    }

    // Get token
    fun getToken(): String? {
        return sharedPreferences.getString("TOKEN_KEY", null)
    }

    // Clear token
    fun clearToken() {
        val editor = sharedPreferences.edit()
        editor.remove("TOKEN_KEY")
        editor.apply()
    }
}
