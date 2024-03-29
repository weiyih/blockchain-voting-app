package com.kevinwei.vote.model

import com.squareup.moshi.Json

data class Candidate(
    @Json(name = "candidate_id")
    val candidateId: String,

    @Json(name = "candidate_name")
    val candidateName: String,

    @Transient
    var selected: Boolean = false
)

