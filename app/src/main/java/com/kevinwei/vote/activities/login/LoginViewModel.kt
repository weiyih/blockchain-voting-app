package com.kevinwei.vote.activities.login

import android.util.Patterns
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.kevinwei.vote.R
import com.kevinwei.vote.model.User

class LoginViewModel : ViewModel() {

//    enum class AuthenticationState {
//        AUTHENTICATED,
//        UNAUTHENTICATED,
//        INVALID_AUTHENTICATION
//    }

    private val _loginForm = MutableLiveData<LoginFormState>()
    val loginWithPasswordFormState: LiveData<LoginFormState> = _loginForm

    // loginResult
    private val _loginResult = MutableLiveData<LoginResult>()
    val loginResult: LiveData<LoginResult> = _loginResult

    // Updates the display text of the login button
    // Checks for biometric hardware enabled
    // Checks if user input password
    // Checks if user password is deleted


    fun onLoginDataChange(username: String, password: String) {
        if (!isUsernameValid(username)) {
            _loginForm.value = FailedLoginFormState(usernameError = R.string.invalid_username)
        } else if (!isPasswordValid(password)) {
            _loginForm.value = FailedLoginFormState(passwordError = R.string.invalid_password)
        } else {
            _loginForm.value = SuccessLoginFormState(isDataValid = true)
        }
    }


    /*
    https://android.googlesource.com/platform/frameworks/base/+/81aa097/core/java/android/util/Patterns.java
    EMAIL_ADDRESS Pattern
    "[a-zA-Z0-9\\+\\.\\_\\%\\-\\+]{1,256}" +
    "\\@" +
    "[a-zA-Z0-9][a-zA-Z0-9\\-]{0,64}" +
    "(" +
    "\\." +
    "[a-zA-Z0-9][a-zA-Z0-9\\-]{0,25}" +
    ")+"
     */
    fun isUsernameValid(username: String): Boolean {
        return if (username.contains('@')) {
            Patterns.EMAIL_ADDRESS.matcher(username).matches()
        } else {
            username.isNotBlank()
        }
    }

    // TODO - Might check if password is empty
    fun isPasswordValid(password: String): Boolean {
        return password.length > 8
    }

    fun loginWithPassword(username: String, password: String) {
        if (isUsernameValid(username) && isPasswordValid(password)) {
            // TODO retrieve token from server to a transient object
            User.username = username
            User.token = java.util.UUID.randomUUID().toString()

            _loginResult.value = LoginResult(true)

        } else {
            _loginResult.value = LoginResult(false)
        }
    }


}

