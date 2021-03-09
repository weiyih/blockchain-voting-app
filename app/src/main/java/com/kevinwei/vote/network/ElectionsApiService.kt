package com.kevinwei.vote.network

import retrofit2.Call
import retrofit2.Retrofit
import retrofit2.converter.scalars.ScalarsConverterFactory
import retrofit2.http.GET

// TODO - Replace URL
private const val BASE_URL = "http://localhost:8080"

private val retrofit = Retrofit.Builder()
    .addConverterFactory(ScalarsConverterFactory.create())
    .baseUrl(BASE_URL)
    .build()

interface ElectionsApiService {
    @GET("elections")
    fun getElections():
            Call<String>

    @GET("ballots")
    fun getBallots():
            Call<String>
}

object ElectionsApi