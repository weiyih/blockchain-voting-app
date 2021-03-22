package com.kevinwei.vote.activities.login

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.biometric.BiometricManager.Authenticators
import androidx.core.widget.doAfterTextChanged
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.navigation.NavController
import androidx.navigation.fragment.findNavController
import com.kevinwei.vote.*
import com.kevinwei.vote.databinding.FragmentLoginBinding
import com.kevinwei.vote.security.SessionManager

class LoginFragment : Fragment() {

    private var _binding: FragmentLoginBinding? = null
    private val binding get() = _binding!!

    private val loginWithPasswordViewModel by viewModels<LoginViewModel>()
    private lateinit var navController: NavController

    private val cryptoManager = CryptographyManager()
    private val ciphertextWrapper
        get() = cryptoManager.getCiphertextWrapperFromSharedPrefs(
                requireActivity().applicationContext,
                SHARED_PREFS_FILENAME,
                Context.MODE_PRIVATE,
                CIPHERTEXT_WRAPPER
        )

    private val AUTHORIZED_BIOMETRICS =
            (Authenticators.DEVICE_CREDENTIAL or Authenticators.BIOMETRIC_STRONG)

    override fun onCreateView(
            inflater: LayoutInflater,
            container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentLoginBinding.inflate(inflater, container, false)

        setupLoginForPassword()
        return binding.root
    }

    override fun onStart() {
        super.onStart()
        requireActivity().actionBar?.hide()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        navController = findNavController()
    }

    private fun setupLoginForPassword() {
        loginWithPasswordViewModel.loginWithPasswordFormState.observe(
                viewLifecycleOwner,
                Observer { formState: LoginFormState ->
                    val loginState = formState ?: return@Observer

                    when (loginState) {
                        is SuccessLoginFormState ->
                            binding.btnLogin.isEnabled = loginState.isDataValid
                        is FailedLoginFormState -> {
                            loginState.usernameError?.let { binding.username.error = getString(it) }
                            loginState.passwordError?.let { binding.password.error = getString(it) }
                        }
                    }
                })

        loginWithPasswordViewModel.loginResult.observe(viewLifecycleOwner, Observer {
            val loginResult = it ?: return@Observer
            if (loginResult.SUCCESS) {
                Toast.makeText(context, "Logged In", Toast.LENGTH_LONG).show()

                // TODO("Check if USER is verified")
                // Navigate to ElectionFragment
                navController.navigate(R.id.action_loginFragment_to_electionFragment)

                // TODO("User unverified")
                // Navigate to ElectionFragment


            } else if (!loginResult.SUCCESS) {
                // Display error
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