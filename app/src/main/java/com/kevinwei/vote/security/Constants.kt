package com.kevinwei.vote

import androidx.biometric.BiometricManager.Authenticators.BIOMETRIC_STRONG
import androidx.biometric.BiometricManager.Authenticators.DEVICE_CREDENTIAL

const val SHARED_PREFS_FILENAME = "prefs_biometric"
const val SHARED_PREFS_USERNAME = "prefs_username"
const val SHARED_PREFS_USERTOKEN = "prefs_usertoken"
const val CIPHERTEXT_WRAPPER = "ciphertext_wrapper"
const val AUTHORIZED_BIOMETRICS = BIOMETRIC_STRONG
const val BASE_URL = "http://10.0.2.2/"
const val SERVER_PUBLIC_KEY = """-----BEGIN PUBLIC KEY-----
MFkwEwYHKoZIzj0CAQYIKoZIzj0DAQcDQgAEi3rRCoYiBw5B2rpSDcLK0E5y/PES
/UYwmBE0WHrH/mBBLXkkJOpVDf1j2XtCTjLdxHRrQrfpe/OKQUjQn3Wn8g==
-----END PUBLIC KEY-----"""