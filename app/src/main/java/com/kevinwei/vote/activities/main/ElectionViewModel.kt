package com.kevinwei.vote.activities.main

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.kevinwei.vote.model.Election
import com.kevinwei.vote.network.ElectionsApi
import retrofit2.Call

import retrofit2.Callback
import retrofit2.Response

class ElectionViewModel:ViewModel() {

    private val _response = MutableLiveData<String>()
//    private val _response = MutableLiveData<Election>()

//    val response: LiveData<Election>
    val response: LiveData<String>
        get() = _response

    init {
        getElections()
    }


    private fun getElections() {
        _response.value = ElectionsApi.retrofitService.getElections().enqueue(
            object: Callback<String> {
                override fun onResponse(call: Call<String>, response: Response<String>) {
                    TODO("Not yet implemented")
                    TODO("Decrypt")
                    TODO("Store in ROOM?")
                    _response.value = response.body()

                }

                override fun onFailure(call: Call<String>, t: Throwable) {
                    TODO("Not yet implemented")
                    _response.value = "Failed: " + t. message
                }
            }
        ).toString()
    }


}