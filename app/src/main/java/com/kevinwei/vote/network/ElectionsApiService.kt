package com.kevinwei.vote.network

import com.kevinwei.vote.model.*
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Headers
import retrofit2.http.POST

//private const val BASE_URL = "http://10.0.2.2/"
private const val BASE_URL = "http://10.0.2.2/"

private val moshi = Moshi.Builder()
    .add(KotlinJsonAdapterFactory())
    .build()

private val retrofit = Retrofit.Builder()
    .baseUrl(BASE_URL)
    .addCallAdapterFactory(NetworkResponseAdapterFactory())
    .addConverterFactory(MoshiConverterFactory.create(moshi))
    .build()

interface ElectionsApiService {
    @Headers(
        "User-Agent: eVoting-android-app"
    )

    @POST("v1/login")
//    suspend fun login(@Body loginRequest: LoginRequest): User
    suspend fun login(@Body loginRequest: LoginRequest): NetworkResponse<User, ErrorResponse>

    @POST("v1/election")
//    suspend fun getElections(): List<Election>
    suspend fun getElections(): NetworkResponse<List<Election>, ErrorResponse>

    @POST("/v1/ballot")
//    suspend fun getBallot(@Body electionId:String): Ballot
    suspend fun getBallot(@Body electionId:String): NetworkResponse<Ballot, ErrorResponse>

    @POST("/v1/register/biometric")
    suspend fun registerBiometricLogin(@Body uniqueID: BiometricToken): NetworkResponse<Object, ErrorResponse>
}

// Lazy init Retrofit services (Computationally expensive)
object ElectionsApi {
    val client: ElectionsApiService by lazy {
        retrofit.create(ElectionsApiService::class.java )
    }
}
