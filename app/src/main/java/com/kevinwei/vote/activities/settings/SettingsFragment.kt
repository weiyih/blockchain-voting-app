package com.kevinwei.vote.activities.settings

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.provider.Settings
import android.util.Log
import android.widget.Toast
import androidx.biometric.BiometricManager
import androidx.biometric.BiometricPrompt
import androidx.lifecycle.lifecycleScope
import androidx.preference.*
import com.kevinwei.vote.*
import com.kevinwei.vote.R
import com.kevinwei.vote.activities.login.LoginResult
import com.kevinwei.vote.activities.login.LoginViewModel
import com.kevinwei.vote.model.BiometricToken
import com.kevinwei.vote.network.ElectionsApi
import com.kevinwei.vote.security.BiometricPromptUtils
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch
import java.lang.Exception
import java.util.*


class SettingsFragment : PreferenceFragmentCompat() {
    private val TAG = "SettingsFragment"
    private lateinit var sharedPreferences: SharedPreferences
    private lateinit var biometricPrompt: BiometricPrompt

    private var cryptographyManager = CryptographyManager()
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
            }
        }

        return super.onPreferenceTreeClick(preference)
    }

    private fun showBiometricPrompt() {
        val biometricManager = BiometricManager.from(this.requireContext())
        when (biometricManager.canAuthenticate(AUTHORIZED_BIOMETRICS)) {
            BiometricManager.BIOMETRIC_SUCCESS -> {
                enableBiometricPrompt()
            }

            // No biometric hardware
            BiometricManager.BIOMETRIC_ERROR_NO_HARDWARE, BiometricManager.BIOMETRIC_ERROR_HW_UNAVAILABLE -> {
                Log.e(TAG, "BIOMETRIC_ERROR_HW_UNAVAILABLE")
                Toast.makeText(requireContext(),
                    "ERROR: BIOMETRIC_ERROR_HW_UNAVAILABLEN",
                    Toast.LENGTH_SHORT).show()
            }

            // No biometric authentication enrolled on the device
            BiometricManager.BIOMETRIC_ERROR_NONE_ENROLLED -> {
                Log.e(TAG, "BIOMETRIC_ERROR_NONE_ENROLLED")
                Toast.makeText(requireContext(),
                    getString(R.string.biometric_enabled_message),
                    Toast.LENGTH_SHORT).show()
                launchBiometricEnroll()
            }
            BiometricManager.BIOMETRIC_ERROR_SECURITY_UPDATE_REQUIRED -> {
                Log.e(TAG, "BIOMETRIC_ERROR_SECURITY_UPDATE_REQUIRED")
                Toast.makeText(requireContext(),
                    "ERROR: BIOMETRIC_ERROR_SECURITY_UPDATE_REQUIRED",
                    Toast.LENGTH_SHORT).show()
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
    private fun enableBiometricPrompt() {
        val biometricManager = BiometricManager.from(this.requireContext())
        val canAuthenticate =
            biometricManager.canAuthenticate(com.kevinwei.vote.AUTHORIZED_BIOMETRICS)


        if (canAuthenticate == BiometricManager.BIOMETRIC_SUCCESS) {
            // BiometricPrompt callback
            val callback = object : BiometricPrompt.AuthenticationCallback() {
                override fun onAuthenticationError(errorCode: Int, errString: CharSequence) {
                    super.onAuthenticationError(errorCode, errString)
                    Toast.makeText(context,
                        "Unable to enable biometrics: $errString", Toast.LENGTH_SHORT).show()
                    updateFailedBiometricPrefs();
                }

                override fun onAuthenticationFailed() {
                    super.onAuthenticationFailed()
                    Toast.makeText(context, "Unable to authenticate biometrics", Toast.LENGTH_SHORT)
                        .show()
                    updateFailedBiometricPrefs();
                }

                override fun onAuthenticationSucceeded(result: BiometricPrompt.AuthenticationResult) {
                    super.onAuthenticationSucceeded(result)
                    encryptAndStoreBiometricToken(result)
                }
            }
            val secretKey = getString(R.string.secret_key)
            cryptographyManager = CryptographyManager()
            val cipher = cryptographyManager.getInitializedCipherForEncryption(secretKey)

            val biometricPrompt =
                BiometricPromptUtils.createBiometricPrompt(requireActivity(), callback)
            val promptInfo = BiometricPromptUtils.enableBiometricPrompt(requireActivity())

            biometricPrompt.authenticate(promptInfo, BiometricPrompt.CryptoObject(cipher))
        }
    }

    private fun encryptAndStoreBiometricToken(authResult: BiometricPrompt.AuthenticationResult) {
        authResult.cryptoObject?.cipher?.apply {
            // Generate a UUID and register it on the server
            val uniqueID: String = UUID.randomUUID().toString()

            // TODO
            viewLifecycleOwner.lifecycleScope.launch {
                try {
                    val biometricToken = BiometricToken(uniqueID)
                    ElectionsApi.client.registerBiometricLogin(biometricToken)
                } catch (e: Exception) {

                }
            }

            // might be better to move to own thread
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

    private fun updateFailedBiometricPrefs() {
        with(sharedPreferences.edit()) {
            putBoolean(getString(R.string.pref_biometric), false)
            apply()
        }
        val pref: SwitchPreferenceCompat = findPreference(getString(R.string.pref_biometric))!!
        pref.isChecked = false
    }

}