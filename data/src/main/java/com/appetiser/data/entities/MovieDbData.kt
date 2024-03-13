package com.appetiser.data.entities

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.appetiser.domain.entities.MovieEntity

@Entity(tableName = "movies")
data class MovieDbData(
    @PrimaryKey
    val id: Int,
    val description: String,
    val image: String,
    val title: String,
    val category: String,
    val isFavorite: Boolean
)

fun MovieDbData.toDomain() = MovieEntity(
    trackId = id,
    image = image,
    description = description,
    title = title,
    category = category,
    isFavorite = isFavorite
)