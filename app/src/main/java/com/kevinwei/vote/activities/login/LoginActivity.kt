package com.kevinwei.vote.activities.login

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.activity.viewModels
import androidx.biometric.BiometricManager
import androidx.appcompat.app.AppCompatActivity
import androidx.biometric.BiometricManager.Authenticators
import androidx.core.widget.doAfterTextChanged
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModel
import androidx.navigation.NavController
import androidx.navigation.findNavController
import com.kevinwei.vote.*
import com.kevinwei.vote.databinding.ActivityLoginBinding

class LoginActivity : AppCompatActivity() {
    private lateinit var binding: ActivityLoginBinding
    private val loginWithPasswordViewModel by viewModels<LoginViewModel>()
//    private lateinit var navController: NavController

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
                        loginState.usernameError?.let { binding.username.error = getString(it) }
                        loginState.passwordError?.let { binding.password.error = getString(it) }
                    }
                }
            })

        loginWithPasswordViewModel.loginResult.observe(this, Observer {
            val loginResult = it ?: return@Observer
            if (loginResult.success) {
                Toast.makeText(this, "Logged In", Toast.LENGTH_LONG).show()

                // Navigate to MainIntent
                val intent = Intent(this,MainActivity::class.java).apply {}
                startActivity(intent)
            }
        })

        binding.username.editText?.doAfterTextChanged {
            loginWithPasswordViewModel.onLoginDataChange(
                binding.username.editText.toString(),
                binding.password.editText.toString()
            )
        }

        binding.btnLogin.setOnClickListener {
            loginWithPasswordViewModel.loginWithPassword(
                binding.username.editText.toString(),
                binding.password.editText.toString()
            )
        }
    }
}