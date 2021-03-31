package com.kevinwei.vote.activities.login

import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
import android.view.ViewGroup
import android.view.WindowInsetsController
import android.view.inputmethod.EditorInfo
import android.widget.EditText
import android.widget.Toast
import androidx.biometric.BiometricManager.Authenticators
import androidx.core.widget.doAfterTextChanged
import androidx.core.widget.doOnTextChanged
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.navigation.NavController
import androidx.navigation.fragment.findNavController
import com.kevinwei.vote.*
import com.kevinwei.vote.databinding.FragmentLoginBinding
import com.kevinwei.vote.model.User
import com.kevinwei.vote.model.User.username

class LoginFragment : Fragment() {
    private var _binding: FragmentLoginBinding? = null
    private val binding get() = _binding!!

    private val loginViewModel by viewModels<LoginViewModel>()
    private lateinit var navController: NavController
    private lateinit var sharedPref: SharedPreferences

    private val AUTHORIZED_BIOMETRICS =
        (Authenticators.DEVICE_CREDENTIAL or Authenticators.BIOMETRIC_STRONG)
    private var biometricEnabled = false
    private var biometricLogin = false
    private val cryptoManager = CryptographyManager()
    private val ciphertextWrapper
        get() = cryptoManager.getCiphertextWrapperFromSharedPrefs(
            requireActivity().applicationContext,
            SHARED_PREFS_FILENAME,
            Context.MODE_PRIVATE,
            CIPHERTEXT_WRAPPER
        )

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentLoginBinding.inflate(inflater, container, false)

        // Hide top action bar on login fragment
        (requireActivity() as MainActivity).supportActionBar!!.hide()

        sharedPref = this.requireActivity().getPreferences(Context.MODE_PRIVATE)
        navController = findNavController()

        setupLogin()
        return binding.root
    }


    override fun onDestroyView() {
        super.onDestroyView()
        (requireActivity() as MainActivity).supportActionBar!!.show()
        _binding = null
    }


    private fun getRememberedUsername() {
        val username = sharedPref.getString(getString(R.string.username), "")
        binding.username.editText?.setText(username)
    }

    // Checks and updates login button if biometrics have been successfully enabled
    private fun checkBiometricEnabled() {
        biometricEnabled = sharedPref.getBoolean(getString(R.string.saved_biometric), false)

        if (biometricEnabled) {
            binding.btnLogin.setText(R.string.btn_login_biometric)
        } else {
            binding.btnLogin.setText(R.string.btn_login)
        }
        binding.btnLogin.isEnabled = biometricEnabled
    }

    private fun setupLogin() {
        getRememberedUsername()
        checkBiometricEnabled()

        // Login observer to display errors
        loginViewModel.loginFormState.observe(
            viewLifecycleOwner,
            Observer { formState: LoginFormState ->
                val loginState = formState ?: return@Observer

                when (loginState) {
                    is SuccessLoginFormState -> {
                        binding.username.error = null
                        binding.password.error = null
                    }
                    is FailedLoginFormState -> {
                        loginState.usernameError?.let { binding.username.error = getString(it) }
                        loginState.passwordError?.let { binding.password.error = getString(it) }
                    }
                }
            })

        // Observe the loginResult from the /v1/login
        loginViewModel.loginResult.observe(viewLifecycleOwner, Observer {

            // TODO ("Replace")
            navController.navigate(R.id.action_loginFragment_to_electionFragment)

//            val loginResult = it ?: return@Observer
//            if (loginResult.SUCCESS) {
//
//                when (User.verified) {
//                    true -> navController.navigate(R.id.action_loginFragment_to_electionFragment)
//                    // false -> navController.navigate(R.id.action_loginFragment_to_verifyFragment)
//                }
//            } else if (!loginResult.SUCCESS) {
//                // Display error
//            }
        })

        //
        binding.username.editText?.doAfterTextChanged {
            // Input validation
            loginViewModel.onLoginDataChange(
                binding.username.editText!!.text.toString(),
                binding.password.editText!!.text.toString()
            )
        }

        // Display different text on login button
        binding.password.editText?.doAfterTextChanged {
            // Input validation
            loginViewModel.onLoginDataChange(
                binding.username.editText!!.text.toString(),
                binding.password.editText!!.text.toString()
            )

            when (binding.password.editText!!.text.length) {
                0 -> {
                    checkBiometricEnabled()
                }
                in 1..7 -> {
                    binding.btnLogin.isEnabled = false
                }
                // Enables login button with password
                in 8..50 -> binding.btnLogin.isEnabled = true
                else -> binding.btnLogin.setText(R.string.btn_login)
            }
        }

        // IME_ACTION_DONE action key performs a "done" operation,
        // meaning there is nothing more to input and the IME will be closed.
        binding.password.editText?.setOnEditorActionListener { _, actionId, _ ->
            when (actionId) {
                EditorInfo.IME_ACTION_DONE ->
                    loginViewModel.loginWithPassword(
                        binding.username.editText.toString(),
                        binding.password.editText.toString()
                    )
            }
            false
        }

        binding.btnLogin.setOnClickListener {
            val username = binding.username.editText.toString()
            val password = binding.password.editText.toString()

            if (biometricEnabled && biometricLogin) {
                // TODO - Trigger biometric prompt
                loginViewModel.loginWithBiometric(username, "biometric_password")
            } else {
                loginViewModel.loginWithPassword(
                    username,
                    password
                )
            }
        }

        // Navigate to register fragment
        binding.btnRegister.setOnClickListener {
//            navController.navigate(R.id.action_loginFragment_to_registerFragment)
        }
    }
}