package com.kevinwei.vote.activities.election

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kevinwei.vote.MainApplication
import com.kevinwei.vote.activities.login.LoginViewModel
import com.kevinwei.vote.model.Election
import com.kevinwei.vote.network.*

import com.kevinwei.vote.security.SessionManager
import kotlinx.coroutines.launch

import java.lang.Exception

class ElectionViewModel : ViewModel() {
    private val TAG = "ElectionViewModel"
    private val sessionManager: SessionManager = SessionManager(MainApplication.appContext)

    // ElectionAPI Results
    private val _apiResult = MutableLiveData<ApiResult>()
    val apiResult: LiveData<ApiResult> = _apiResult

    private val _data = MutableLiveData<List<Election>>()
    val electionData: LiveData<List<Election>> = _data

    fun getElections() {
        viewModelScope.launch {
            try {
                Log.d(TAG, "Getting Elections")
                val response = ElectionsApi.client.getElections()
                Log.d(TAG, response.toString())
                when (response ) {
                    is Result.Unauthenticated -> {
                        Log.d(TAG, "Unauthenticated")
                        _apiResult.value = FailedResult(unauthenticated = true)
                        sessionManager.removeAuthToken()
                    }
                    is Result.Success -> {
                        Log.d(TAG, response.body.toString())
                        when (response.body!!.success) {
                            "error" -> {
                                Log.d(TAG, "error")
                                // Unable to retrieve elections
                                _apiResult.value = FailedResult(response.body.error!!.message.toString())
                            }
                            "success" -> {
                                Log.d(TAG, "success")
                                _apiResult.value = SuccessResult(true)
                                _data.value = response.body.data!!

                            }
                        }
                    }
                    is Result.Error -> {
                        Log.d(TAG, "API Error")
                        _apiResult.value = FailedResult("Something went wrong. Try again later")
                    }
                    is Result.NetworkError -> {
                        Log.d(TAG, "Network Error")
                        _apiResult.value = FailedResult("Network error. Try again later")
                    }
                }


            } catch (e: Exception) {
                _apiResult.value = FailedResult(e.message.toString())
            } finally {
                // TODO (" Loading screen disabled")
            }
        }
    }

    /**
     * Navigation to a specific Ballot fragment
     */
    private val _navigateToBallot = MutableLiveData<Election?>()

    // State variable for navigation
    val navigateToBallot
        get() = _navigateToBallot

    // sets state variable when election clicked
    fun onElectionClicked(election: Election) {
        _navigateToBallot.value = election
    }

    // Call immediately on BallotFragment after navigation to clear navigation requests
    fun onBallotFragmentNavigated() {
        _navigateToBallot.value = null
    }
}

