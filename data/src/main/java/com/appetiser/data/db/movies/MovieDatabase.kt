package com.appetiser.data.db.movies

import androidx.room.Database
import androidx.room.RoomDatabase
import com.appetiser.data.db.favoritemovies.FavoriteMovieDao
import com.appetiser.data.entities.MovieRemoteKeyDbData
import com.appetiser.data.entities.FavoriteMovieDbData
import com.appetiser.data.entities.MovieDbData

@Database(
    entities = [MovieDbData::class, FavoriteMovieDbData::class, MovieRemoteKeyDbData::class],
    version = 1,
    exportSchema = false
)
abstract class MovieDatabase : RoomDatabase() {
    abstract fun movieDao(): MovieDao
    abstract fun movieRemoteKeysDao(): MovieRemoteKeyDao
    abstract fun favoriteMovieDao(): FavoriteMovieDao
}