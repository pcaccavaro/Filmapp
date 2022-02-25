package com.example.filmapp.data.repository

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

sealed class Resource<T> {
    data class Success<T>(val data: T) : Resource<T>()
    data class Loading<T>(val data: T? = null) : Resource<T>()
    data class Error<T>(val errorMessage: String?) : Resource<T>()
}

inline fun <T> networkResource(crossinline createCall: suspend () -> Resource<T>): Flow<Resource<T>> =
    flow {
        emit(Resource.Loading())

        val resource = createCall()

        if (resource is Resource.Error || resource is Resource.Success) {
            emit(resource)
        }
    }
