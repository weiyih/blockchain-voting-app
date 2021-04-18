package com.kevinwei.vote.network

import android.content.Context
import com.kevinwei.vote.MainApplication
import com.kevinwei.vote.R
import okhttp3.Interceptor
import okhttp3.Request
import okhttp3.Response


class AuthInterceptor() : Interceptor {
    private val context = MainApplication.appContext
    private val USER_TOKEN = context.getString(R.string.pref_usertoken)

    override fun intercept(chain: Interceptor.Chain): Response {
        var request: Request = chain.request()

        // TODO - DI shared preferences
        val shared = context.getString(R.string.preference_file_key)
        val sharedPrefs = context.getSharedPreferences(shared, Context.MODE_PRIVATE)
        val authToken = sharedPrefs.getString(USER_TOKEN, null)

        if (authToken != null) {
            request = request.newBuilder()
                .addHeader("Authorization", "Bearer $authToken")
                .build()
        }
        return chain.proceed(request)
    }
}