package com.appetiser.itunesmovie.ui.search

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import androidx.paging.CombinedLoadStates
import androidx.paging.LoadState
import androidx.paging.PagingData
import androidx.paging.cachedIn
import androidx.paging.map
import com.appetiser.data.util.DispatchersProvider
import com.appetiser.domain.usecase.AddMovieToFavorite
import com.appetiser.domain.usecase.CheckFavoriteStatus
import com.appetiser.domain.usecase.RemoveMovieFromFavorite
import com.appetiser.domain.usecase.SearchMovies
import com.appetiser.domain.util.onSuccess
import com.appetiser.itunesmovie.base.BaseViewModel
import com.appetiser.itunesmovie.common.util.singleSharedFlow
import com.appetiser.itunesmovie.entities.MovieListItem
import com.appetiser.itunesmovie.mapper.toPresentation
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.*
import javax.inject.Inject


@HiltViewModel
class SearchViewModel @Inject constructor(
    private val searchMovies: SearchMovies,
    private val savedStateHandle: SavedStateHandle,
    private val checkFavoriteStatus: CheckFavoriteStatus,
    private val removeMovieFromFavorite: RemoveMovieFromFavorite,
    private val addMovieToFavorite: AddMovieToFavorite,
    dispatchers: DispatchersProvider
) : BaseViewModel(dispatchers) {

    data class SearchUiState(
        val showDefaultState: Boolean = true,
        val showLoading: Boolean = false,
        val showNoMoviesFound: Boolean = false,
        val errorMessage: String? = null
    )

    sealed class NavigationState {
        data class MovieDetails(val movieId: Int) : NavigationState()
    }

    @OptIn(ExperimentalCoroutinesApi::class, FlowPreview::class)
    var movies: Flow<PagingData<MovieListItem>> =
        savedStateHandle.getStateFlow(KEY_SEARCH_QUERY, "")
            .onEach { query ->
                _uiState.value = if (query.isNotEmpty()) SearchUiState(
                    showDefaultState = false,
                    showLoading = true
                ) else SearchUiState()
            }
            .debounce(500)
            .filter { it.isNotEmpty() }
            .flatMapLatest { query ->
                searchMovies(query, 200).map { pagingData ->
                    pagingData.map { movieEntity -> movieEntity.toPresentation() as MovieListItem }
                }
            }.cachedIn(viewModelScope)

    private val _uiState: MutableStateFlow<SearchUiState> = MutableStateFlow(SearchUiState())
    val uiState = _uiState.asStateFlow()

    private val _navigationState: MutableSharedFlow<NavigationState> = singleSharedFlow()
    val navigationState = _navigationState.asSharedFlow()

    fun onSearch(query: String) {
        savedStateHandle[KEY_SEARCH_QUERY] = query
    }

    fun onMovieClicked(movieId: Int) =
        _navigationState.tryEmit(NavigationState.MovieDetails(movieId))

    fun onMovieFavoriteClicked(movieId: Int) = launchOnMainImmediate {
        checkFavoriteStatus(movieId).onSuccess { isFavorite ->
            if (isFavorite) removeMovieFromFavorite(movieId) else addMovieToFavorite(movieId)
        }
    }

    fun getSearchQuery(): CharSequence? = savedStateHandle.get<String>(KEY_SEARCH_QUERY)

    fun onLoadStateUpdate(loadState: CombinedLoadStates, itemCount: Int) {
        val showLoading = loadState.refresh is LoadState.Loading
        val showNoData = itemCount < 1

        val error = when (val refresh = loadState.refresh) {
            is LoadState.Error -> refresh.error.message
            else -> null
        }

        _uiState.update {
            it.copy(
                showLoading = showLoading,
                showNoMoviesFound = showNoData,
                errorMessage = error
            )
        }
    }

    companion object {
        const val KEY_SEARCH_QUERY = "search_query"
    }
}
