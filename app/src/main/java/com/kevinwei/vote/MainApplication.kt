package com.kevinwei.vote

import android.app.Application
import android.content.Context
import android.provider.Settings

class MainApplication : Application() {

    override fun onCreate() {
        super.onCreate()
        appContext = applicationContext
        deviceId = Settings.Secure.getString(contentResolver,Settings.Secure.ANDROID_ID);
    }

    companion object {
        lateinit var appContext: Context
        lateinit var deviceId: String
    }
}