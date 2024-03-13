package com.appetiser.data.repository.movie

import androidx.paging.ExperimentalPagingApi
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.map
import com.appetiser.data.entities.toDomain
import com.appetiser.data.exception.DataNotAvailableException
import com.appetiser.data.repository.movie.favorite.FavoriteMoviesDataSource
import com.appetiser.domain.entities.MovieEntity
import com.appetiser.domain.repository.MovieRepository
import com.appetiser.domain.util.Result
import com.appetiser.domain.util.getResult
import com.appetiser.domain.util.onSuccess
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import java.util.*

class MovieRepositoryImpl constructor(
    private val remote: MovieDataSource.Remote,
    private val local: MovieDataSource.Local,
    private val remoteMediator: MovieRemoteMediator,
    private val localFavorite: FavoriteMoviesDataSource.Local
) : MovieRepository {

    @OptIn(ExperimentalPagingApi::class)
    override fun paginateMovies(pageSize: Int): Flow<PagingData<MovieEntity>> = Pager(
        config = PagingConfig(
            pageSize = pageSize,
            enablePlaceholders = false
        ),
        remoteMediator = remoteMediator,
        pagingSourceFactory = { local.movies() }
    ).flow.map { pagingData ->
        pagingData.map {
            it.toDomain().apply {
                isFavorite = localFavorite.isFavoriteMovie(it.id)
            }
        }
    }

    override fun favoriteMovies(pageSize: Int): Flow<PagingData<MovieEntity>> = Pager(
        config = PagingConfig(
            pageSize = pageSize,
            enablePlaceholders = false
        ),
        pagingSourceFactory = { localFavorite.favoriteMovies() }
    ).flow.map { pagingData ->
        pagingData.map { it.toDomain().apply { isFavorite = true } }
    }

    @OptIn(ExperimentalPagingApi::class)
    override fun search(query: String, pageSize: Int): Flow<PagingData<MovieEntity>> = Pager(
        config = PagingConfig(
            pageSize = pageSize,
            enablePlaceholders = false
        ),
        remoteMediator = remoteMediator,
        pagingSourceFactory = { local.search(query) }
    ).flow.map { pagingData ->
        pagingData.map {
            it.toDomain().apply {
                isFavorite = localFavorite.isFavoriteMovie(it.id)
            }
        }
    }

    override suspend fun getMovie(movieId: Int): Result<MovieEntity> =
        local.getMovie(movieId).getResult({ it }, { remote.getMovie(movieId) })

    override suspend fun checkFavoriteStatus(movieId: Int): Result<Boolean> =
        localFavorite.checkFavoriteStatus(movieId)

    override suspend fun addMovieToFavorite(movieId: Int) {
        getMovie(movieId).onSuccess {
            local.saveMovies(Collections.singletonList(it))
            localFavorite.addMovieToFavorite(movieId)
        }
    }

    override suspend fun removeMovieFromFavorite(movieId: Int) =
        localFavorite.removeMovieFromFavorite(movieId)

    override suspend fun sync(): Boolean = local.getMovies().getResult(
        { movieIdsResult ->
            remote.getMovies(
                movieIdsResult.data.map { it.trackId })
                .getResult(
                    {
                        local.saveMovies(it.data)
                        true
                    }, {
                        it.error is DataNotAvailableException
                    })
        }, {
            it.error is DataNotAvailableException
        })
}
