package com.example.filmapp.data.repository

import com.example.filmapp.data.api.TMDBService
import com.example.filmapp.data.api.TMDBServicesInterface
import com.example.filmapp.data.model.PopularMoviesResponse
import com.example.filmapp.data.model.TopRatedMoviesResponse
import com.example.filmapp.data.model.UpcomingMoviesResponse
import kotlinx.coroutines.flow.Flow

class TMDBRepository(private val service: TMDBServicesInterface = TMDBService.getTMDBService()) {
    fun getPopularMovies(): Flow<Resource<PopularMoviesResponse>> =
        networkResource (createCall = { service.getPopularMovies() })

    suspend fun getTopRatedMovies(): Flow<Resource<TopRatedMoviesResponse>> =
        networkResource(createCall = { service.getTopRatedMovies() })

    suspend fun getUpcomingMovies(): Flow<Resource<UpcomingMoviesResponse>> =
        networkResource(createCall = { service.getUpcomingMovies() })
}