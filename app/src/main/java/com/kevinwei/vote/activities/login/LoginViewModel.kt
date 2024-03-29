package com.kevinwei.vote.activities.login

import android.util.Log
import android.util.Patterns
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kevinwei.vote.MainApplication
import com.kevinwei.vote.R
import com.kevinwei.vote.model.LoginRequest
import com.kevinwei.vote.model.User
import com.kevinwei.vote.network.*
import com.kevinwei.vote.security.SessionManager
import kotlinx.coroutines.launch
import java.lang.Exception

class LoginViewModel : ViewModel() {
    private val TAG = "LoginViewModel"

    var sessionManager: SessionManager

    enum class AuthenticationState { AUTHENTICATED, UNAUTHENTICATED }

    private val _authState = MutableLiveData<AuthenticationState>()
    val authState: LiveData<AuthenticationState> = _authState

    // Input validation
    private val _loginForm = MutableLiveData<LoginFormState>()
    val loginFormState: LiveData<LoginFormState> = _loginForm

    // Login Results
    private val _apiResult = MutableLiveData<ApiResult>()
    val apiResult: LiveData<ApiResult> = _apiResult

    // User
    private val _user = MutableLiveData<User>()
    val user: LiveData<User> = _user

    init {
        _authState.value = AuthenticationState.UNAUTHENTICATED
        _loginForm.value = FailedLoginFormState()
        sessionManager = SessionManager(MainApplication.appContext)
    }

    /*
    * Validates user input for username and password
    * Displays error messages
    */

    fun onUsernameDataChange(username: String): Int? {
        if (!isUsernameValid(username)) return R.string.invalid_username
        return null
    }

    fun onPasswordDataChange(password: String): Int? {
        if (!isPasswordValid(password)) return R.string.invalid_password
        return null
    }

    fun onLoginDataChange(username: String, password: String) {
        val userError: Int? = onUsernameDataChange(username)
        val passError: Int? = onPasswordDataChange(password)

        if (userError == null && passError == null) {
            _loginForm.value = SuccessLoginFormState(isDataValid = true)
        } else {
            _loginForm.value = FailedLoginFormState(usernameError = userError, passwordError = passError)
        }
    }

    /*    * Verifies if the username matches the default email pattern
    * https://android.googlesource.com/platform/frameworks/base/+/81aa097/core/java/android/util/Patterns.java
    */
    private fun isUsernameValid(username: String): Boolean {
        return if (username.contains('@')) {
            Patterns.EMAIL_ADDRESS.matcher(username).matches()
        } else {
            return false
        }
    }

    /*
    * Verifies if the password is the correct length
    */
    private fun isPasswordValid(password: String): Boolean {
        if (password.isEmpty()) {
            return true
        } else return password.length >= 8
    }

    /*
    * Login with username and passwword
    */
    fun login(username: String, password: String) {
        viewModelScope.launch {
            try {
                Log.d(TAG, "Loggin In")
                // TODO (" Loading screen")
                // TODO("Pass in device information")
                val loginRequest = LoginRequest(username, password)
                val response = ElectionsApi.client.login(loginRequest)
                when (response) {
                    is Result.Success -> {
                        when (response.body!!.success) {
                            "error" -> {
                                Log.d(TAG, "API Success Error")
                                //Missing or Invalid username/password
                                _apiResult.value = FailedResult(response.body!!.error!!.message)
                                _authState.value = AuthenticationState.UNAUTHENTICATED
                            }
                            "success" -> {
                                Log.d(TAG, "API Success")
                                _user.value = response.body.data!!
                                _authState.value = AuthenticationState.AUTHENTICATED
                                _apiResult.value = SuccessResult(true)
                                sessionManager.saveAuthToken(user.value!!.token.toString())
                            }
                        }
                    }
                    is Result.Error -> {
                        Log.d(TAG, "API Error")
                        //Something went wrong
                        _apiResult.value = FailedResult("Something went wrong. Try again later")
                        _authState.value = AuthenticationState.UNAUTHENTICATED
                    }
                    is Result.NetworkError -> {
                        Log.d(TAG, "Network Error")
                        _apiResult.value = FailedResult("Network error. Try again later")
                        _authState.value = AuthenticationState.UNAUTHENTICATED
                    }
                }
            } catch (e: Exception) {
                Log.d(TAG, e.message.toString())
                _apiResult.value = FailedResult(e.message.toString())
                _authState.value = AuthenticationState.UNAUTHENTICATED
            } finally {
                // TODO (" Loading screen disabled")
            }
        }
    }
}

