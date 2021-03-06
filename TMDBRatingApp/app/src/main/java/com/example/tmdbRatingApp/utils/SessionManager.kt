package com.example.tmdbRatingApp.utils

import android.content.Context
import android.content.SharedPreferences
import com.example.tmdbRatingApp.R
import com.example.tmdbRatingApp.utils.Constants.ADULT
import com.example.tmdbRatingApp.utils.Constants.USER_ID
import com.example.tmdbRatingApp.utils.Constants.USER_NAME
import com.example.tmdbRatingApp.utils.Constants.USER_TOKEN

class SessionManager (context: Context) {
    private var prefs: SharedPreferences = context.getSharedPreferences(context.getString(R.string.app_name), Context.MODE_PRIVATE)


    fun saveAuthToken(token: String?) {
        val editor = prefs.edit()
        editor.putString(USER_TOKEN, token)
        editor.apply()
    }


    fun fetchAuthToken(): String? {
        return prefs.getString(USER_TOKEN, null)
    }

    fun saveAccId(id: Int) {
        val editor = prefs.edit()
        editor.putInt(USER_ID, id)
        editor.apply()
    }


    fun fetchAccId(): Int {
        return prefs.getInt(USER_ID, 0)
    }


    fun saveUserName(name: String) {
        val editor = prefs.edit()
        editor.putString(USER_NAME, name)
        editor.apply()
    }


    fun fetchUserName(): String? {
        return prefs.getString(USER_NAME,null)
    }

    fun saveIncludeAdult(boolean: Boolean) {
        val editor = prefs.edit()
        editor.putBoolean(ADULT, boolean)
        editor.apply()
    }


    fun fetchIncludeAdult(): Boolean {
        return prefs.getBoolean(ADULT,false)
    }
}