package com.kevinwei.vote.activities.ballot

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
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

    fun getBallot(electionId: String) {
        viewModelScope.launch {
            try {
                val response = ElectionsApi.client.getBallot(electionId)

                when (response) {
                    is NetworkResponse.Success -> {
                        _candidateData.value = response.body!!.candidateList
                    }
                    is NetworkResponse.Failure -> {

                    }
                    is NetworkResponse.NetworkError -> {

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


    /**
     * Navigation to a vote confirmation fragment
     */

    // sets state variable when candidate clicked
    fun onCandidateSelected(position:Int, candidate: Candidate) {


        for (i in _candidateData.value!!.indices) {
            if (i == position) {
                _selectedCandidate.value = if (_selectedCandidate.value == candidate) null else candidate
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
