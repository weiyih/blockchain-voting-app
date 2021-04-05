package com.kevinwei.vote.activities.login

import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.widget.Toast
import androidx.biometric.BiometricManager.Authenticators
import androidx.core.widget.doAfterTextChanged
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.lifecycle.SavedStateHandle
import androidx.navigation.NavController
import androidx.navigation.fragment.findNavController
import com.kevinwei.vote.*
import com.kevinwei.vote.databinding.FragmentLoginBinding

class LoginFragment : Fragment() {
    companion object {
        const val LOGIN_SUCCESSFUL: String = "LOGIN_SUCCESSFUL"
    }

    private val loginViewModel by activityViewModels<LoginViewModel>()
    private lateinit var navController: NavController
    private lateinit var sharedPref: SharedPreferences
    private lateinit var savedStateHandle: SavedStateHandle

    private var _binding: FragmentLoginBinding? = null
    private val binding get() = _binding!!

    private var biometricEnabled = false // Biometric hardware settings
    private var biometricLogin = false // Check if

    private val AUTHORIZED_BIOMETRICS =
        (Authenticators.DEVICE_CREDENTIAL or Authenticators.BIOMETRIC_STRONG)
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
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        sharedPref = this.requireActivity().getPreferences(Context.MODE_PRIVATE)
        navController = findNavController()
        savedStateHandle = findNavController().previousBackStackEntry!!.savedStateHandle
        savedStateHandle.set(LOGIN_SUCCESSFUL, false)

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
        val username = sharedPref.getString(getString(R.string.username), "")
        binding.username.editText?.setText(username)
    }

    // Checks and updates login button if biometrics have been successfully enabled
    private fun checkBiometricEnabled() {
        biometricEnabled = sharedPref.getBoolean(getString(R.string.saved_biometric), false)


        if (biometricEnabled) {
            biometricLogin = true
            binding.btnLogin.setText(R.string.btn_login_biometric)
        } else {
            biometricLogin = false
            binding.btnLogin.setText(R.string.btn_login)
        }
        // Enable login button if biometric login is enabled
        binding.btnLogin.isEnabled = biometricEnabled
    }

    private fun setupLogin() {
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

        // Observe the loginResult from the login API
        loginViewModel.loginResult.observe(viewLifecycleOwner, Observer {

            // TODO ("Replace")
            // TODO ("BUG - NavStack fix going to election fragment")
//            navController.popBackStack()
            navController.navigate(R.id.electionFragment)

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
                // TODO - Trigger biometric prompt
                Toast.makeText(context, "Biometric Prompt", Toast.LENGTH_SHORT).show()
                loginWithBiometric()
            } else {
                loginWithPassword()
            }
        }

        // Navigate to register fragment
        binding.btnRegister.setOnClickListener {
//            navController.navigate(R.id.action_loginFragment_to_registerFragment)
        }
    }

    fun loginWithPassword() {
        val username = binding.username.editText!!.text.toString()
        val password = binding.password.editText!!.text.toString()

//        loginViewModel.loginWithPassword(username, password)
        loginViewModel.testLogin()
    }

    fun loginWithBiometric() {
        val username = binding.username.editText!!.text.toString()
        loginViewModel.loginWithBiometric(username, "password")
    }
}