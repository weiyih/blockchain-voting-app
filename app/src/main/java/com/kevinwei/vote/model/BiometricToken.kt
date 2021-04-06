package com.kevinwei.vote.model

import com.squareup.moshi.Json

data class BiometricToken(
    @Json(name = "biometric_token")
    val biometricToken: String
)