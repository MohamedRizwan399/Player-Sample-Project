package com.example.player_sample_project.app

import android.content.Context
import android.content.SharedPreferences
import com.google.firebase.auth.FirebaseAuth

/**
 * Used this AppController class to handled the preference data for Login and Api
 * method return type - String
 */
class AppController(context: Context) {
    private val sharedPrefLogin: SharedPreferences = context.getSharedPreferences("LoginPrefs", Context.MODE_PRIVATE)
    private val sharedPrefApi: SharedPreferences = context.getSharedPreferences("ApiPrefs", Context.MODE_PRIVATE)

    // key - "loginStatus"
    fun storeLoginStatus(key1: String, value1: String, key2: String, value2: String) { //update with Map
        val editor = sharedPrefLogin.edit()
        editor.putString(key1, value1)
        editor.putString(key2, value2)
        editor.apply()
    }

    fun getLoginStatus(key: String, value: String): String? {
        return sharedPrefLogin.getString(key, value)
    }

    fun clearLoginPreferences() {
        val editor = sharedPrefLogin.edit()
        editor.clear()
        editor.apply()
    }

    fun storePreferenceApiData(key: String, value: String) {
        val editor = sharedPrefApi.edit()
        editor.putString(key, value)
        editor.apply()
    }

    fun getPreferenceApiData(key: String, value: String): String? {
        return sharedPrefApi.getString(key, value)
    }

    fun clearPreferencesApiData() {
        val editor = sharedPrefApi.edit()
        editor.clear()
        editor.apply()
    }


}