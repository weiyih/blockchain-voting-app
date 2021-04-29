package com.kevinwei.vote.activities.login

import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.widget.Toast
import androidx.activity.addCallback
import androidx.biometric.BiometricManager
import androidx.biometric.BiometricPrompt
import androidx.core.widget.doAfterTextChanged
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import androidx.lifecycle.SavedStateHandle
import androidx.navigation.NavController
import androidx.navigation.fragment.findNavController
import androidx.preference.PreferenceManager
import com.google.android.material.snackbar.Snackbar
import com.kevinwei.vote.*
import com.kevinwei.vote.databinding.FragmentLoginBinding
import com.kevinwei.vote.network.ApiResult
import com.kevinwei.vote.network.FailedResult
import com.kevinwei.vote.network.SuccessResult
import com.kevinwei.vote.security.BiometricPromptUtils


class LoginFragment : Fragment() {
    private val TAG = "LoginFragment"
    private var _binding: FragmentLoginBinding? = null
    private val binding get() = _binding!!

    private val loginViewModel by activityViewModels<LoginViewModel>()
    private lateinit var navController: NavController
    private lateinit var sharedPrefs: SharedPreferences
    private lateinit var savedStateHandle: SavedStateHandle


    // SharedPreference setting for biometric authentication
    private var biometricEnabled: Boolean = false

    // UI login for login with password or biometric
    private var biometricLogin: Boolean = false

    // CryptographyManager for the crypto object used in BiometricPrompt authentication
    private val cryptographyManager = CryptographyManager()
    private val ciphertextWrapper
        get() = cryptographyManager.getCiphertextWrapperFromSharedPrefs(
            requireActivity().applicationContext,
            SHARED_PREF_FILENAME,
            Context.MODE_PRIVATE,
            CIPHERTEXT_WRAPPER
        )

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        _binding = FragmentLoginBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        // Hide top action bar on login fragment
        (requireActivity() as MainActivity).supportActionBar!!.hide()

        sharedPrefs = PreferenceManager.getDefaultSharedPreferences(requireContext());
        biometricEnabled = sharedPrefs.getBoolean(getString(R.string.pref_biometric), false)

        navController = findNavController()

        // TODO - Prevent back navigation to electionFragment
        overrideOnBackPressed()

