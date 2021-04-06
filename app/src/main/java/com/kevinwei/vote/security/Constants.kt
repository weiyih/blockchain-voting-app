package com.kevinwei.vote

import androidx.biometric.BiometricManager.Authenticators.BIOMETRIC_STRONG
import androidx.biometric.BiometricManager.Authenticators.DEVICE_CREDENTIAL

const val SHARED_PREFS_FILENAME = "biometric_prefs"
const val CIPHERTEXT_WRAPPER = "ciphertext_wrapper"
const val AUTHORIZED_BIOMETRICS = BIOMETRIC_STRONG
