package com.kevinwei.vote.network

import com.kevinwei.vote.model.BallotJson
import com.kevinwei.vote.model.Election
import com.kevinwei.vote.model.User
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import retrofit2.Call
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import retrofit2.http.GET
import retrofit2.http.POST

// TODO - Replace URL
private const val BASE_URL = "http://localhost:8080"

private val moshi = Moshi.Builder()
    .add(KotlinJsonAdapterFactory())
    .build()

private val retrofit = Retrofit.Builder()
    .addConverterFactory(MoshiConverterFactory.create(moshi))
    .baseUrl(BASE_URL)
    .build()

// TODO - Look into retrofit documention
interface ElectionsApiService {
    @POST("login")
    fun login(username:String, password:String): User

    @GET("elections")
    suspend fun getElections(): List<Election>

    @GET("ballots")
    suspend fun getBallot(electionId:String): BallotJson
}

// Lazy init Retrofit services (Computationally expensive)
object ElectionsApi {
    val retrofitService: ElectionsApiService by lazy {
        retrofit.create(ElectionsApiService::class.java )
    }
}
