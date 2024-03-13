package com.appetiser.itunesmovie.ui.movie

import androidx.lifecycle.viewModelScope
import androidx.paging.CombinedLoadStates
import androidx.paging.LoadState
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.appetiser.data.util.DispatchersProvider
import com.appetiser.domain.usecase.AddMovieToFavorite
import com.appetiser.domain.usecase.CheckFavoriteStatus
import com.appetiser.domain.usecase.RemoveMovieFromFavorite
import com.appetiser.domain.util.onSuccess
import com.appetiser.itunesmovie.base.BaseViewModel
import com.appetiser.itunesmovie.common.util.singleSharedFlow
import com.appetiser.itunesmovie.entities.MovieListItem
import com.appetiser.itunesmovie.ui.movie.usecase.GetMoviesWithSeparators
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import javax.inject.Inject

@HiltViewModel
class MovieViewModel @Inject constructor(
    getMoviesWithSeparators: GetMoviesWithSeparators,
    dispatchers: DispatchersProvider,
    private val checkFavoriteStatus: CheckFavoriteStatus,
    private val removeMovieFromFavorite: RemoveMovieFromFavorite,
    private val addMovieToFavorite: AddMovieToFavorite,
) : BaseViewModel(dispatchers) {

    data class MoviesUiState(
        val showLoading: Boolean = true,
        val errorMessage: String? = null
    )

    sealed class NavigationState {
        data class MovieDetails(val movieId: Int) : NavigationState()
    }

    val movies: Flow<PagingData<MovieListItem>> = getMoviesWithSeparators.movies(
        pageSize = 200
    ).cachedIn(viewModelScope)

    private val _uiState: MutableStateFlow<MoviesUiState> = MutableStateFlow(MoviesUiState())
    val uiState = _uiState.asStateFlow()

    private val _navigationState: MutableSharedFlow<NavigationState> = singleSharedFlow()
    val navigationState = _navigationState.asSharedFlow()

    fun onMovieClicked(movieId: Int) =
        _navigationState.tryEmit(NavigationState.MovieDetails(movieId))

    fun onMovieFavoriteClicked(movieId: Int) = launchOnMainImmediate {
        checkFavoriteStatus(movieId).onSuccess { isFavorite ->
            if (isFavorite) removeMovieFromFavorite(movieId) else addMovieToFavorite(movieId)
        }
    }

    fun onLoadStateUpdate(loadState: CombinedLoadStates) {
        val showLoading = loadState.refresh is LoadState.Loading

        val error = when (val refresh = loadState.refresh) {
            is LoadState.Error -> refresh.error.message
            else -> null
        }

        _uiState.update { it.copy(showLoading = showLoading, errorMessage = error) }
    }
}
