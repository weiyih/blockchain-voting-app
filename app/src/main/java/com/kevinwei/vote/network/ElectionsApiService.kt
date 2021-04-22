package com.kevinwei.vote.network

import com.kevinwei.vote.BASE_URL
import com.kevinwei.vote.model.*
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import retrofit2.http.*
import java.util.concurrent.TimeUnit

interface ElectionsApiService {
    @POST("v1/login")
    suspend fun login(@Body loginRequest: LoginRequest): Result<LoginResponse>

    @POST( "v1/register")
    suspend fun register(@Body biometricRequest: BiometricRequest): Result<GenericResponse>

    @POST("v1/election")
    suspend fun getElections(): Result<ElectionResponse>

    @POST("/v1/ballot/{id}")
    suspend fun getBallot(@Path("id") electionId: String): Result<BallotResponse>

    @POST("/v1/submit/{id}")
    suspend fun submit(
        @Path("id") electionId: String,
        @Body request: BallotRequest,
    ): Result<SubmitResponse>
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
    .readTimeout(30, TimeUnit.SECONDS)
    .connectTimeout(30, TimeUnit.SECONDS)
    .build()

private val retrofit = Retrofit.Builder()
    .baseUrl(BASE_URL)
    .client(okHttpClient)
    .addCallAdapterFactory(MyCallAdapterFactory())
    .addConverterFactory(MoshiConverterFactory.create(moshi))
    .build()
