package com.appetiser.itunesmovie.mapper

import com.appetiser.domain.entities.MovieEntity
import com.appetiser.itunesmovie.entities.MovieListItem

fun MovieEntity.toPresentation() = MovieListItem.Movie(
    id = trackId,
    imageUrl = image,
    title = title,
    category = category,
    isFavorite = isFavorite
)