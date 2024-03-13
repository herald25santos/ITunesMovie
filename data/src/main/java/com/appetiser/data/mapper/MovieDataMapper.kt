package com.appetiser.data.mapper

import com.appetiser.data.entities.MovieDbData
import com.appetiser.domain.entities.MovieEntity

fun MovieEntity.toDbData() = MovieDbData(
    id = trackId,
    image = image,
    description = description,
    title = title,
    category = category,
    isFavorite = isFavorite
)
