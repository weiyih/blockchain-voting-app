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

    init {
        getElections()
    }

    fun getElections() {
        viewModelScope.launch {
            try {
                when (val response = ElectionsApi.client.getElections()) {
                    is Result.Unauthenticated -> {
                        _apiResult.value = FailedResult(unauthenticated = true)
                        sessionManager.removeAuthToken()
                    }
                    is Result.Success -> {
                        when (response.body!!.success) {
                            "error" -> {
                                // Unable to retrieve elections
                                _apiResult.value = FailedResult(response.body.error!!.message.toString())
                            }
                            "success" -> {
                                _apiResult.value = SuccessResult(true)
                                _data.value = response.body.data!!

                            }
                        }
                    }
                    is Result.Error -> {
                        _apiResult.value = FailedResult("Something went wrong. Try again later")
                    }
                    is Result.NetworkError -> {
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

    // state variable for navigation
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

