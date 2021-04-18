package com.kevinwei.vote.security

import android.content.Context
import android.content.SharedPreferences
import com.kevinwei.vote.MainApplication
import com.kevinwei.vote.R

class SessionManager(context: Context) {

//    private val context = MainApplication.appContext

    private val prefKey = context.getString(R.string.preference_file_key)
    var sharedPrefs: SharedPreferences = context.getSharedPreferences(prefKey, Context.MODE_PRIVATE)

    private val USER_TOKEN = context.getString(R.string.pref_usertoken)

    init {
        getAuthToken()
    }

    fun saveAuthToken(token: String) {
        val editor = sharedPrefs.edit()
        editor.putString(USER_TOKEN, token)
        editor.apply()
    }

    fun getAuthToken(): String? {
        return sharedPrefs.getString(USER_TOKEN, null)
    }

    fun removeAuthToken() {
        val editor = sharedPrefs.edit()
        editor.remove(USER_TOKEN)
        editor.apply()
    }
}
