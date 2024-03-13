package com.appetiser.data.db.movies

import androidx.paging.PagingSource
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.appetiser.data.entities.MovieDbData

@Dao
interface MovieDao {

    @Query("SELECT movies.*, favorite_movies.movieId as isFavorite FROM 'movies' LEFT JOIN 'favorite_movies' ON movies.id = favorite_movies.movieId ORDER BY category,id")
    fun movies(): PagingSource<Int, MovieDbData>

    @Query("SELECT movies.*, favorite_movies.movieId as isFavorite FROM 'movies' LEFT JOIN 'favorite_movies' ON movies.id = favorite_movies.movieId WHERE movies.title LIKE '%'||:query||'%' OR movies.category LIKE '%'||:query||'%' ORDER BY category,id")
    fun search(query: String): PagingSource<Int, MovieDbData>

    @Query("SELECT * FROM movies ORDER BY category,id")
    fun getMovies(): List<MovieDbData>

    @Query("SELECT * FROM movies WHERE id = :movieId")
    suspend fun getMovie(movieId: Int): MovieDbData?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun saveMovies(movies: List<MovieDbData>)

    @Query("DELETE FROM movies WHERE id NOT IN (SELECT movieId FROM favorite_movies)")
    suspend fun clearMoviesExceptFavorites()
}