        getRememberedUsername()
        checkBiometricEnabled()
        setupLoginForm()
        setupLoginButton()
        setupRegisterButton()
        observeLoginResponse()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        (requireActivity() as MainActivity).supportActionBar!!.show()
        _binding = null
    }

    /*
    * Prevents the loginFragment from attempt back navigation to the ElectionFragment
    */
    private fun overrideOnBackPressed() {
        val callback = requireActivity().onBackPressedDispatcher.addCallback(this) { }
        callback.isEnabled
    }


    /*
    * getRemeberedUsername retrieves the stored username from SharedPreferences and populates the
    * username TextView
    */
    private fun getRememberedUsername() {
        val usernameKey = getString(R.string.pref_usertoken)
        val username = sharedPrefs.getString(usernameKey, "").toString()
        binding.username.editText!!.setText(username)
    }

    /*
    * checkBiometricEnabled
    * Retrieves the biometricEnabled settings from SharedPreferences
    * Updates the text of the login button and disables the UI login
    */
    private fun checkBiometricEnabled() {
        // Update login button text
        if (biometricEnabled) {
            biometricLogin = true
            binding.btnLogin.setText(R.string.btn_login_biometric)
        } else {
            biometricLogin = false
            binding.btnLogin.setText(R.string.btn_login)
        }
    }

    /*
    * setupLoginForm registers obsersers to manage the state of the
    */
    private fun setupLoginForm() {
        // Observer to display input validation errors
        loginViewModel.loginFormState.observe(
            viewLifecycleOwner,
            Observer { formState: LoginFormState ->
                val loginState = formState ?: return@Observer
                when (loginState) {
                    is SuccessLoginFormState -> {
                        binding.username.error = null
                        binding.password.error = null

                        when (!biometricEnabled && binding.password.editText!!.text.isEmpty()) {
                            true -> binding.btnLogin.isEnabled = false
                            false -> binding.btnLogin.isEnabled = true
                        }

                    }
                    is FailedLoginFormState -> {
                        binding.btnLogin.isEnabled = false
                        binding.username.error = null
                        binding.password.error = null
                        loginState.usernameError?.let { binding.username.error = getString(it) }
                        loginState.passwordError?.let { binding.password.error = getString(it) }
                    }
                }
            })

        // Triggers viewModel to validate username and password on changes to the username
        binding.username.editText?.doAfterTextChanged {
            // Input validation
            loginViewModel.onLoginDataChange(
                binding.username.editText!!.text.toString(),
                binding.password.editText!!.text.toString()
            )
        }

        // Triggers viewModel to validate username and password on changes to the password
        binding.password.editText?.doAfterTextChanged {
            // Input validation
            loginViewModel.onLoginDataChange(
                binding.username.editText!!.text.toString(),
                binding.password.editText!!.text.toString()
            )

            // UI Logic to determine the login behaviour of the login button
            when (binding.password.editText!!.text.length) {
                // Empty password field
                0 -> {
                    checkBiometricEnabled()
                }
                // Invalid password length triggers a disabled login with password
                in 1..7 -> {
                    biometricLogin = false
                    binding.btnLogin.setText(R.string.btn_login)
                    binding.btnLogin.isEnabled = false
                }
            }
        }
    }

    /*
    * observeLoginResponse registers observer to handle the Login API responses
    */
    private fun observeLoginResponse() {
        // Observe the loginResult from the login API
        loginViewModel.apiResult.observe(
            viewLifecycleOwner,
            Observer { apiResult: ApiResult ->
                val result = apiResult ?: return@Observer
                when (result) {
                    is SuccessResult -> {
                        // TODO - Disable progress bar
                        val user = loginViewModel.user.value!!
                        when (user.verified) {
                            true -> navController.navigate(R.id.action_loginFragment_to_electionFragment)
                            // TODO - Implement registration workflow use-case
                            false -> navController.navigate(R.id.action_loginFragment_to_electionFragment)
                        }
                    }
                    is FailedResult -> {
                        // TODO - Disable progress bar
                        result.error?.let {
                            Toast.makeText(requireContext(), it, Toast.LENGTH_SHORT).show()
                        }
                    }
                }
            })
    }


    /*
    * setupLoginButton registers behaviour to determine biometric authentication or password login
    */
    private fun setupLoginButton() {
        binding.btnLogin.setOnClickListener {
            val username = binding.username.editText!!.text.toString()
            val password = binding.password.editText!!.text.toString()

            // User enabled biometrics and no password has been entered
            if (biometricEnabled && biometricLogin) {
                showBiometricLoginPrompt(username)
            } else {
                loginWithPassword(username, password)
            }
        }
    }

    /*
    * setupRegisterButton registers behaviour to navigate to the registrationFragment
    */
    private fun setupRegisterButton() {
        binding.btnRegister.isEnabled = false
        // Navigate to register fragment
        binding.btnRegister.setOnClickListener {
            // TODO - Navigate to registration
//            navController.navigate(R.id.action_loginFragment_to_registerFragment)
        }
    }

    /*
    * Displays a biometricPrompt for login
    */
    private fun showBiometricLoginPrompt(username: String) {
        ciphertextWrapper?.let { textWrapper ->
            val biometricManager = BiometricManager.from(this.requireContext())
            val canAuthenticate =
                biometricManager.canAuthenticate(com.kevinwei.vote.AUTHORIZED_BIOMETRICS)

            // Safety check to ensure user can successfully authenticate
            if (canAuthenticate == BiometricManager.BIOMETRIC_SUCCESS) {

                //Decrypt biometric password token
                val secretKey = getString(R.string.secret_key)
                val cipher = cryptographyManager.getInitializedCipherForDecryption(secretKey,
                    textWrapper.initializationVector)

                // BiometricPrompt callback to respond to the AuthenticationCallback events
                val callback = object : BiometricPrompt.AuthenticationCallback() {
                    // User canceled
                    override fun onAuthenticationError(
                        errorCode: Int,
                        errString: CharSequence,
                    ) {
                        super.onAuthenticationError(errorCode, errString)
                    }

                    // Valid biometric but not recognized
                    override fun onAuthenticationFailed() {
                        super.onAuthenticationFailed()
                        Toast.makeText(context, "Authentication Failed", Toast.LENGTH_SHORT)
                            .show()
                    }

                    // Biometric is recognized
                    override fun onAuthenticationSucceeded(result: BiometricPrompt.AuthenticationResult) {
                        super.onAuthenticationSucceeded(result)
                        loginWithBiometric(result, username)
                    }


                }

                val biometricPrompt =
                    BiometricPromptUtils.createBiometricPrompt(requireActivity(), callback)
                val promptInfo = BiometricPromptUtils.loginBiometricPrompt(requireActivity())

                biometricPrompt.authenticate(promptInfo, BiometricPrompt.CryptoObject(cipher))
            }
        }
    }

    /*
    * loginWithBiometric decrypts the biometric password and attempts to login
    */
    fun loginWithBiometric(authResult: BiometricPrompt.AuthenticationResult, username: String) {
        // Decrypt biometric login token
        ciphertextWrapper?.let { textWrapper ->
            authResult.cryptoObject?.cipher?.let {
                val biometricToken = cryptographyManager.decryptData(textWrapper.ciphertext, it)
                loginViewModel.login(username, biometricToken)
            }
        }
    }

    /*
    * loginWithPassword attempts to login with the user input username and password
    */
    fun loginWithPassword(username: String, password: String) {
        loginViewModel.login(username, password)
    }
}