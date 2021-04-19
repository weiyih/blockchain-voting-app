package com.kevinwei.vote.activities.login

import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.widget.Toast
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
import com.kevinwei.vote.*
import com.kevinwei.vote.databinding.FragmentLoginBinding
import com.kevinwei.vote.security.BiometricPromptUtils


class LoginFragment : Fragment() {
    private val TAG = "LoginFragment"

    private val loginViewModel by activityViewModels<LoginViewModel>()
    private lateinit var navController: NavController
    private lateinit var sharedPrefs: SharedPreferences
    private lateinit var savedStateHandle: SavedStateHandle

    private var _binding: FragmentLoginBinding? = null
    private val binding get() = _binding!!

    private var biometricEnabled = false // Biometric hardware settings
    private var biometricLogin = false // Check if
    private var username: String = ""
    private val cryptographyManager = CryptographyManager()
    private val ciphertextWrapper
        get() = cryptographyManager.getCiphertextWrapperFromSharedPrefs(
            requireActivity().applicationContext,
            SHARED_PREFS_FILENAME,
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
        sharedPrefs = PreferenceManager.getDefaultSharedPreferences(requireContext());

        navController = findNavController()
//        savedStateHandle = findNavController().previousBackStackEntry!!.savedStateHandle
//        savedStateHandle.set(LOGIN_SUCCESSFUL, false)

        // Hide top action bar on login fragment
        (requireActivity() as MainActivity).supportActionBar!!.hide()

        getRememberedUsername()
        checkBiometricEnabled()
        setupLogin()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        // Re-enable app bar
        (requireActivity() as MainActivity).supportActionBar!!.show()
        _binding = null
    }


    private fun getRememberedUsername() {
        // TODO - replace with ""
        val usernameKey = getString(R.string.pref_usertoken)
        username = sharedPrefs.getString(usernameKey, "weiyih@sheridancollege.ca").toString()
        binding.username.editText?.setText(username)
    }

    // Checks and updates login button if biometrics have been successfully enabled
    private fun checkBiometricEnabled() {
        biometricEnabled = sharedPrefs.getBoolean(getString(R.string.pref_biometric), false)

        if (biometricEnabled) {
            biometricLogin = true
            binding.btnLogin.setText(R.string.btn_login_biometric)
        } else {
            biometricLogin = false
            binding.btnLogin.setText(R.string.btn_login)
        }
    }

    private fun setupLogin() {
        // LoginFORM
        loginViewModel.loginFormState.observe(
            viewLifecycleOwner,
            Observer { formState: LoginFormState ->
                val loginState = formState ?: return@Observer

                when (loginState) {
                    is SuccessLoginFormState -> {
                        binding.username.error = null
                        binding.password.error = null
                        binding.btnLogin.isEnabled = true
                    }
                    is FailedLoginFormState -> {
                        loginState.usernameError?.let { binding.username.error = getString(it) }
                        loginState.passwordError?.let { binding.password.error = getString(it) }
                        binding.btnLogin.isEnabled = false
                    }
                }
            })

        // Observe the loginResult from the login API
        loginViewModel.loginResult.observe(viewLifecycleOwner, Observer {
            // TODO - clear login from backstack()
//            clearNavStack()
            // TODO - navigate to verify or election
            navController.navigate(R.id.electionFragment)

//            loginViewModel.user.observe(viewLifecycleOwner, Observer { user ->
//                when (user.verified) {
//                    true -> navController.navigate(R.id.action_loginFragment_to_electionFragment)
//
//                    false -> navController.navigate(R.id.action_loginFragment_to_verifyFragment)
//                }
//            })
        })

        binding.username.editText?.doAfterTextChanged {
            // Input validation
            loginViewModel.onLoginDataChange(
                binding.username.editText!!.text.toString(),
                binding.password.editText!!.text.toString()
            )
        }

        binding.password.editText?.doAfterTextChanged {
            // Input validation
            loginViewModel.onLoginDataChange(
                binding.username.editText!!.text.toString(),
                binding.password.editText!!.text.toString()
            )

            // TODO ("Might be possible to refactor into the LoginFormState?")
            when (binding.password.editText!!.text.length) {
                // Check if biometric login is enabled
                0 -> {
                    checkBiometricEnabled()
                }
                // Disables login with password
                in 1..7 -> {
                    biometricLogin = false
                    binding.btnLogin.setText(R.string.btn_login)
                    binding.btnLogin.isEnabled = false
                }
                // Enables login button with password
                in 8..50 -> binding.btnLogin.isEnabled = true
            }
        }

        // IME_ACTION_DONE action key performs a "done" operation,
        // meaning there is nothing more to input and the IME will be closed.
        binding.password.editText?.setOnEditorActionListener { _, actionId, _ ->
            when (actionId) {
                EditorInfo.IME_ACTION_DONE ->
                    loginWithPassword()
            }
            false
        }

        binding.btnLogin.setOnClickListener {
            if (biometricEnabled && biometricLogin) {
                Toast.makeText(requireActivity(), "BIOMETRIC PROMPT", Toast.LENGTH_SHORT).show();
                showBiometricLoginPrompt()

            } else {
                loginWithPassword()
            }
        }

        // Navigate to register fragment
        binding.btnRegister.setOnClickListener {
            // TODO - Navigate to registration
//            navController.navigate(R.id.action_loginFragment_to_registerFragment)
        }
    }

