package com.kevinwei.vote.network

import com.kevinwei.vote.model.BallotResult
import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class SubmitResponse(
    @Json(name = "success")
    val success: String,

    @Json(name = "data")
    val data: BallotResult?,

    @Json(name ="error")
    val error: ErrorResponse?
)

