package com.kevinwei.vote.model

import com.squareup.moshi.Json

data class Election(
    @Json(name = "election_id")
    val electionId: String,

    @Json(name = "election_name")
    val electionName: String,

    @Json(name = "election_description")
    val electionDescription: String,

    @Json(name = "election_start_date")
    val electionStartDate: String,

    @Json(name = "election_end_date")
    val electionEndDate: String,

    @Json(name = "advanced_start_date")
    val advancedStartDate: String,

    @Json(name = "advanced_end_date")
    val advancedEndDate: String,

    @Json(name = "advanced_polling")
    val advancedPolling: Boolean,

    @Json(name = "created_at")
    val createdAt: String,

    @Json(name = "updated_at")
    val updatedAt: String,
)

//    election_id: '9cd5f582-75e5-4bee-b451-e5417c18e761',
//    election_name: 'Oakville Municipal Election 2022',
//    election_start_date: '2021-03-01T00:00:00.000Z',
//    election_end_date: '2022-01-01T00:00:00.000Z',
//    advanced_polling: 1,
//    advanced_start_date: '2020-01-01T00:00:00.000Z',
//    advanced_end_date: '2020-12-31T24:00:00.000Z',
//    created_at: '2020-01-01T00:00:00.000Z',
//    updated_at: '2020-02-01T00:00:00.000Z',
//    locked: 0,
//    progress: 0,
//    disabled: 1,
//    channel_name: 'oakville-municipal-election-2022',
//    contract_name: 'voting_contract',