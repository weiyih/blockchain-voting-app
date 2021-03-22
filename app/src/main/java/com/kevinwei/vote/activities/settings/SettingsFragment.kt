package com.kevinwei.vote.activities.settings

import android.os.Bundle
import androidx.preference.PreferenceFragmentCompat
import com.kevinwei.vote.R

class SettingsFragment : PreferenceFragmentCompat() {
    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        TODO("Not yet implemented")
        setPreferencesFromResource(R.xml.root_preferences, rootKey)


    }


}