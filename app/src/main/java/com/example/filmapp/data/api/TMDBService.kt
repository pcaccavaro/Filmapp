package com.example.filmapp.data.api

import com.example.filmapp.data.api.factory.CallAdapterFactory
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object TMDBService {

    fun getTMDBService(): TMDBServicesInterface =
        Retrofit.Builder().baseUrl(TMDBServicesInterface.TMDB_BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .addCallAdapterFactory(CallAdapterFactory()).build()
            .create(TMDBServicesInterface::class.java)
}