package com.kevinwei.vote.model

import com.squareup.moshi.Json

// JSON object of the submitted ballot vote
data class BallotVote (
    @Json(name = "election_id")
    val electionId: String,

    @Json(name = "district_id")
    val districtId: String,

    @Json(name = "district_name")
    val districtName: String,

    @Json(name = "candidate_id")
    val candidateId: String,

    @Json(name = "timestamp")
    val timestamp: String
)