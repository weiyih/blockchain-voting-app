package com.kevinwei.vote.activities.login

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.biometric.BiometricManager.Authenticators
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.navigation.NavController
import androidx.navigation.fragment.findNavController
import com.kevinwei.vote.*
import com.kevinwei.vote.databinding.FragmentLoginBinding

class LoginFragment : Fragment() {

    private var binding: FragmentLoginBinding? = null

    private val loginWithPasswordViewModel by viewModels<LoginViewModel>()
    private lateinit var navController: NavController

    private val cryptoManager = CryptographyManager()

//    private val ciphertextWrapper
//        get() = cryptoManager.getCiphertextWrapperFromSharedPrefs(
//            applicationContext,
//            SHARED_PREFS_FILENAME,
//            Context.MODE_PRIVATE,
//            CIPHERTEXT_WRAPPER
//        )

    private val AUTHORIZED_BIOMETRICS =
        (Authenticators.DEVICE_CREDENTIAL or Authenticators.BIOMETRIC_STRONG)

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
//        val binding = DataBindingUtil.inflate<FragmentLoginBinding>(
//            inflater,
//            R.layout.fragment_login,
//            container,
//            false
//        )
        binding = FragmentLoginBinding.inflate(inflater, container, false)
//        setupLoginForPassword()

        activity?.actionBar?.hide()

        return binding!!.root
    }

    override fun onStart() {
        super.onStart()
        requireActivity().actionBar?.hide()
    }

    override fun onDestroyView() {
        binding = null
        super.onDestroyView()
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        navController = findNavController()
    }

//    private fun setupLoginForPassword() {
//        loginWithPasswordViewModel.loginWithPasswordFormState.observe(
//            this,
//            Observer { formState: LoginFormState ->
//                val loginState = formState ?: return@Observer
//
//                when (loginState) {
////                    is SuccessLoginFormState -> binding.btnLogin.isEnabled = loginState.isDataValid
////                    is FailedLoginFormState -> {
////                        loginState.usernameError?.let { binding.username.error = getString(it) }
////                        loginState.passwordError?.let { binding.password.error = getString(it) }
////                    }
//                }
//            })
//
//        loginWithPasswordViewModel.loginResult.observe(this, Observer {
//            val loginResult = it ?: return@Observer
//            if (loginResult.success) {
////                Toast.makeText(this, "Logged In", Toast.LENGTH_LONG).show()
//
//                // Navigate to MainIntent
////                val intent = Intent(this,MainActivity::class.java).apply {}
////                startActivity(intent)
//            }
//        })
////
//        binding.username.editText?.doAfterTextChanged {
//            loginWithPasswordViewModel.onLoginDataChange(
//                binding.username.editText.toString(),
//                binding.password.editText.toString()
//            )
//        }
//
//        binding.btnLogin.setOnClickListener {
//            loginWithPasswordViewModel.loginWithPassword(
//                binding.username.editText.toString(),
//                binding.password.editText.toString()
//            )
    }
//    }
//}