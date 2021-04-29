package com.kevinwei.vote

import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.NavigationUI
import androidx.preference.PreferenceManager
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.kevinwei.vote.security.SessionManager
import com.kevinwei.vote.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding

    lateinit var sessionManager: SessionManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
//        val binding = DataBindingUtil.setContentView<ActivityMainBinding>(this, R.layout.activity_main)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val navController = this.findNavController(R.id.navHostFragment)
        val appBarConfiguration = AppBarConfiguration(navController.graph)
        NavigationUI.setupActionBarWithNavController(this, navController, appBarConfiguration)

    }

    override fun onDestroy() {
        super.onDestroy()
        sessionManager = SessionManager(applicationContext)
        sessionManager.removeAuthToken()
    }

    override fun onSupportNavigateUp(): Boolean {
        val navController = this.findNavController(R.id.navHostFragment)

        // Dirty hack to display message on SettingsFragment for the AppBar Navigate Up
        val sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this)
        var confirm = sharedPreferences.getBoolean(getString(R.string.pref_biometric), false)
        if (!confirm) {
            MaterialAlertDialogBuilder(this)
                .setTitle("Biometric Authentication")
                .setMessage("Biometric Authentication must be enabled for the application to work. Please enable biometrics in the settings.")
                .setPositiveButton("OK") { _, _ ->
                }
                .show()
        }

        return if (!confirm) {
            false
        } else {
            navController.navigateUp()
        }

    }

}