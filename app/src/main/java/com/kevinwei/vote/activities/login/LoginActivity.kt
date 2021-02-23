package com.kevinwei.vote.activities.login

import android.content.Context
import android.os.Bundle
import android.widget.Toast
import androidx.activity.viewModels
import androidx.biometric.BiometricManager
import androidx.appcompat.app.AppCompatActivity
import androidx.biometric.BiometricManager.Authenticators
import androidx.core.widget.doAfterTextChanged
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModel
import com.kevinwei.vote.CIPHERTEXT_WRAPPER
import com.kevinwei.vote.CryptographyManager
import com.kevinwei.vote.SHARED_PREFS_FILENAME
import com.kevinwei.vote.databinding.ActivityLoginBinding


class LoginActivity : AppCompatActivity() {
    private lateinit var binding: ActivityLoginBinding
    private val cryptoManager = CryptographyManager()
    private val ciphertextWrapper
        get() = cryptoManager.getCiphertextWrapperFromSharedPrefs(
            applicationContext,
            SHARED_PREFS_FILENAME,
            Context.MODE_PRIVATE,
            CIPHERTEXT_WRAPPER
        )
    private val AUTHORIZED_BIOMETRICS =
        (Authenticators.DEVICE_CREDENTIAL or Authenticators.BIOMETRIC_STRONG)

    private val loginWithPasswordViewModel by viewModels<LoginViewModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupLoginForPassword()
    }

    private fun setupLoginForPassword() {
        loginWithPasswordViewModel.loginWithPasswordFormState.observe(
            this,
            Observer { formState: LoginFormState ->
                val loginState = formState ?: return@Observer

                when (loginState) {
                    is SuccessLoginFormState -> binding.btnLogin.isEnabled = loginState.isDataValid
                    is FailedLoginFormState -> {
                        loginState.usernameError?.let { binding.txtUser.error = getString(it) }
                        loginState.passwordError?.let { binding.txtPassword.error = getString(it) }
                    }
                }
            })

        loginWithPasswordViewModel.loginResult.observe(this, Observer {
            val loginResult = it ?: return@Observer
            if (loginResult.success) {
                Toast.makeText(this, "Logged In", Toast.LENGTH_LONG).show()
            }
        })

        binding.txtUser.editText?.doAfterTextChanged {
            loginWithPasswordViewModel.onLoginDataChange(
                binding.txtUser.editText.toString(),
                binding.txtPassword.editText.toString()
            )
        }

        binding.txtPassword.editText?.doAfterTextChanged {
            loginWithPasswordViewModel.onLoginDataChange(
                binding.txtUser.editText.toString(),
                binding.txtPassword.editText.toString()
            )
        }

        binding.btnLogin.setOnClickListener {
            loginWithPasswordViewModel.loginWithPassword(
                binding.txtUser.editText.toString(),
                binding.txtPassword.editText.toString()
            )
        }
    }

    private fun checkBioAuth() {
        var canAuthenticate: Int
//        binding = ActivityLoginBinding.inflate

        // Android 9.0 Pie
        if (android.os.Build.VERSION.SDK_INT == 28) {
//            canAuthenticate = KeyguardManager.from(applicationContext)

        } else if (android.os.Build.VERSION.SDK_INT == 29) {
            // BIOMETRIC_SUCCESS or STATUS_UNKNOWN
            canAuthenticate = BiometricManager.from(applicationContext).canAuthenticate()
        } else if (android.os.Build.VERSION.SDK_INT >= 30) {
            canAuthenticate =
                BiometricManager.from(applicationContext).canAuthenticate(AUTHORIZED_BIOMETRICS)
        }
        canAuthenticate =
            BiometricManager.from(applicationContext).canAuthenticate(AUTHORIZED_BIOMETRICS)
        if (canAuthenticate == BiometricManager.BIOMETRIC_SUCCESS) {

        }

    }

    // Determines login method
    private fun onLogin() {

    }


    private fun loginWithPassword() {

    }

    private fun loginWithBiometrics() {

    }
}