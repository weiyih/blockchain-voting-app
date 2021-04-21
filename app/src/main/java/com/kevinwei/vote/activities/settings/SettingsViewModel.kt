package com.kevinwei.vote.activities.settings

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kevinwei.vote.model.BiometricRequest
import com.kevinwei.vote.network.ElectionsApi
import kotlinx.coroutines.launch
import java.lang.Exception

class SettingsViewModel : ViewModel() {
    private val TAG = "SettingsViewModel"

    // Biometric Registration Results
    private val _biometricResults = MutableLiveData<Boolean>()
    val biometricResults: LiveData<Boolean> = _biometricResults

    init {
        _biometricResults.value = true
    }

    fun registerBiometric(password: String) {
        viewModelScope.launch {
            try {
                Log.d(TAG, "Register biometric")
                // TODO (" Loading screen")
                val biometricRequest = BiometricRequest(password);
                val response = ElectionsApi.client.register(biometricRequest)
                Log.d(TAG, response.toString())
//                when (response) {
//                    is NetworkResponse.Success -> {
//                        _biometricResults.value = true
//                        Log.d(TAG, response.body.toString())
//                    }
//                    is NetworkResponse.Failure -> {
//                        _biometricResults.value = false
//                    }
//                    is NetworkResponse.NetworkError -> {
//                        _biometricResults.value = true
//
//                    }
//                    is NetworkResponse.UnknownError -> {
//                        _biometricResults.value = true
//                    }
//
//                }
            } catch (e: Exception) {
                Log.d(TAG, e.message.toString())
            } finally {
                // TODO (" Loading screen disabled")
            }
        }
    }
}