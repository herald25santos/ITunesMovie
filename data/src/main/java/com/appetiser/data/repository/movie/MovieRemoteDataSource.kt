package com.appetiser.data.repository.movie

import com.appetiser.data.api.MovieApi
import com.appetiser.data.entities.toDomain
import com.appetiser.domain.entities.MovieEntity
import com.appetiser.domain.util.Result

class MovieRemoteDataSource(
    private val movieApi: MovieApi
) : MovieDataSource.Remote {

    override suspend fun getPaginateMovies(page: Int, limit: Int): Result<List<MovieEntity>> = try {
        // Since itunes api doesn't have page param. Limit the page into 1
        if (page == 2) {
            Result.Success(mutableListOf())
        } else {
            // Constant parameters
            val term = "star"
            val country = "au"
            val media = "movie"
            val result = movieApi.getMovies(term, country, media, limit = limit)
            Result.Success(result.results.map { it.toDomain() })
        }

    } catch (e: Exception) {
        Result.Error(e)
    }

    override suspend fun getMovies(movieIds: List<Int>): Result<List<MovieEntity>> = try {
        val result = movieApi.getMovies(movieIds)
        Result.Success(result.map { it.toDomain() })
    } catch (e: Exception) {
        Result.Error(e)
    }

    override suspend fun getMovie(movieId: Int): Result<MovieEntity> = try {
        val result = movieApi.getMovie(movieId)
        Result.Success(result.toDomain())
    } catch (e: Exception) {
        Result.Error(e)
    }

    override suspend fun search(query: String, page: Int, limit: Int): Result<List<MovieEntity>> =
        try {
            // Since itunes api doesn't have page param. Limit the page into 1
            if (page == 2) {
                Result.Success(mutableListOf())
            } else {
                // Constant parameters
                val term = "star"
                val country = "au"
                val media = "movie"
                val result = movieApi.getMovies(term, country, media, limit = limit)
                Result.Success(result.results.map { it.toDomain() })
            }
        } catch (e: Exception) {
            Result.Error(e)
        }
}
