package com.kevinwei.vote.model

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class User (
    @Json( name="email")
    var username: String = "",

    @Json(name= "token")
    var token: String = "",

    @Json (name="verified")
    var verified: Boolean = false,
)