package com.example.filmapp.data.api.factory

import com.example.filmapp.data.repository.Resource
import okhttp3.Request
import retrofit2.Call
import retrofit2.CallAdapter
import retrofit2.Callback
import retrofit2.Response
import java.lang.UnsupportedOperationException
import java.lang.reflect.Type

internal class ResourceCallAdapter<S: Any>(private val responseType: Type) : CallAdapter<S, Call<Resource<S>>> {

    override fun responseType(): Type = responseType

    override fun adapt(call: Call<S>): Call<Resource<S>> = CallResource(call)
}

internal class CallResource<S: Any>(private val delegate: Call<S>) : Call<Resource<S>> {
    override fun clone(): Call<Resource<S>> = this.clone()

    override fun execute(): Response<Resource<S>> = throw UnsupportedOperationException("CallResource doesn't support execute")

    override fun enqueue(callback: Callback<Resource<S>>) = delegate.enqueue(object : Callback<S> {
        override fun onResponse(call: Call<S>, response: Response<S>) {
            val responseBody = response.body()
            if (response.isSuccessful && responseBody != null) {
                callback.onResponse(this@CallResource, Response.success(Resource.Success(data = responseBody)))
            } else {
                callback.onResponse(this@CallResource, Response.success(Resource.Error(errorMessage = response.message())))
            }
        }

        override fun onFailure(call: Call<S>, t: Throwable) {
            callback.onResponse(this@CallResource, Response.success(Resource.Error(errorMessage = t.message)))
        }

    })

    override fun isExecuted(): Boolean = delegate.isExecuted

    override fun cancel() = delegate.cancel()

    override fun isCanceled(): Boolean = delegate.isCanceled

    override fun request(): Request = delegate.request()
}