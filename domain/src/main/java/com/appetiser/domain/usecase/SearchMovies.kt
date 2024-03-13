package com.appetiser.domain.usecase

import androidx.paging.PagingData
import com.appetiser.domain.entities.MovieEntity
import com.appetiser.domain.repository.MovieRepository
import kotlinx.coroutines.flow.Flow

class SearchMovies(
    private val movieRepository: MovieRepository
) {
    operator fun invoke(query: String, pageSize: Int): Flow<PagingData<MovieEntity>> =
        movieRepository.search(query, pageSize)
}
