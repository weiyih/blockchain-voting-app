package com.kevinwei.vote.activities.settings

import android.annotation.TargetApi
import android.content.Intent
import android.content.SharedPreferences
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.util.Log
import androidx.biometric.BiometricManager
import androidx.preference.PreferenceFragmentCompat
import androidx.preference.PreferenceManager
import com.kevinwei.vote.AUTHORIZED_BIOMETRICS
import com.kevinwei.vote.R


class SettingsFragment : PreferenceFragmentCompat() {
    private lateinit var sharedPreferences: SharedPreferences

    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        // Load the preferences from an XML resource
        setPreferencesFromResource(R.xml.root_preferences, rootKey)
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this.context)

        setupEnableBiometrics()
    }

    private fun setupEnableBiometrics() {
        val currentBiometric = sharedPreferences.getBoolean("biometric", false)

        //Register listener
        val biometricListener = SharedPreferences.OnSharedPreferenceChangeListener { prefs, key ->
            val valueChanged = preferenceManager.sharedPreferences.getBoolean("biometric", false)
            when (valueChanged) {
                true -> showBiometricPrompt()
                // TODO("false - show dialog warning")
//                false ->
            }
        }
    }

    // TODO - SharedPreference for username/email


    private fun showBiometricPrompt() {
        val biometricManager = BiometricManager.from(this.requireContext())

        when (biometricManager.canAuthenticate(AUTHORIZED_BIOMETRICS)) {
            BiometricManager.BIOMETRIC_SUCCESS ->
                Log.d("MY_APP_TAG", "App can authenticate using biometrics.")
            BiometricManager.BIOMETRIC_ERROR_NO_HARDWARE ->
                Log.e("MY_APP_TAG", "No biometric features available on this device.")
            // TODO("Implement behaviour")
            BiometricManager.BIOMETRIC_ERROR_HW_UNAVAILABLE ->
                Log.e("MY_APP_TAG", "Biometric features are currently unavailable.")
            // TODO("Implement behavkour")
            BiometricManager.BIOMETRIC_ERROR_NONE_ENROLLED -> {
                when (Build.VERSION.SDK_INT) {
                    Build.VERSION_CODES.R -> launchBiometricEnroll()
                    Build.VERSION_CODES.P, Build.VERSION_CODES.Q -> launchFingerprintEnroll()
                }
            }
        }
    }

    @TargetApi(30)
    private fun launchBiometricEnroll() {
        val enrollIntent = Intent(Settings.ACTION_BIOMETRIC_ENROLL).apply {
            putExtra(
                Settings.EXTRA_BIOMETRIC_AUTHENTICATORS_ALLOWED,
                AUTHORIZED_BIOMETRICS
            )
        }
        startActivity(enrollIntent)
    }

    @TargetApi(28)
    private fun launchFingerprintEnroll() {
        val enrollIntent = Intent(Settings.ACTION_FINGERPRINT_ENROLL).apply {
            putExtra(
                Settings.EXTRA_BIOMETRIC_AUTHENTICATORS_ALLOWED,
                AUTHORIZED_BIOMETRICS
            )
        }
        startActivity(enrollIntent)
    }
}