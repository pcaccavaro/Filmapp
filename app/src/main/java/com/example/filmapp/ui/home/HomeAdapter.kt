package com.example.filmapp.ui.home

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.example.filmapp.R
import com.example.filmapp.data.model.Movie
import com.example.filmapp.databinding.MovieListItemBinding
import com.example.filmapp.databinding.MovieListItemLoadingBinding

class HomeAdapter : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private companion object {
        const val LOADING_STATUS = 1
        const val CONTENT_STATUS = 2
    }

    private var movieList = listOf<Movie>()

    override fun onCreateViewHolder(viewGroup: ViewGroup, viewType: Int): RecyclerView.ViewHolder =
        if (viewType == LOADING_STATUS) {
            HomeProgressViewHolder(DataBindingUtil.inflate(
                LayoutInflater.from(viewGroup.context),
                R.layout.movie_list_item_loading,
                viewGroup,
                false
            ))
        } else {
            HomeMovieViewHolder(DataBindingUtil.inflate(
                LayoutInflater.from(viewGroup.context),
                R.layout.movie_list_item,
                viewGroup,
                false
            ))
        }

    override fun onBindViewHolder(viewHolder: RecyclerView.ViewHolder, position: Int) {
        if (viewHolder is HomeMovieViewHolder) {
            viewHolder.bind(movieList[position])
        }
    }

    override fun getItemCount(): Int = if(movieList.isEmpty()) 1 else movieList.size

    override fun getItemViewType(position: Int): Int = if (movieList.isEmpty()) LOADING_STATUS else CONTENT_STATUS

    fun setMovieList(movieList: List<Movie>) {
        this.movieList = movieList
        notifyItemRangeChanged(0, movieList.size)
    }

    private class HomeProgressViewHolder(binding: MovieListItemLoadingBinding) : RecyclerView.ViewHolder(binding.root)

    private class HomeMovieViewHolder(private val binding: MovieListItemBinding) : RecyclerView.ViewHolder(binding.root) {

        fun bind(movie: Movie) {
            binding.movie = movie
        }
    }
}