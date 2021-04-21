package com.kevinwei.vote.activities.ballot

import android.util.Log
import android.widget.Toast
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kevinwei.vote.MainApplication
import com.kevinwei.vote.activities.login.LoginViewModel
import com.kevinwei.vote.model.BallotRequest
import com.kevinwei.vote.model.Candidate
import com.kevinwei.vote.model.LoginRequest
import com.kevinwei.vote.network.*
import com.kevinwei.vote.security.SessionManager
import kotlinx.coroutines.launch

class BallotViewModel : ViewModel() {
    private val TAG = "BallotViewModel"
    private val sessionManager: SessionManager = SessionManager(MainApplication.appContext)

    // Candidate list data
    private val _candidateData = MutableLiveData<List<Candidate>>()
    val candidateData: LiveData<List<Candidate>> = _candidateData

    // LoadBallot API Results
    private val _apiLoadResult = MutableLiveData<ApiResult>()
    val apiLoadResult: LiveData<ApiResult> = _apiLoadResult

    // SubmitBallot API Results
    private val _apiSubmitResult = MutableLiveData<ApiResult>()
    val apiSubmitResult: LiveData<ApiResult> = _apiSubmitResult

    // Ballot Transaction details
    private val _selectedCandidate = MutableLiveData<Candidate?>()
    val selectedCandidate: LiveData<Candidate?> = _selectedCandidate
    private var _electionId: String = ""
    private var _districtId: Int = 0


    // Retrieve ballot
    fun getBallot(electionId: String) {
        _electionId = electionId

        viewModelScope.launch {
            try {
                when (val response = ElectionsApi.client.getBallot(electionId)) {
                    is Result.Unauthenticated -> {
                        _apiLoadResult.value = FailedResult(unauthenticated = true)
                        sessionManager.removeAuthToken()
                    }
                    is Result.Success -> {
                        when (response.body!!.success) {
                            "error" -> {
                                // Unable to retrieve ballot
                                _apiSubmitResult.value = FailedResult(response.body!!.data.error)
                            }
                            "success" -> {
                                _candidateData.value = response.body!!.data.candidateList
                                _apiLoadResult.value = SuccessResult(true)
                            }
                        }
                    }
                    is Result.Error -> {
                        //Something went wrong
                        _apiLoadResult.value = FailedResult("Something went wrong. Try again later")
                    }
                    is Result.NetworkError -> {
                        _apiLoadResult.value = FailedResult("Network error. Try again later")
                    }
                }
            } catch (e: Exception) {
                _apiLoadResult.value = FailedResult(e.message.toString())
                _candidateData.value = listOf<Candidate>()
                // throw Exception("Unable to retrieve ballot")
            }
        }
    }

    fun submitBallot() {
        viewModelScope.launch {
            try {
                val candidate = selectedCandidate.value!!

                val ballotVote = BallotRequest(
                    _electionId,
                    _districtId,
                    candidate.candidateId,
                )
                when (val response = ElectionsApi.client.submit(_electionId, ballotVote)) {
                    is Result.Unauthenticated -> {
                        _apiSubmitResult.value = FailedResult(unauthenticated = true)
                        sessionManager.removeAuthToken()
                    }
                    is Result.Success -> {
                        when (response.body!!.success) {
                            "error" -> {
                                // Unable to retrieve ballot
                                _apiSubmitResult.value = FailedResult(response.body!!.data.message)
                            }
                            "success" -> {
                                _apiSubmitResult.value = SuccessResult(true)
                            }
                        }
                    }
                    is Result.Error -> {
                        //Something went wrong
                        _apiSubmitResult.value = FailedResult("Unable to submit ballot")
                    }
                    is Result.NetworkError -> {
                        _apiSubmitResult.value = FailedResult("Network error: Unable to submit ballot. ")
                    }
                }
            } catch (e: Exception) {
                _candidateData.value = listOf<Candidate>()
                // throw Exception("Unable to retrieve ballot")
            }
        }
    }


    /**
     * Navigation to a vote confirmation fragment
     */

// sets state variable when candidate clicked
    fun onCandidateSelected(position: Int, candidate: Candidate) {


        for (i in _candidateData.value!!.indices) {
            if (i == position) {
                _selectedCandidate.value =
                    if (_selectedCandidate.value == candidate) null else candidate
                _candidateData.value!![i].selected = !_candidateData.value!![i].selected
            } else {
                _candidateData.value!![i].selected = false
            }
        }
    }

    private val _navigateToBallotConfirm = MutableLiveData<Candidate?>()

    // state variable for navigation
    val navigateToBallot
        get() = _navigateToBallotConfirm


    // Call immediately on BallotFragment after navigation to clear navigation requests
    fun onBallotFragmentNavigated() {
        _navigateToBallotConfirm.value = null
    }
}
