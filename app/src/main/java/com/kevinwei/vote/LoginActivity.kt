package com.kevinwei.vote

import androidx.biometric.BiometricManager
import androidx.biometric.BiometricPrompt
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.biometric.BiometricManager.Authenticators.BIOMETRIC_STRONG
import androidx.biometric.BiometricManager.Authenticators.DEVICE_CREDENTIAL

class LoginActivity : AppCompatActivity() {

    private val cryptoManager = CryptographyManager()

    private val AUTHORIZED_BIOMETRICS = ( DEVICE_CREDENTIAL or BIOMETRIC_STRONG )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        //

        // BiometricManager queries:
        // android.hardware.biometrics.BiometricManager (API 29 and above)
        // androidx.core.hardware.fingerprint.FingerprintManagerCompat (API 28 and below)
        val bioManager = BiometricManager.from(this)

        // Queries
        when (bioManager.canAuthenticate(AUTHORIZED_BIOMETRICS)){

        }
    }

    private fun loginWithPassword() {

    }

    private fun loginWithBiometrics() {

    }
}