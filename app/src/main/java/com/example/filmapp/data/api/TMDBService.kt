package com.example.filmapp.data.api

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object TMDBService {

    fun getTMDBService(): TMDBServicesInterface =
        Retrofit.Builder().baseUrl(TMDBServicesInterface.TMDB_BASE_URL)
            .addConverterFactory(GsonConverterFactory.create()).build()
            .create(TMDBServicesInterface::class.java)
}