package com.kevinwei.vote.network

import retrofit2.Call
import retrofit2.Retrofit
import retrofit2.converter.scalars.ScalarsConverterFactory
import retrofit2.http.GET
import retrofit2.http.POST

// TODO - Replace URL
private const val BASE_URL = "http://localhost:8080"

private val retrofit = Retrofit.Builder()
    .addConverterFactory(ScalarsConverterFactory.create())
    .baseUrl(BASE_URL)
    .build()

// TODO - Look into retrofit documention
interface ElectionsApiService {
    @POST("login")
    fun login(username:String, password:String):
            Call<String>

    @GET("elections")
    fun getElections():
            Call<String>

    @GET("ballots")
    fun getBallots():
            Call<String>
}

// Lazy init Retrofit services (Computationally expensive)
object ElectionsApi {
    val retrofitService: ElectionsApiService by lazy {
        retrofit.create(ElectionsApiService::class.java )
    }
}
