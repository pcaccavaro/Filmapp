package com.example.filmapp.data.model

import com.google.gson.annotations.SerializedName

data class UpcomingMoviesResponse(
    @SerializedName("page")
    val page: Int?,
    @SerializedName("results")
    val results: List<Movie>?
)
