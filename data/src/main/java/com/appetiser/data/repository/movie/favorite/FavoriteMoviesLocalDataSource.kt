package com.appetiser.data.repository.movie.favorite

import androidx.paging.PagingSource
import com.appetiser.data.db.favoritemovies.FavoriteMovieDao
import com.appetiser.data.entities.FavoriteMovieDbData
import com.appetiser.data.entities.MovieDbData
import com.appetiser.data.exception.DataNotAvailableException
import com.appetiser.data.util.DiskExecutor
import com.appetiser.domain.util.Result
import kotlinx.coroutines.asCoroutineDispatcher
import kotlinx.coroutines.withContext

class FavoriteMoviesLocalDataSource(
    private val executor: DiskExecutor,
    private val favoriteMovieDao: FavoriteMovieDao,
) : FavoriteMoviesDataSource.Local {

    override fun favoriteMovies(): PagingSource<Int, MovieDbData> =
        favoriteMovieDao.favoriteMovies()

    override suspend fun addMovieToFavorite(movieId: Int) =
        withContext(executor.asCoroutineDispatcher()) {
            favoriteMovieDao.add(FavoriteMovieDbData(movieId))
        }

    override suspend fun removeMovieFromFavorite(movieId: Int) =
        withContext(executor.asCoroutineDispatcher()) {
            favoriteMovieDao.remove(movieId)
        }

    override suspend fun checkFavoriteStatus(movieId: Int): Result<Boolean> =
        withContext(executor.asCoroutineDispatcher()) {
            return@withContext Result.Success(favoriteMovieDao.get(movieId) != null)
        }

    override suspend fun isFavoriteMovie(movieId: Int): Boolean =
        withContext(executor.asCoroutineDispatcher()) {
            return@withContext favoriteMovieDao.get(movieId) != null
        }

    override suspend fun getFavoriteMovieIds(): Result<List<Int>> =
        withContext(executor.asCoroutineDispatcher()) {
            val movieIds = favoriteMovieDao.getAll().map { it.movieId }
            return@withContext if (movieIds.isNotEmpty()) {
                Result.Success(movieIds)
            } else {
                Result.Error(DataNotAvailableException())
            }
        }
}
