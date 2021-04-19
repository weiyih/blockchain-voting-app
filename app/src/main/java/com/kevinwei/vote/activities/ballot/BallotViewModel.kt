package com.kevinwei.vote.activities.ballot

import android.util.Log
import android.widget.Toast
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kevinwei.vote.MainApplication
import com.kevinwei.vote.activities.login.LoginResult
import com.kevinwei.vote.model.BallotVote
import com.kevinwei.vote.model.Candidate
import com.kevinwei.vote.network.ElectionsApi
import com.kevinwei.vote.network.NetworkResponse
import kotlinx.coroutines.launch

class BallotViewModel : ViewModel() {
    private val TAG = "BallotViewModel"

    // Candidate list data
    private val _candidateData = MutableLiveData<List<Candidate>>()
    val candidateData: LiveData<List<Candidate>> = _candidateData

    // Selected candidate
    private val _selectedCandidate = MutableLiveData<Candidate?>()
    val selectedCandidate: LiveData<Candidate?> = _selectedCandidate

    // Login Results
    private val _submitResult = MutableLiveData<SubmitResult>()
    val submitResult: LiveData<SubmitResult> = _submitResult

    private var _electionId: String = ""
    private var _districtId: String = ""


    // Retrieve ballot
    fun getBallot(electionId: String) {
        _electionId = electionId

        viewModelScope.launch {
            try {
                val response = ElectionsApi.client.getBallot(electionId)

                when (response) {
                    is NetworkResponse.Success -> {
                        _districtId = response.body!!.districtId
                        _candidateData.value = response.body!!.candidateList
                    }
                    is NetworkResponse.Failure -> {

                    }
                    is NetworkResponse.NetworkError -> {
                        Toast.makeText(MainApplication.appContext,
                            "Network Error - Unable to retrieve ballot",
                            Toast.LENGTH_SHORT).show();

                    }
                    is NetworkResponse.UnknownError -> {

                    }
                }
                Log.d(TAG, response.toString())
            } catch (e: Exception) {
                _candidateData.value = listOf<Candidate>()
                // throw Exception("Unable to retrieve ballot")
            }
        }
    }

    fun submitBallot() {
        viewModelScope.launch {
            try {
                val candidate = selectedCandidate.value!!

                val ballotVote = BallotVote(
                    _electionId,
                    _districtId,
                    candidate.candidateId,
                )

                val response = ElectionsApi.client.submit(ballotVote)

                when (response) {
                    is NetworkResponse.Success -> {
                        _submitResult.value = SubmitResult(true)
                        // TODO (Set success)
                        Toast.makeText(MainApplication.appContext,
                            "Succcess - Submitted ballot",
                            Toast.LENGTH_SHORT).show();
                    }
                    is NetworkResponse.Failure -> {
                        _submitResult.value = SubmitResult(false)
                        Toast.makeText(MainApplication.appContext,
                            "Error - Unable to submit ballot",
                            Toast.LENGTH_SHORT).show();
                    }
                    is NetworkResponse.NetworkError -> {
                        _submitResult.value = SubmitResult(false)
                        Toast.makeText(MainApplication.appContext,
                            "Network Error - Unable to submit ballot",
                            Toast.LENGTH_SHORT).show();

                    }
                    is NetworkResponse.UnknownError -> {
                        _submitResult.value = SubmitResult(false)
                        Toast.makeText(MainApplication.appContext,
                            "Unknown Error - Unable to submit ballot",
                            Toast.LENGTH_SHORT).show();
                    }
                }
                Log.d(TAG, response.toString())
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
