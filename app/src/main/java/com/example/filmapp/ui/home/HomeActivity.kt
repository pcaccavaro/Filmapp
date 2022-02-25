package com.example.filmapp.ui.home

import android.os.Bundle
import androidx.activity.viewModels
import androidx.databinding.DataBindingUtil
import com.example.filmapp.R
import com.example.filmapp.data.repository.TMDBRepository
import com.example.filmapp.databinding.ActivityHomeBinding
import com.example.filmapp.ui.common.BaseActivity

class HomeActivity : BaseActivity<HomeEvent, HomeAction>() {

    private lateinit var binding: ActivityHomeBinding

    private lateinit var popularMoviesAdapter: HomeAdapter
    private lateinit var topRatedMoviesAdapter: HomeAdapter
    private lateinit var upcomingMoviesAdapter: HomeAdapter

    override val viewModel by viewModels<HomeViewModel> {
        HomeViewModelFactory(
            application,
            TMDBRepository()
        )
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_home)
        binding.lifecycleOwner = this

        setUpPopularMoviesAdapter()
        setUpTopRatedMoviesAdapter()
        setUpUpcomingMoviesAdapter()
    }

    private fun setUpPopularMoviesAdapter() {
        popularMoviesAdapter = HomeAdapter()
        binding.activityHomePopularMovies.adapter = popularMoviesAdapter
    }

    private fun setUpTopRatedMoviesAdapter() {
        topRatedMoviesAdapter = HomeAdapter()
        binding.activityHomeTopRatedMovies.adapter = topRatedMoviesAdapter
    }

    private fun setUpUpcomingMoviesAdapter() {
        upcomingMoviesAdapter = HomeAdapter()
        binding.activityHomeUpcomingMovies.adapter = upcomingMoviesAdapter
    }

    override fun processAction(action: HomeAction) {
        when(action) {
            is HomeAction.ShowPopularMovieList -> popularMoviesAdapter.setMovieList(action.popularMovieList)
            is HomeAction.ShowTopRatedMovieList -> topRatedMoviesAdapter.setMovieList(action.topRatedMovieList)
            is HomeAction.ShowUpcomingMovieList -> upcomingMoviesAdapter.setMovieList(action.upcomingMovieList)
        }
    }
}