    private fun clearNavStack() {
//        navController.popBackStack(R.id.loginFragment, true)
    }

    private fun showBiometricLoginPrompt() {
        Log.d(TAG, "ShowBiometricLoginPrompt");
        ciphertextWrapper?.let { textWrapper ->
            val biometricManager = BiometricManager.from(this.requireContext())
            val canAuthenticate =
                biometricManager.canAuthenticate(com.kevinwei.vote.AUTHORIZED_BIOMETRICS)


            if (canAuthenticate == BiometricManager.BIOMETRIC_SUCCESS) {
                val secretKey = getString(R.string.secret_key)
                val cipher = cryptographyManager.getInitializedCipherForDecryption(secretKey,
                    textWrapper.initializationVector)

                // BiometricPrompt callback
                val callback = object : BiometricPrompt.AuthenticationCallback() {
                    override fun onAuthenticationError(errorCode: Int, errString: CharSequence) {
                        super.onAuthenticationError(errorCode, errString)
                        Toast.makeText(context,
                            "Biometric Authentication Error: $errString", Toast.LENGTH_SHORT).show()
                    }

                    override fun onAuthenticationFailed() {
                        super.onAuthenticationFailed()
                        Toast.makeText(context, "Authentication Failed", Toast.LENGTH_SHORT).show()
                    }

                    override fun onAuthenticationSucceeded(result: BiometricPrompt.AuthenticationResult) {
                        super.onAuthenticationSucceeded(result)
                        loginWithBiometric(result)
                        // TODO - Enable login loading screen
                    }
                }

                val biometricPrompt = BiometricPromptUtils.createBiometricPrompt(requireActivity(), callback)
                val promptInfo = BiometricPromptUtils.loginBiometricPrompt(requireActivity())

                biometricPrompt.authenticate(promptInfo, BiometricPrompt.CryptoObject(cipher))
            }
        }
    }

    fun loginWithBiometric(authResult: BiometricPrompt.AuthenticationResult) {
        Log.d(TAG, "Prompt Success");
        // Decrypt biometric login token
        ciphertextWrapper?.let { textWrapper ->
            authResult.cryptoObject?.cipher?.let {
                Log.d(TAG, "Decrypt");
                val username = binding.username.editText!!.text.toString()
                val biometricToken = cryptographyManager.decryptData(textWrapper.ciphertext, it)
//                loginViewModel.loginWithBiometric(username, biometricToken)
                loginViewModel.testLogin()
            }
        }
    }

    fun loginWithPassword() {
        val username = binding.username.editText!!.text.toString()
        val password = binding.password.editText!!.text.toString()
//        loginViewModel.loginWithPassword(username, password)
    }
}