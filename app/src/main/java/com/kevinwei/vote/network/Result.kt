package com.kevinwei.vote.network


import android.util.Log
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
*/

// Result class can be either Success, NetworkError, or GenericError (non 200)
sealed class Result<out T> {
    data class Success<T>(val body: T?) : Result<T>()
    data class Error(val data: String? = null) : Result<Nothing>()
    object Unauthenticated : Result<Nothing>()
    object NetworkError : Result<Nothing>()
}

abstract class CallDelegate<TIn, TOut>(protected val proxy: Call<TIn>) : Call<TOut> {
    override fun execute(): Response<TOut> = throw NotImplementedError()
    final override fun enqueue(callback: Callback<TOut>) = enqueueImpl(callback)
    final override fun clone(): Call<TOut> = cloneImpl()

    override fun cancel() = proxy.cancel()
    override fun request(): Request = proxy.request()
    override fun isExecuted() = proxy.isExecuted
    override fun isCanceled() = proxy.isCanceled

    abstract fun enqueueImpl(callback: Callback<TOut>)
    abstract fun cloneImpl(): Call<TOut>
}

class ResultCall<T>(proxy: Call<T>) : CallDelegate<T, Result<T>>(proxy) {
    override fun enqueueImpl(callback: Callback<Result<T>>) = proxy.enqueue(object : Callback<T> {
        override fun onResponse(call: Call<T>, response: Response<T>) {
            val code = response.code()
            val body = response.body()

            val result = when (code) {
                in 200..300 -> {
                    val successResult: Result<T> = Result.Success(body)
                    successResult
                }
                401 -> Result.Unauthenticated
                else -> {
                    Result.Error(code.toString())
                }
            }
            callback.onResponse(this@ResultCall, Response.success(result))
        }

        override fun onFailure(call: Call<T>, t: Throwable) {
            val result = if (t is IOException) {
                Result.NetworkError
            } else {
                Result.Error(null)
            }
        }
    })

    override fun cloneImpl() = ResultCall(proxy.clone())
    override fun timeout(): Timeout = proxy.timeout()
}

class ResultAdapter(
    private val type: Type,
) : CallAdapter<Type, Call<Result<Type>>> {
    override fun responseType() = type
    override fun adapt(call: Call<Type>): Call<Result<Type>> = ResultCall(call)
}

class MyCallAdapterFactory : CallAdapter.Factory() {
    override fun get(
        returnType: Type,
        annotations: Array<Annotation>,
        retrofit: Retrofit,
    ) = when (getRawType(returnType)) {
        Call::class.java -> {
            val callType = getParameterUpperBound(0, returnType as ParameterizedType)
            when (getRawType(callType)) {
                Result::class.java -> {
                    val resultType = getParameterUpperBound(0, callType as ParameterizedType)
                    ResultAdapter(resultType)
                }
                else -> null
            }
        }
        else -> null
    }
}
