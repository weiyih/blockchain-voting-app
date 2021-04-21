package com.kevinwei.vote.model

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class BallotVote (
    @Json(name = "election_id")
    val electionId: String,

    @Json(name = "district_id")
    val districtId: Int,

    @Json(name = "candidate_id")
    val candidateId: String,
)