package com.kevinwei.vote.model

import java.util.Date

class Election {
    var title: String? = null
    var description: String? = null
    var voteStatus: Boolean? = false
    var startDate: Date? = null
    var endDate: Date? = null
    var advStarDate: Date? = null
    var advEndDate: Date? = null
}