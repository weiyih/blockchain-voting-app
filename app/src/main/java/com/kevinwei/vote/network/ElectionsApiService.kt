package com.kevinwei.vote.network

import com.kevinwei.vote.BASE_URL
import com.kevinwei.vote.model.*
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import retrofit2.http.*

interface ElectionsApiService {
    @POST("v1/login")
    suspend fun login(@Body loginRequest: LoginRequest): NetworkResponse<User, ErrorResponse>

    @POST( "v1/register")
    suspend fun register(@Body biometricRequest: BiometricRequest): NetworkResponse<Boolean, ErrorResponse>

    @POST("v1/election")
    suspend fun getElections(): NetworkResponse<List<Election>, ErrorResponse>

    @POST("/v1/ballot/{id}")
    suspend fun getBallot(@Path("id") electionId: String): NetworkResponse<Ballot, ErrorResponse>

    @POST("/v1/submit/{id}")
    suspend fun submit(
        @Path("id") electionId: String,
        @Body vote: BallotVote,
    ): NetworkResponse<Object, ErrorResponse>
}

// Lazy init Retrofit services (Reduce computationally expensive process)
object ElectionsApi {
    val client: ElectionsApiService by lazy {
        retrofit.create(ElectionsApiService::class.java)
    }
}

private val moshi = Moshi.Builder()
    .add(KotlinJsonAdapterFactory())
    .build()

private val okHttpClient = OkHttpClient.Builder()
    .addInterceptor(AuthInterceptor())
    .build()

private val retrofit = Retrofit.Builder()
    .baseUrl(BASE_URL)
    .client(okHttpClient)
    .addCallAdapterFactory(NetworkResponseAdapterFactory())
    .addConverterFactory(MoshiConverterFactory.create(moshi))
    .build()
