package com.kevinwei.vote.network

import com.kevinwei.vote.model.Election
import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class SubmitResponse(
    @Json(name = "success")
    val success: String,

    @Json(name = "data")
    val data: Election,
)

