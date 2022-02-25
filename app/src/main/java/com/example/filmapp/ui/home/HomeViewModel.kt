package com.example.filmapp.ui.home

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.filmapp.data.repository.Resource
import com.example.filmapp.data.repository.TMDBRepository
import com.example.filmapp.ui.common.BaseViewModel
import com.example.filmapp.util.logDTag
import com.example.filmapp.util.logETag
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

class HomeViewModel(app: Application, private val tmdbRepository: TMDBRepository) : BaseViewModel<HomeEvent, HomeAction>(app) {

    init {
        requestPopularMovies()
        requestTopRatedMovies()
        requestUpcomingMovies()
    }

    override fun processEvent(event: HomeEvent) {
        when(event) {
            is HomeEvent.OnPopularMovieItemClicked -> TODO()
            is HomeEvent.OnTopRatedMovieItemClicked -> TODO()
            is HomeEvent.OnUpcomingMovieItemClicked -> TODO()
        }
    }

    private fun requestPopularMovies() {
        viewModelScope.launch {
            tmdbRepository.getPopularMovies().collect { popularMoviesResource ->
                when(popularMoviesResource) {
                    is Resource.Error -> this@HomeViewModel.logETag("getPopularMovies: Fail -> ${popularMoviesResource.errorMessage}")
                    is Resource.Loading -> this@HomeViewModel.logDTag("getPopularMovies: Loading")
                    is Resource.Success -> this@HomeViewModel.logDTag("getPopularMovies: Success")
                }
            }
        }
    }

    private fun requestTopRatedMovies() {
        viewModelScope.launch {
            tmdbRepository.getTopRatedMovies().collect { topRatedMoviesResource ->
                when(topRatedMoviesResource) {
                    is Resource.Error -> this@HomeViewModel.logETag("getTopRatedMovies: Fail -> ${topRatedMoviesResource.errorMessage}")
                    is Resource.Loading -> this@HomeViewModel.logDTag("getTopRatedMovies: Loading")
                    is Resource.Success -> this@HomeViewModel.logDTag("getTopRatedMovies: Success")
                }
            }
        }
    }

    private fun requestUpcomingMovies() {
        viewModelScope.launch {
            tmdbRepository.getUpcomingMovies().collect { upcomingMoviesResource ->
                when(upcomingMoviesResource) {
                    is Resource.Error -> this@HomeViewModel.logETag("getUpcomingMovies: Fail -> ${upcomingMoviesResource.errorMessage}")
                    is Resource.Loading -> this@HomeViewModel.logDTag("getUpcomingMovies: Loading")
                    is Resource.Success -> this@HomeViewModel.logDTag("getUpcomingMovies: Success")
                }
            }
        }
    }
}

class HomeViewModelFactory(private val application: Application, private val tmdbRepository: TMDBRepository) : ViewModelProvider.NewInstanceFactory() {
    override fun <T : ViewModel> create(modelClass: Class<T>): T = modelClass.cast(HomeViewModel(application, tmdbRepository))!!
}