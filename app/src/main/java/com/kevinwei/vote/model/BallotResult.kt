package com.kevinwei.vote.model

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass


@JsonClass(generateAdapter = true)
data class BallotResult (
    @Json(name = "message")
    val message: String,

    @Json(name = "timestamp")
    val timestamp: Int,
)