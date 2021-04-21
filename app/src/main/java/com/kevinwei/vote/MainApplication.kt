package com.kevinwei.vote

import android.app.Application
import android.content.Context
import android.provider.Settings

class MainApplication : Application() {

    override fun onCreate() {
        super.onCreate()
        appContext = applicationContext

        /*
        On Android 8.0 (API level 26) and higher versions of the platform, a 64-bit number
        (expressed as a hexadecimal string), unique to each combination of app-signing key,
        user, and device. Values of ANDROID_ID are scoped by signing key and user. The value may
        change if a factory reset is performed on the device or if an APK signing key changes.
        For more information about how the platform handles ANDROID_ID in Android 8.0 (API level 26)
        and higher, see Android 8.0 Behavior Changes.
         */
        deviceId = Settings.Secure.getString(contentResolver,Settings.Secure.ANDROID_ID);
    }

    companion object {
        lateinit var appContext: Context
        lateinit var deviceId: String
    }
}