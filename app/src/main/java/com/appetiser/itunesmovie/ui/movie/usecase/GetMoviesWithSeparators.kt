package com.appetiser.itunesmovie.ui.movie.usecase

import androidx.paging.PagingData
import androidx.paging.map
import com.appetiser.domain.repository.MovieRepository
import com.appetiser.itunesmovie.entities.MovieListItem
import com.appetiser.itunesmovie.mapper.toPresentation
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class GetMoviesWithSeparators @Inject constructor(
    private val movieRepository: MovieRepository,
    private val insertSeparatorIntoPagingData: InsertSeparatorIntoPagingData
) {

    fun movies(pageSize: Int): Flow<PagingData<MovieListItem>> =
        movieRepository.paginateMovies(pageSize).map {
            val pagingData: PagingData<MovieListItem.Movie> =
                it.map { movie -> movie.toPresentation() }
            insertSeparatorIntoPagingData.insert(pagingData)
        }
}
