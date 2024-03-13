package com.appetiser.itunesmovie.entities

sealed class MovieListItem {
    data class Movie(
        val id: Int,
        val imageUrl: String,
        val title: String,
        val category: String,
        var isFavorite: Boolean = false
    ) : MovieListItem()

    data class Separator(val category: String) : MovieListItem()
}
