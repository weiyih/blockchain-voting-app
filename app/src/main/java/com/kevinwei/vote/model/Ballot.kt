package com.kevinwei.vote.model

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class Ballot(
    @Json(name = "election_id")
    val electionId: String,

    @Json(name = "district_id")
    val districtId: String,

    @Json(name = "district_name")
    val districtName: String,

    @Json(name = "candidate_list")
    val candidateList: List<Candidate>
)