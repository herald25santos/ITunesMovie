package com.appetiser.domain.repository

import androidx.paging.PagingData
import com.appetiser.domain.entities.MovieEntity
import com.appetiser.domain.util.Result
import kotlinx.coroutines.flow.Flow

interface MovieRepository {
    fun paginateMovies(pageSize: Int): Flow<PagingData<MovieEntity>>
    fun favoriteMovies(pageSize: Int): Flow<PagingData<MovieEntity>>
    fun search(query: String, pageSize: Int): Flow<PagingData<MovieEntity>>
    suspend fun getMovie(movieId: Int): Result<MovieEntity>
    suspend fun checkFavoriteStatus(movieId: Int): Result<Boolean>
    suspend fun addMovieToFavorite(movieId: Int)
    suspend fun removeMovieFromFavorite(movieId: Int)
    suspend fun sync(): Boolean
}