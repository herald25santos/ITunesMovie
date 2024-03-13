package com.appetiser.data.entities

import com.appetiser.domain.entities.MovieEntity
import com.google.gson.annotations.SerializedName

data class MovieData(
    @SerializedName("trackId")
    val trackId: Int,
    @SerializedName("longDescription")
    val description: String,
    @SerializedName("artworkUrl100")
    val image: String,
    @SerializedName("trackName")
    val title: String,
    @SerializedName("primaryGenreName")
    val category: String,
)

fun MovieData.toDomain() = MovieEntity(
    trackId = trackId,
    image = image,
    description = description,
    title = title,
    category = category,
    isFavorite = false
)