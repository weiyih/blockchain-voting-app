package com.kevinwei.vote.network

import com.google.gson.annotations.SerializedName
import com.kevinwei.vote.model.User
import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass
import org.json.JSONObject

@JsonClass(generateAdapter = true)
data class GenericResponse(
    @Json(name="success")
    val success: String,

    @Json(name="data")
    val data: Message?,

    @Json(name ="error")
    val error: ErrorResponse?,
)

data class Message(
    @Json(name = "message")
    val message: String,

    )
