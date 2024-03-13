package com.appetiser.domain.usecase

import com.appetiser.domain.repository.MovieRepository
import com.appetiser.domain.util.Result

class CheckFavoriteStatus(
    private val movieRepository: MovieRepository
) {
    suspend operator fun invoke(movieId: Int): Result<Boolean> =
        movieRepository.checkFavoriteStatus(movieId)
}