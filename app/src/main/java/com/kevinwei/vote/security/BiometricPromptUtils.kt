package com.kevinwei.vote.security

import androidx.biometric.BiometricPrompt
import androidx.core.content.ContextCompat
import androidx.fragment.app.FragmentActivity
import com.kevinwei.vote.R
import com.kevinwei.vote.activities.ballot.BallotFragment
import com.kevinwei.vote.activities.login.LoginFragment

/*
BiometricPromptUtils
Used to generate Biometric Authentication Prompts to user


BiometricPrompt class manages system-provided biometric prompts.
API 28 and above (Android 9.0) will show supported system-provided authentication prompts
API 28 and below will show custom authentication dialog

Prompts persist accross configuration changes unless explicitly cancelled.

If application exits foreground, prmompts are dismissed for security reasons

IMPORTANT!
Persistent authentication accross configuartion changes requires (re)creation of prompt everytime
the activity/fragment is created. Instantiate prompt with callback early in the activity lifecycle.
(e.g. in onCreate()) will ensure ongoing authentication sessions callbacks to be received by the new
instance.

Note that cancelAuthentication() should not be called, and authenticate() does not need to be invoked
during activity/fragment creation.
 */

object BiometricPromptUtils {
    private const val TAG = "BiometricPromptUtils"

    // Create Biometric Prompt that returns a callback
    fun createBiometricPrompt(
        activity: FragmentActivity,
        callback: BiometricPrompt.AuthenticationCallback
    ): BiometricPrompt {
        // Return an Executor that will run enqueued tasks on the main thread associated with this context.
        val executor = ContextCompat.getMainExecutor(activity)

        /*
        Move callback into creation of BiometricPrompt to handle AuthenticationCallback
        val callback = object : BiometricPrompt.AuthenticationCallback() {
            override fun onAuthenticationError(errorCode: Int, errString: CharSequence) {
                super.onAuthenticationError(errorCode, errString)
            }

            override fun onAuthenticationFailed() {
                super.onAuthenticationFailed()
                Log.d(TAG, "User biometric rejected.")
            }

            override fun onAuthenticationSucceeded(result: BiometricPrompt.AuthenticationResult) {
                super.onAuthenticationSucceeded(result)
                Log.d(TAG, "Biometric authentication successful")
            }
        }
        */
        return BiometricPrompt(activity, executor, callback)
    }

    /*
    * Biometric Prompt displays for enabling, login, and voting
    * NOTE: setConfirmationRequired only displays with face unlock. Fingerprint is considered confirmation
    */
    fun enableBiometricPrompt(activity: FragmentActivity): BiometricPrompt.PromptInfo =
        BiometricPrompt.PromptInfo.Builder().apply {
            setTitle(activity.getString(R.string.prompt_bio_title))
            setSubtitle(activity.getString(R.string.prompt_bio_subtitle_enable))
            setConfirmationRequired(true)
            setNegativeButtonText(activity.getString(R.string.cancel))
        }.build()

    fun loginBiometricPrompt(activity: FragmentActivity): BiometricPrompt.PromptInfo =
        BiometricPrompt.PromptInfo.Builder().apply {
            setTitle(activity.getString(R.string.prompt_bio_title))
            setSubtitle(activity.getString(R.string.prompt_bio_subtitle_login))
            setConfirmationRequired(true)
            setNegativeButtonText(activity.getString(R.string.cancel))
        }.build()

    fun voteBiometricPrompt(activity: FragmentActivity): BiometricPrompt.PromptInfo =
        BiometricPrompt.PromptInfo.Builder().apply {
            setTitle(activity.getString(R.string.prompt_bio_title))
            setSubtitle(activity.getString(R.string.prompt_bio_subtitle_vote))
            setConfirmationRequired(true)
            setNegativeButtonText(activity.getString(R.string.cancel))
        }.build()
}