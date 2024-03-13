package com.appetiser.itunesmovie.ui.favorites

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import androidx.core.view.isVisible
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.paging.CombinedLoadStates
import com.appetiser.itunesmovie.base.BaseFragment
import com.appetiser.itunesmovie.common.util.createMovieGridLayoutManager
import com.appetiser.itunesmovie.common.util.hide
import com.appetiser.itunesmovie.common.util.launchAndRepeatWithViewLifecycle
import com.appetiser.itunesmovie.databinding.FragmentFavoritesBinding
import com.appetiser.itunesmovie.ui.adapter.movie.MoviePagingAdapter
import com.appetiser.itunesmovie.ui.favorites.FavoritesViewModel.FavoriteUiState
import com.appetiser.itunesmovie.ui.favorites.FavoritesViewModel.NavigationState
import com.appetiser.itunesmovie.ui.favorites.FavoritesViewModel.NavigationState.MovieDetails
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class FavoritesFragment : BaseFragment<FragmentFavoritesBinding>() {

    private val viewModel: FavoritesViewModel by viewModels()

    private val movieAdapter by lazy {
        MoviePagingAdapter(
            viewModel::onMovieClicked,
            viewModel::onMovieFavoriteClicked,
            imageFixedSize = getImageFixedSize()
        )
    }

    private val loadStateListener: (CombinedLoadStates) -> Unit = {
        viewModel.onLoadStateUpdate(it, movieAdapter.itemCount)
    }

    override fun inflateViewBinding(inflater: LayoutInflater): FragmentFavoritesBinding =
        FragmentFavoritesBinding.inflate(inflater)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        setupViews()
        setupListeners()
        setupObservers()
    }

    private fun setupViews() {
        setupRecyclerView()
    }

    private fun setupListeners() {
        movieAdapter.addLoadStateListener(loadStateListener)
    }

    private fun setupRecyclerView() = with(binding.recyclerView) {
        adapter = movieAdapter
        layoutManager = createMovieGridLayoutManager(requireContext(), movieAdapter)
        setHasFixedSize(true)
        setItemViewCacheSize(0)
    }

    private fun setupObservers() = with(viewModel) {
        launchAndRepeatWithViewLifecycle {
            launch { movies.collect { movieAdapter.submitData(it) } }
            launch { uiState.collect { handleFavoriteUiState(it) } }
            launch { navigationState.collect { handleNavigationState(it) } }
        }
    }

    private fun handleFavoriteUiState(favoriteUiState: FavoriteUiState) = with(favoriteUiState) {
        binding.progressBar.isVisible = isLoading
        if (isLoading) {
            if (binding.noDataView.isVisible) binding.noDataView.hide()
        } else {
            binding.noDataView.isVisible = noDataAvailable
        }
    }

    private fun handleNavigationState(navigationState: NavigationState) = when (navigationState) {
        is MovieDetails -> navigateToMovieDetails(navigationState.movieId)
    }

    private fun navigateToMovieDetails(movieId: Int) = findNavController().navigate(
        FavoritesFragmentDirections.toMovieDetailsActivity(movieId)
    )

    override fun onDestroyView() {
        super.onDestroyView()
        movieAdapter.removeLoadStateListener(loadStateListener)
    }

    private fun getImageFixedSize(): Int =
        requireContext().applicationContext.resources.displayMetrics.widthPixels / 3

}