package com.appetiser.domain.entities

data class MovieEntity(
    val trackId: Int,
    val title: String,
    val description: String,
    val image: String,
    val category: String,
    var isFavorite: Boolean
)