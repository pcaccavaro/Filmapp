package com.example.filmapp.data.api.factory

import com.example.filmapp.data.repository.Resource
import java.lang.reflect.ParameterizedType
import java.lang.reflect.Type
import retrofit2.Call
import retrofit2.CallAdapter
import retrofit2.Retrofit

class CallAdapterFactory : CallAdapter.Factory() {

    override fun get(returnType: Type, annotations: Array<out Annotation>, retrofit: Retrofit): CallAdapter<*, *>? {
        if (Call::class.java != getRawType(returnType)) {
            return null
        }

        val responseType = getParameterUpperBound(0, returnType as ParameterizedType)
        val rawDeferredType = getRawType(responseType)

        if (rawDeferredType != Resource::class.java) {
            return null
        }

        return ResourceCallAdapter<Any>(getParameterUpperBound(0, responseType as ParameterizedType))
    }
}