package com.appetiser.itunesmovie.ui.adapter.movie

import androidx.recyclerview.widget.DiffUtil
import com.appetiser.itunesmovie.entities.MovieListItem

object MovieDiffCallback : DiffUtil.ItemCallback<MovieListItem>() {

    override fun areItemsTheSame(oldItem: MovieListItem, newItem: MovieListItem): Boolean =
        if (oldItem is MovieListItem.Movie && newItem is MovieListItem.Movie) {
            oldItem.id == newItem.id && oldItem.isFavorite && newItem.isFavorite
        } else if (oldItem is MovieListItem.Separator && newItem is MovieListItem.Separator) {
            oldItem.category == newItem.category
        } else {
            oldItem == newItem
        }

    override fun areContentsTheSame(oldItem: MovieListItem, newItem: MovieListItem): Boolean = oldItem == newItem
}