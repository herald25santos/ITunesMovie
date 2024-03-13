package com.appetiser.itunesmovie.ui.favorites

import androidx.lifecycle.viewModelScope
import androidx.paging.CombinedLoadStates
import androidx.paging.LoadState
import androidx.paging.PagingData
import androidx.paging.cachedIn
import androidx.paging.map
import com.appetiser.data.util.DispatchersProvider
import com.appetiser.domain.usecase.AddMovieToFavorite
import com.appetiser.domain.usecase.CheckFavoriteStatus
import com.appetiser.domain.usecase.GetFavoriteMovies
import com.appetiser.domain.usecase.RemoveMovieFromFavorite
import com.appetiser.domain.util.onSuccess
import com.appetiser.itunesmovie.base.BaseViewModel
import com.appetiser.itunesmovie.common.util.singleSharedFlow
import com.appetiser.itunesmovie.entities.MovieListItem
import com.appetiser.itunesmovie.mapper.toPresentation
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.update
import javax.inject.Inject


@HiltViewModel
class FavoritesViewModel @Inject constructor(
    getFavoriteMovies: GetFavoriteMovies,
    dispatchers: DispatchersProvider,
    private val checkFavoriteStatus: CheckFavoriteStatus,
    private val removeMovieFromFavorite: RemoveMovieFromFavorite,
    private val addMovieToFavorite: AddMovieToFavorite,
) : BaseViewModel(dispatchers) {

    data class FavoriteUiState(
        val isLoading: Boolean = true,
        val noDataAvailable: Boolean = false
    )

    sealed class NavigationState {
        data class MovieDetails(val movieId: Int) : NavigationState()
    }

    val movies: Flow<PagingData<MovieListItem>> = getFavoriteMovies(200).map {
        it.map { it.toPresentation() as MovieListItem }
    }.cachedIn(viewModelScope)

    private val _uiState: MutableStateFlow<FavoriteUiState> = MutableStateFlow(FavoriteUiState())
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

    fun onLoadStateUpdate(loadState: CombinedLoadStates, itemCount: Int) {
        val showLoading = loadState.refresh is LoadState.Loading
        val showNoData = loadState.append.endOfPaginationReached && itemCount < 1

        _uiState.update {
            it.copy(
                isLoading = showLoading,
                noDataAvailable = showNoData
            )
        }
    }
}
