package com.kevinwei.vote.network

import okhttp3.*
import okio.Timeout
import retrofit2.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.IOException
import java.lang.reflect.ParameterizedType
import java.lang.reflect.Type

/*
* Network Results replaces the resulting API responses and parses them accordingly
* https://stackoverflow.com/questions/56483235/how-to-create-a-call-adapter-for-suspending-functions-in-retrofit
* https://github.com/icesmith/android-samples/tree/master/RetrofitSuspendingCallAdapter
* https://github.com/mlegy/retrofit2-kotlin-coroutines-call-adapter
*/

// Result class can be either Success, NetworkError, or GenericError (non 200)

sealed class NetworkResponse<out T : Any, out U : Any> {
    data class Success<T : Any>(val body: T) : NetworkResponse<T, Nothing>()
    data class Failure<U : Any>(val body: U, val code: Int) : NetworkResponse<Nothing, U>()
    data class NetworkError(val error: IOException) : NetworkResponse<Nothing, Nothing>()
    data class UnknownError(val error: Throwable?) : NetworkResponse<Nothing, Nothing>()
}

//abstract class CallDelegate<TIn, TOut>(protected val proxy: Call<TIn>) : Call<TOut> {
//    override fun execute(): Response<TOut> = throw NotImplementedError()
//    final override fun enqueue(callback: Callback<TOut>) = enqueueImpl(callback)
//    final override fun clone(): Call<TOut> = cloneImpl()
//
//    override fun cancel() = proxy.cancel()
//    override fun request(): Request = proxy.request()
//    override fun isExecuted() = proxy.isExecuted
//    override fun isCanceled() = proxy.isCanceled
//
//    abstract fun enqueueImpl(callback: Callback<TOut>)
//    abstract fun cloneImpl(): Call<TOut>
//}

