package com.kevinwei.vote.activities.settings

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.provider.Settings
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.biometric.BiometricManager
import androidx.biometric.BiometricPrompt
import androidx.lifecycle.lifecycleScope
import androidx.preference.*
import com.kevinwei.vote.*
import com.kevinwei.vote.R
import com.kevinwei.vote.model.BiometricToken
import com.kevinwei.vote.model.User
import com.kevinwei.vote.network.ElectionsApi
import com.kevinwei.vote.security.BiometricPromptUtils
import kotlinx.coroutines.launch
import java.util.*
import kotlin.coroutines.coroutineContext


class SettingsFragment : PreferenceFragmentCompat() {
    private val TAG = "SettingsFragment"
    private lateinit var sharedPreferences: SharedPreferences
    private lateinit var biometricPrompt: BiometricPrompt

    private val cryptographyManager = CryptographyManager()
    private val ciphertextWrapper
        get() = cryptographyManager.getCiphertextWrapperFromSharedPrefs(
            requireActivity().applicationContext,
            SHARED_PREFS_FILENAME,
            Context.MODE_PRIVATE,
            CIPHERTEXT_WRAPPER
        )

    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        // Load the preferences from an XML resource

        setPreferencesFromResource(R.xml.root_preferences, rootKey)
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this.context)
    }

    override fun onPreferenceTreeClick(preference: Preference?): Boolean {
        when (preference?.key) {
            getString(R.string.pref_biometric) -> {
                val value = sharedPreferences.getBoolean(getString(R.string.pref_biometric), false)
                when (value) {
                    true -> showBiometricPrompt()
                    false ->
                        // TODO ("Show biometric requirement warning")
                        // TODO ("Unregister biometric login")
                        Toast.makeText(requireContext(),
                            "SHOW BIOMETRIC WARNING",
                            Toast.LENGTH_SHORT).show()
                }
            }
            getString(R.string.pref_notification) -> {
                val value =
                    sharedPreferences.getBoolean(getString(R.string.pref_notification), false)
                Toast.makeText(requireContext(), value.toString(), Toast.LENGTH_SHORT).show()
            }
        }

        return super.onPreferenceTreeClick(preference)
    }

    private fun showBiometricPrompt() {
        val biometricManager = BiometricManager.from(this.requireContext())
        when (biometricManager.canAuthenticate(AUTHORIZED_BIOMETRICS)) {
            BiometricManager.BIOMETRIC_SUCCESS -> {
                registerBiometric()
            }

            // No biometric hardware
            BiometricManager.BIOMETRIC_ERROR_NO_HARDWARE, BiometricManager.BIOMETRIC_ERROR_HW_UNAVAILABLE -> {
                Toast.makeText(requireContext(),
                    "No biometric features available on this device.",
                    Toast.LENGTH_SHORT).show()
            }

            // No biometric authentication enrolled on the device
            BiometricManager.BIOMETRIC_ERROR_NONE_ENROLLED -> {
                Log.e("MY_APP_TAG", "BIOMETRIC_ERROR_NONE_ENROLLED")
                Toast.makeText(requireContext(),
                    getString(R.string.biometric_enabled_message),
                    Toast.LENGTH_SHORT).show()
                // TODO("Remove previous cipher")
                launchBiometricEnroll()
            }
            BiometricManager.BIOMETRIC_ERROR_SECURITY_UPDATE_REQUIRED -> {
                Toast.makeText(requireContext(),
                    "Security device out of date.",
                    Toast.LENGTH_SHORT).show()
                // TODO("Display error prompt")
            }
        }
    }

    // Creates a system intent to register a fingerprint
    private fun launchBiometricEnroll() {
        val enrollIntent = Intent(Settings.ACTION_BIOMETRIC_ENROLL).apply {
            putExtra(
                Settings.EXTRA_BIOMETRIC_AUTHENTICATORS_ALLOWED,
                AUTHORIZED_BIOMETRICS
            )
        }
        activity?.startActivity(enrollIntent)
    }


    /*
    * Biometric Settings
    */
    private fun createBiometricPrompt(): BiometricPrompt {
        val executor = requireContext().mainExecutor

        val callback = object : BiometricPrompt.AuthenticationCallback() {
            override fun onAuthenticationError(errorCode: Int, errString: CharSequence) {
                super.onAuthenticationError(errorCode, errString)
                Log.d(TAG, "$errorCode :: $errString")
                Toast.makeText(activity, "Biometric authentication failed", Toast.LENGTH_SHORT)
                    .show()
                updateFailedBiometricPrefs()
            }

            override fun onAuthenticationFailed() {
                super.onAuthenticationFailed()
                Log.d(TAG, "Authentication failed for an unknown reason")
                Toast.makeText(activity, "Biometric authentication failed", Toast.LENGTH_SHORT)
                    .show()

                updateFailedBiometricPrefs()
            }

            override fun onAuthenticationSucceeded(result: BiometricPrompt.AuthenticationResult) {
                super.onAuthenticationSucceeded(result)

                Log.d(TAG, "Authentication was successful")
                Toast.makeText(activity,
                    getString(R.string.biometric_enabled_message),
                    Toast.LENGTH_SHORT)
                    .show()
                encryptAndStoreBiometricToken(result)
            }
        }

        return BiometricPrompt(requireActivity(), executor, callback)
    }


    private fun updateFailedBiometricPrefs() {
        with(sharedPreferences.edit()) {
            putBoolean(getString(R.string.pref_biometric), false)
            apply()
        }
        val pref: SwitchPreferenceCompat = findPreference(getString(R.string.pref_biometric))!!
        pref.isChecked = false
    }

    private fun enableBiometricPrompt(): BiometricPrompt.PromptInfo {
        // TODO ("Bug - setConfirmationRequired not working")
        val promptInfo = BiometricPrompt.PromptInfo.Builder().apply {
            setTitle(getString(R.string.prompt_bio_title))
            setSubtitle(getString(R.string.prompt_bio_subtitle_enable))
            // TODO ("Bug - setConfirmationRequired(true) not working")
            setConfirmationRequired(false)
            setNegativeButtonText(getString(R.string.cancel))
        }.build()
        // .setDeviceCredentialAllowed(true) // Allow PIN/pattern/password authentication.
        // setDeviceCredentialAllowed and setNegativeButtonText are exclusive options
        return promptInfo
    }

    private fun registerBiometric() {
        val biometricManager = BiometricManager.from(this.requireContext())
        val canAuthentication = biometricManager.canAuthenticate(AUTHORIZED_BIOMETRICS)

        if (canAuthentication == BiometricManager.BIOMETRIC_SUCCESS) {

            val secretKey = getString(R.string.secret_key)
            val cipher = cryptographyManager.getInitializedCipherForEncryption(secretKey)

            //NOTE: :: converts function into lambda
            biometricPrompt = createBiometricPrompt()
            val promptInfo = enableBiometricPrompt()
            biometricPrompt.authenticate(promptInfo, BiometricPrompt.CryptoObject(cipher))
        }
    }

    private fun encryptAndStoreBiometricToken(authenticationResult: BiometricPrompt.AuthenticationResult) {
        authenticationResult.cryptoObject?.cipher?.apply {
            // Generate a GUID and register it on the server
            val uniqueID: String = UUID.randomUUID().toString()

            // TODO ("Handle failed network registration")
            viewLifecycleOwner.lifecycleScope.launch {
                val biometricToken = BiometricToken(uniqueID)
                ElectionsApi.retrofitService.registerBiometricLogin(biometricToken)
            }

            val encryptedBiometricTokenWrapper = cryptographyManager.encryptData(uniqueID, this)
            cryptographyManager.persistCiphertextWrapperToSharedPrefs(
                encryptedBiometricTokenWrapper,
                requireContext().applicationContext,
                SHARED_PREFS_FILENAME,
                Context.MODE_PRIVATE,
                CIPHERTEXT_WRAPPER
            )
        }
    }
}