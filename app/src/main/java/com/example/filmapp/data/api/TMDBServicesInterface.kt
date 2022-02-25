package com.example.filmapp.data.api

import com.example.filmapp.BuildConfig
import com.example.filmapp.data.model.PopularMoviesResponse
import com.example.filmapp.data.model.TopRatedMoviesResponse
import com.example.filmapp.data.model.UpcomingMoviesResponse
import kotlinx.coroutines.flow.Flow
import retrofit2.http.GET
import retrofit2.http.Query

interface TMDBServicesInterface {
    companion object {
        const val TMDB_BASE_URL = "https://api.themoviedb.org/3/"

        const val TMDB_POPULAR_MOVIES_PATH = "movie/popular"
        const val TMDB_TOP_RATED_MOVIES_PATH = "movie/top_rated"
        const val TMDB_UPCOMING_MOVIES_PATH = "movie/upcoming"

        const val TMDB_API_KEY_QUERY = "api_key"
    }

    @GET(TMDB_POPULAR_MOVIES_PATH)
    suspend fun getPopularMovies(@Query(TMDB_API_KEY_QUERY) apiKey: String = BuildConfig.API_KEY): Flow<PopularMoviesResponse>

    @GET(TMDB_TOP_RATED_MOVIES_PATH)
    suspend fun getTopRatedMovies(@Query(TMDB_API_KEY_QUERY) apiKey: String = BuildConfig.API_KEY): Flow<TopRatedMoviesResponse>

    @GET(TMDB_UPCOMING_MOVIES_PATH)
    suspend fun getUpcomingMovies(@Query(TMDB_API_KEY_QUERY) apiKey: String = BuildConfig.API_KEY): Flow<UpcomingMoviesResponse>
}