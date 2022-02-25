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

    override val viewModel by viewModels<HomeViewModel> {
        HomeViewModelFactory(
            application,
            TMDBRepository()
        )
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_home)
    }

    override fun processAction(action: HomeAction) {
        when(action) {
            is HomeAction.ShowPopularMovieList -> TODO()
            is HomeAction.ShowTopRatedMovieList -> TODO()
            is HomeAction.ShowUpcomingMovieList -> TODO()
        }
    }
}