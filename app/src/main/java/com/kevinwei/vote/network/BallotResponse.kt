package com.kevinwei.vote.network

import com.kevinwei.vote.model.Ballot
import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class BallotResponse(
    @Json(name = "success")
    val success: String,

    @Json(name = "data")
    val data: Ballot,
)

