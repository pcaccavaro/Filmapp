package com.example.filmapp.ui.common

import android.net.Uri
import android.widget.ImageView
import androidx.databinding.BindingAdapter
import com.bumptech.glide.Glide
import com.example.filmapp.data.api.TMDBServicesInterface

@BindingAdapter("loadImageFromPath")
fun loadImageFromPath(imageView: ImageView, imagePath: String) {
    Glide.with(imageView)
        .load(Uri.parse(TMDBServicesInterface.TMDB_BASE_IMAGE_URL + imagePath))
        .into(imageView)
}