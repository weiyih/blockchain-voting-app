package com.kevinwei.vote.activities.ballot

import android.util.Log
import androidx.databinding.Bindable
import androidx.databinding.Observable
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kevinwei.vote.model.Candidate
import com.kevinwei.vote.network.ElectionsApi
import kotlinx.coroutines.launch

class BallotViewModel : ViewModel() {
    private val TAG = "BallotViewModel"

    // Candidate list data
    private val _candidateData = MutableLiveData<List<Candidate>>()
    val candidateData: LiveData<List<Candidate>> = _candidateData

    // Selected candidate
    private val _selectedCandidate = MutableLiveData<Candidate?>()
    val selectedCandidate: LiveData<Candidate?> = _selectedCandidate

    //TODO - replace with network call
    init {
        _selectedCandidate.value = null

        val testList = listOf(
            Candidate(
                "1111",
                "Candidate Name A",
            ),
            Candidate(
                "2222",
                "Candidate B",
            ),
            Candidate(
                "3333",
                "Candidate Name 4",
            ),
            Candidate(
                "4444",
                "Candidate Five",
            )
        )
        _candidateData.value = testList
    }


    fun getBallot(electionId: String) {
        viewModelScope.launch {
            try {
                val ballot = ElectionsApi.retrofitService.getBallot(electionId)
                Log.d(TAG, ballot.toString())
                _candidateData.value = ballot.candidateList
            } catch (e: Exception) {
                TODO("Not yet implemented")
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
