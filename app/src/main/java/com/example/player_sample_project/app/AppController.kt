package com.example.player_sample_project.app

import android.content.Context
import android.content.SharedPreferences

/**
 * Used this AppController class to handled the preference data for Login and Api
 * method return type - String
 */
class AppController(context: Context) {
    private val sharedPrefLogin: SharedPreferences = context.getSharedPreferences("LoginPrefs", Context.MODE_PRIVATE)
    private val sharedPrefApi: SharedPreferences = context.getSharedPreferences("ApiPrefs", Context.MODE_PRIVATE)

    // key - "loginStatus"
    fun storeLoginStatus(hashMap: HashMap<String, String>) { //update with Map
        val editor = sharedPrefLogin.edit()
        for (itemKey in hashMap.keys) {
            editor.putString(itemKey, hashMap[itemKey])
        }
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