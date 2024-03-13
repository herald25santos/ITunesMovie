package com.appetiser.domain.usecase

import com.appetiser.domain.repository.MovieRepository

class RemoveMovieFromFavorite(
    private val movieRepository: MovieRepository
) {
    suspend operator fun invoke(movieId: Int) = movieRepository.removeMovieFromFavorite(movieId)
}