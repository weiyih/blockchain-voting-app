package com.kevinwei.vote.activities.login

import android.content.SharedPreferences
import android.util.Log
import android.util.Patterns
import android.widget.Toast
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kevinwei.vote.R
import com.kevinwei.vote.model.Election
import com.kevinwei.vote.model.User
import com.kevinwei.vote.network.ElectionsApi
import com.kevinwei.vote.security.BiometricPromptUtils
import kotlinx.coroutines.launch
import java.lang.Exception

class LoginViewModel : ViewModel() {

    enum class AuthenticationState {
        AUTHENTICATED, UNAUTHENTICATED;
    }

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
        _authState.value = AuthenticationState.UNAUTHENTICATED
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
    fun isUsernameValid(username: String): Boolean {
        return if (username.contains('@')) {
            Patterns.EMAIL_ADDRESS.matcher(username).matches()
        } else {
            return false
        }
    }

    /*
    * Verifies if the password is not empty or the correct length
    */
    fun isPasswordValid(password: String): Boolean {
        if (password.isEmpty()) {
            return true
        } else return password.length >= 8
    }

    /*
    * Login with username and passwword
    */
    fun loginWithPassword(username: String, password: String) {
        if (loginFormState.value is SuccessLoginFormState) {
            viewModelScope.launch {
                try {
                    // TODO("Pass in device information")
                    val user = ElectionsApi.retrofitService.login(username, password)
                    // TODO retrieve token from server to a transient object
                    // TODO - Handle server response
                    _loginResult.value = LoginResult(true)
                    _authState.value = AuthenticationState.AUTHENTICATED
                } catch (e: Exception) {
                    _loginResult.value = LoginResult(false)
                    _authState.value = AuthenticationState.UNAUTHENTICATED
                }
            }
        }
    }

    // TODO("get generated password_token used with biometric login")
    fun loginWithBiometric(username: String, biometricPassword: String) {
        viewModelScope.launch {
            try {
                _user.value = ElectionsApi.retrofitService.login(username, "biometric_token")
                _authState.value = AuthenticationState.AUTHENTICATED
                _loginResult.value = LoginResult(true)
            } catch (e: Exception) {
                _authState.value = AuthenticationState.UNAUTHENTICATED
                _loginResult.value = LoginResult(false)
            }
        }
    }

    fun testLogin() {
        _authState.value = AuthenticationState.AUTHENTICATED
        _loginResult.value = LoginResult(true)
    }
}

