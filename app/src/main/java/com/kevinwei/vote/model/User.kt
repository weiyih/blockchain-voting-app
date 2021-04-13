package com.kevinwei.vote.model

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
object User {
    @Json( name="username")
    var username: String? = null

    @Json (name="user_id")
    var userId: String? = null

//    @Json(name= "token")
//    var token: String? = null

    @Json (name="verified_status")
    var verified: Boolean = false
}