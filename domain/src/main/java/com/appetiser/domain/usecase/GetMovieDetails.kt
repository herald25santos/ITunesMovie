package com.appetiser.domain.usecase

import com.appetiser.domain.entities.MovieEntity
import com.appetiser.domain.repository.MovieRepository
import com.appetiser.domain.util.Result

class GetMovieDetails(
    private val movieRepository: MovieRepository
) {
    suspend operator fun invoke(movieId: Int): Result<MovieEntity> =
        movieRepository.getMovie(movieId)
}
