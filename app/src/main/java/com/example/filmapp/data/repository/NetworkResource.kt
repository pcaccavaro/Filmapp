package com.example.filmapp.data.repository

sealed class Resource {
    data class ResourceSuccess<T>(val data: T) : Resource()
    object ResourceLoading : Resource()
    data class ResourceError(val errorMessage: String?) : Resource()
}