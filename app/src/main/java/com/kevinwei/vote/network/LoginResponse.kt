package com.kevinwei.vote.network

import com.google.gson.annotations.SerializedName
import com.kevinwei.vote.model.User
import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass
import org.json.JSONObject

@JsonClass(generateAdapter = true)
data class LoginResponse(
    @Json(name="success")
    val success: String,

    @Json(name="data")
    val data: User,
)