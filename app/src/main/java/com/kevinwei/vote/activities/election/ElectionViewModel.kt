package com.kevinwei.vote.activities.election

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kevinwei.vote.network.ElectionsApi
import kotlinx.coroutines.launch

import java.lang.Exception

class ElectionViewModel:ViewModel() {
    private val TAG = "ElectionViewModel"
    private val _response = MutableLiveData<String>()
//    private val _response = MutableLiveData<Election>()

    //    val response: LiveData<Election>
    val response: LiveData<String>
        get() = _response

    init {
        getElections()
    }


    private fun getElections() {
        viewModelScope.launch {
            try{
                val electionList = ElectionsApi.retrofitService.getElections()
//                    TODO("Not yet implemented")
//                    TODO("Decrypt data")
//                    TODO("Store in ROOM?")
                Log.d(TAG, electionList.size.toString())
                _response.value = "${electionList.size} retrieved"

            }catch (e:Exception) {
//                    TODO("Not yet implemented")
                _response.value = "Failed: " + e.message
            }
        }
    }
}
