package com.kevinwei.vote.model

import com.squareup.moshi.Json

// JSON object of the ballot to be displayed for a specific district and election
data class BallotJson(
    @Json(name = "election_id")
    val electionId: String,

    @Json(name = "district_id")
    val districtId: String,

    @Json(name = "district_name")
    val districtName: String,

    @Json(name = "candidate_list")
    val candidateList: List<Candidate>
)