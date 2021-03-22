package com.kevinwei.vote.activities.ballot

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kevinwei.vote.model.Candidate
import com.kevinwei.vote.network.ElectionsApi
import kotlinx.coroutines.launch

class BallotViewModel: ViewModel() {
    private val TAG ="BallotViewModel"

    private val _data = MutableLiveData<List<Candidate>>()

    val candidateData: LiveData<List<Candidate>>
        get() = _data

    fun getBallot(electionId: String) {
        viewModelScope.launch {
            try {
                val ballot = ElectionsApi.retrofitService.getBallot(electionId)
                Log.d(TAG, ballot.toString())
                _data.value = ballot.candidateList
            } catch (e: Exception) {
                TODO("Not yet implemented")
                _data.value = listOf<Candidate>()
                // throw Exception("Unable to retrieve ballot")
            }
        }
    }
}
