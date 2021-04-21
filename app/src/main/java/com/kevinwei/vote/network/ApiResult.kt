package com.kevinwei.vote.network

sealed class ApiResult

data class SuccessResult(
    val success: Boolean? = false
) : ApiResult()

data class FailedResult(
    val error: String? = null,
    val unauthenticated: Boolean? = null
) : ApiResult()

