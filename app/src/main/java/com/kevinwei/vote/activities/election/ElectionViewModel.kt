package com.kevinwei.vote.activities.election

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kevinwei.vote.model.Election
import com.kevinwei.vote.network.ElectionsApi
import kotlinx.coroutines.launch

import java.lang.Exception

class ElectionViewModel : ViewModel() {
    private val TAG = "ElectionViewModel"

//    private val _status = MutableLiveData<ElectionApiStatus>
//    val status: LiveData<ElectionApiStatus>

    private val _data = MutableLiveData<List<Election>>()

    val electionData: LiveData<List<Election>>
        get() = _data

    init {
        getElections()
    }

    fun getElections() {
        viewModelScope.launch {
            try {
                val electionList = ElectionsApi.retrofitService.getElections()
//                    TODO("Not yet implemented")
//                    TODO("Decrypt data")
                Log.d(TAG, electionList.size.toString())
                _data.value = electionList

            } catch (e: Exception) {
//                    TODO("Not yet implemented")
//                _data = empty
                _data.value = ArrayList()
//                throw Exception(_data.value)
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

