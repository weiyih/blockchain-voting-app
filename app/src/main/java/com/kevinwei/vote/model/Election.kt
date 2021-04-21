package com.kevinwei.vote.model

import android.os.Parcelable
import com.squareup.moshi.*
import kotlinx.parcelize.Parcelize

@Parcelize
@JsonClass(generateAdapter = true)
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

    @Json(name = "advanced_polling")
    val advancedPolling: Boolean,

    @Json(name = "advanced_start_date")
    var advancedStartDate: String? = null,

    @Json(name = "advanced_end_date")
    var advancedEndDate: String? = null,

    @Json(name = "created_at")
    val createdAt: String,

    @Json(name = "updated_at")
    val updatedAt: String,

    @Json(name = "vote_status")
    val voteStatus: Int,
) : Parcelable