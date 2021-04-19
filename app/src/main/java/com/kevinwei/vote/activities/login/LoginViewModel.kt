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
import com.kevinwei.vote.network.ElectionsApi
import com.kevinwei.vote.network.NetworkResponse
import com.kevinwei.vote.security.SessionManager
import kotlinx.coroutines.cancel
import kotlinx.coroutines.launch
import java.lang.Exception

class LoginViewModel : ViewModel() {
    private val TAG = "LoginViewModel"

    lateinit var sessionManager: SessionManager

    enum class AuthenticationState { AUTHENTICATED, UNAUTHENTICATED }
    private val _authState = MutableLiveData<AuthenticationState>()
    val authState: LiveData<AuthenticationState> = _authState

    // Input validation
    private val _loginForm = MutableLiveData<LoginFormState>()
    val loginFormState: LiveData<LoginFormState> = _loginForm

    // Login Results
    private val _loginResult = MutableLiveData<LoginResult>()
    val loginResult: LiveData<LoginResult> = _loginResult

    // User
    private val _user = MutableLiveData<User>()
    val user: LiveData<User> = _user

    init {
//        _authState.value = AuthenticationState.AUTHENTICATED
        _authState.value = AuthenticationState.UNAUTHENTICATED
        sessionManager = SessionManager(MainApplication.appContext)
    }

    /*
    * Validates user input for username and password
    * Displays error messages
    */
    fun onLoginDataChange(username: String, password: String) {
        // Checks if username is a valid email
        if (!isUsernameValid(username)) {
            _loginForm.value = FailedLoginFormState(usernameError = R.string.invalid_username)
            // Checks if password is empty or greater than 7 characters
        } else if (!isPasswordValid(password)) {
            _loginForm.value = FailedLoginFormState(passwordError = R.string.invalid_password)
        } else {
            _loginForm.value = SuccessLoginFormState(isDataValid = true)
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
    fun loginWithPassword(username: String, password: String) {
        viewModelScope.launch {
            try {
                // LoginFormState should always be SuccessLoginFormState
                if (loginFormState.value is SuccessLoginFormState) {

                    // TODO (" Loading screen")
                    // TODO("Pass in device information")
                    val loginRequest = LoginRequest(username, password)
                    val response = ElectionsApi.client.login(loginRequest)

                    when (response) {
                        is NetworkResponse.Success -> {
                            Log.d(TAG, response.body.toString())
                            _user.value = response.body!!
                            _authState.value = AuthenticationState.AUTHENTICATED
                            _loginResult.value = LoginResult(true)
                            sessionManager.saveAuthToken(user.value!!.token.toString())
                        }
                        is NetworkResponse.Failure -> {
                            _loginResult.value = LoginResult(false)
                            _authState.value = AuthenticationState.UNAUTHENTICATED
                        }
                        is NetworkResponse.NetworkError -> {
                            _loginResult.value = LoginResult(false)
                            _authState.value = AuthenticationState.UNAUTHENTICATED

                        }
                        is NetworkResponse.UnknownError -> {
                            _loginResult.value = LoginResult(false)
                            _authState.value = AuthenticationState.UNAUTHENTICATED
                        }
                    }
                }
            } catch (e: Exception) {
                Log.d(TAG, e.message.toString())
                _loginResult.value = LoginResult(false)
                _authState.value = AuthenticationState.UNAUTHENTICATED
            } finally {
                // TODO (" Loading screen disabled")
            }
        }
    }


    // TODO("get generated password_token used with biometric login")
    fun loginWithBiometric(username: String, biometricPassword: String) {
        viewModelScope.launch {
            try {
                Log.d(TAG, "Logging in")
                val loginRequest = LoginRequest(username, biometricPassword)
                val result = ElectionsApi.client.login(loginRequest)
//                _user.value = result.body!!
                _authState.value = AuthenticationState.AUTHENTICATED
                _loginResult.value = LoginResult(true)
            } catch (e: Exception) {
                Log.d(TAG, e.toString())
                _authState.value = AuthenticationState.UNAUTHENTICATED
                _loginResult.value = LoginResult(false)
            }
        }
    }

    // TODO ("remove")
    fun testLogin() {
        _authState.value = AuthenticationState.AUTHENTICATED
        _loginResult.value = LoginResult(true)
    }
}

