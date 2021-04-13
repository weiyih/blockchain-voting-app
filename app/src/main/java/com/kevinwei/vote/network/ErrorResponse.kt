package com.kevinwei.vote.network

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class ErrorResponse(
    @Json(name="status")
    val status: String,

    @Json(name="message")
    val message: String,
    )