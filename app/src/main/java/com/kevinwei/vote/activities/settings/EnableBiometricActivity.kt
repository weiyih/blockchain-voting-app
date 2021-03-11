package com.kevinwei.vote.activities.settings

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.kevinwei.vote.R
import com.kevinwei.vote.databinding.ActivityEnableBiometricBinding

class EnableBiometricActivity : AppCompatActivity() {
    private lateinit var binding: ActivityEnableBiometricBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_enable_biometric)
    }
}