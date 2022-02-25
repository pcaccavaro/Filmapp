package com.example.filmapp.ui.home

import com.example.filmapp.data.model.Movie

sealed class HomeAction {
    data class ShowPopularMovieList(val popularMovieList: List<Movie>) : HomeAction()
    data class ShowTopRatedMovieList(val topRatedMovieList: List<Movie>) : HomeAction()
    data class ShowUpcomingMovieList(val upcomingMovieList: List<Movie>) : HomeAction()
}

sealed class HomeEvent {
    data class OnPopularMovieItemClicked(val movie: Movie) : HomeEvent()
    data class OnTopRatedMovieItemClicked(val movie: Movie) : HomeEvent()
    data class OnUpcomingMovieItemClicked(val movie: Movie) : HomeEvent()
}