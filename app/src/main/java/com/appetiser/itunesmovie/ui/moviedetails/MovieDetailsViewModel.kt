package com.appetiser.itunesmovie.ui.moviedetails

import androidx.annotation.VisibleForTesting
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.appetiser.data.util.DispatchersProvider
import com.appetiser.domain.entities.MovieEntity
import com.appetiser.domain.usecase.AddMovieToFavorite
import com.appetiser.domain.usecase.CheckFavoriteStatus
import com.appetiser.domain.usecase.GetMovieDetails
import com.appetiser.domain.usecase.RemoveMovieFromFavorite
import com.appetiser.domain.util.Result
import com.appetiser.domain.util.getResult
import com.appetiser.domain.util.onSuccess
import com.appetiser.itunesmovie.base.BaseViewModel
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

class MovieDetailsViewModel @AssistedInject constructor(
    @Assisted
    private var movieId: Int,
    private val getMovieDetails: GetMovieDetails,
    private val checkFavoriteStatus: CheckFavoriteStatus,
    private val addMovieToFavorite: AddMovieToFavorite,
    private val removeMovieFromFavorite: RemoveMovieFromFavorite,
    dispatchers: DispatchersProvider
) : BaseViewModel(dispatchers) {

    data class MovieDetailsUiState(
        val title: String = "",
        val description: String = "",
        val imageUrl: String = "",
        val isFavorite: Boolean = false,
    )

    private val _uiState: MutableStateFlow<MovieDetailsUiState> = MutableStateFlow(
        MovieDetailsUiState()
    )
    val uiState = _uiState.asStateFlow()

    init {
        onInitialState()
    }

    @VisibleForTesting(otherwise = VisibleForTesting.PRIVATE)
    fun onInitialState() = launchOnMainImmediate {
        val isFavorite = async {
            checkFavoriteStatus(movieId).getResult({ favoriteResult -> favoriteResult.data },
                                                   { false })
        }
        getMovieById(movieId).onSuccess {
            _uiState.value = MovieDetailsUiState(
                title = it.title,
                description = it.description,
                imageUrl = it.image,
                isFavorite = isFavorite.await()
            )
        }
    }

    fun onFavoriteClicked() = launchOnMainImmediate {
        checkFavoriteStatus(movieId).onSuccess { isFavorite ->
            if (isFavorite) removeMovieFromFavorite(movieId) else addMovieToFavorite(movieId)
            _uiState.update { it.copy(isFavorite = !isFavorite) }
        }
    }

    private suspend fun getMovieById(movieId: Int): Result<MovieEntity> = getMovieDetails(movieId)

    private suspend fun checkFavoriteStatus(movieId: Int): Result<Boolean> =
        checkFavoriteStatus.invoke(movieId)

    @AssistedFactory
    interface Factory {
        fun create(movieId: Int): MovieDetailsViewModel
    }

    companion object {
        @Suppress("UNCHECKED_CAST")
        fun provideFactory(
            assistedFactory: Factory,
            movieId: Int
        ): ViewModelProvider.Factory = object : ViewModelProvider.Factory {
            override fun <T : ViewModel> create(modelClass: Class<T>): T =
                assistedFactory.create(movieId) as T
        }
    }
}