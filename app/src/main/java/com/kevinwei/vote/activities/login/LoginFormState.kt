package com.kevinwei.vote.activities.login

sealed class LoginFormState

data class FailedLoginFormState(
    val usernameError: Int? = null,
    val passwordError: Int? = null
) : LoginFormState()

data class SuccessLoginFormState(
    val isDataValid: Boolean = false
) : LoginFormState()

