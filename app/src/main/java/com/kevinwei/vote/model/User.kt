package com.kevinwei.vote.model

import com.google.gson.annotations.SerializedName
import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class User (
    @Json( name="email")
    var username: String? = null,

    @Json(name= "token")
    var token: String? = null,

    @Json (name="verified")
    var verified: Boolean? = false,

    @Json( name= "error")
    var error: String? = null
)
//@JsonClass(generateAdapter = true)
//data class User (
//    @SerializedName("username")
//    var username: String? = null,
//
//    @SerializedName("token")
//    var token: String? = null,
//
//    @SerializedName("verified")
//    var verified: Boolean = false,
//
//    @SerializedName("verified")
//    var verified: Boolean = false,
//
//)