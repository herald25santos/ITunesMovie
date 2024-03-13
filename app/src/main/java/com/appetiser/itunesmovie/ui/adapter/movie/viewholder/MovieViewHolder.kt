package com.appetiser.itunesmovie.ui.adapter.movie.viewholder

import android.graphics.drawable.Drawable
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.ImageView
import androidx.appcompat.widget.AppCompatImageView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.appetiser.itunesmovie.R
import com.appetiser.itunesmovie.databinding.ItemMovieBinding
import com.appetiser.itunesmovie.entities.MovieListItem
import com.bumptech.glide.Glide
import com.bumptech.glide.RequestBuilder
import com.bumptech.glide.load.DecodeFormat
import com.bumptech.glide.load.engine.DiskCacheStrategy

class MovieViewHolder(
    parent: ViewGroup,
    private val onMovieClick: (movieId: Int) -> Unit,
    private val onMovieFavoriteClick: (movieId: Int) -> Unit,
    private val imageFixedSize: Int
) : RecyclerView.ViewHolder(
    ItemMovieBinding.inflate(LayoutInflater.from(parent.context), parent, false).root
) {

    private val binding = ItemMovieBinding.bind(itemView)

    fun bind(movie: MovieListItem.Movie) = with(binding) {
        loadImage(image, movie.imageUrl)
        ivFavorite.setBackgroundResource(if (movie.isFavorite) R.drawable.ic_favorite_active else R.drawable.ic_favorite_border)
        tvTitle.text = movie.title
        ivFavorite.setOnClickListener {
            onMovieFavoriteClick(movie.id)
        }
        root.setOnClickListener { onMovieClick(movie.id) }
    }

    fun unBind() = with(binding) {
        clearImage(image)
        root.setOnClickListener(null)
    }

    private fun loadImage(image: AppCompatImageView, url: String) = Glide.with(image)
        .asDrawable()
        .override(imageFixedSize)
        .format(DecodeFormat.PREFER_RGB_565)
        .diskCacheStrategy(DiskCacheStrategy.RESOURCE)
        .thumbnail(getThumbnailRequest(image, url))
        .load(url)
        .placeholder(R.drawable.bg_no_image)
        .error(R.drawable.bg_image)
        .into(image)

    private fun getThumbnailRequest(imageView: ImageView, url: String): RequestBuilder<Drawable> =
        Glide.with(imageView)
            .asDrawable()
            .override(imageFixedSize)
            .format(DecodeFormat.PREFER_RGB_565)
            .diskCacheStrategy(DiskCacheStrategy.RESOURCE)
            .sizeMultiplier(0.2F)
            .load(url)

    private fun clearImage(image: AppCompatImageView) = Glide.with(image).clear(image)
}