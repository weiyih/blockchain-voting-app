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

class ElectionViewModel:ViewModel() {
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
            try{
                val electionList = ElectionsApi.retrofitService.getElections()
//                    TODO("Not yet implemented")
//                    TODO("Decrypt data")
//                    TODO("Store in ROOM?")
                Log.d(TAG, electionList.size.toString())
                _data.value = electionList

            }catch (e:Exception) {
//                    TODO("Not yet implemented")
//                _data = empty
                _data.value = ArrayList()
//                throw Exception(_data.value)
            }
        }
    }


    // Navigation
    private val _navigateToBallot = MutableLiveData<String?>()
    val navigateToBallot: LiveData<String?>
        get() = _navigateToBallot

    // Trigger on BallotFragment
    // TODO("call on fragment observer")
    fun doneNavigating() {
        _navigateToBallot.value = null
    }
}
