package com.appetiser.itunesmovie.ui.movie

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.widget.Toast
import androidx.core.view.isEmpty
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.paging.CombinedLoadStates
import com.appetiser.itunesmovie.MovieDetailsGraphDirections
import com.appetiser.itunesmovie.base.BaseFragment
import com.appetiser.itunesmovie.common.util.NetworkMonitor
import com.appetiser.itunesmovie.common.util.createMovieGridLayoutManager
import com.appetiser.itunesmovie.common.util.launchAndRepeatWithViewLifecycle
import com.appetiser.itunesmovie.databinding.FragmentMovieBinding
import com.appetiser.itunesmovie.ui.adapter.movie.MoviePagingAdapter
import com.appetiser.itunesmovie.ui.adapter.movie.loadstate.MovieLoadStateAdapter
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import javax.inject.Inject


@AndroidEntryPoint
class MovieFragment : BaseFragment<FragmentMovieBinding>() {

    @Inject
    lateinit var networkMonitor: NetworkMonitor

    private val viewModel: MovieViewModel by viewModels()

    private val movieAdapter by lazy {
        MoviePagingAdapter(
            viewModel::onMovieClicked,
            viewModel::onMovieFavoriteClicked,
            getImageFixedSize()
        )
    }

    private val detailsNavController by lazy {
        binding.container.getFragment<Fragment>().findNavController()
    }

    private val loadStateListener: (CombinedLoadStates) -> Unit = {
        viewModel.onLoadStateUpdate(it)
    }

    override fun inflateViewBinding(inflater: LayoutInflater): FragmentMovieBinding =
        FragmentMovieBinding.inflate(inflater)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        setupViews()
        setupListeners()
        observeViewModel()
    }

    private fun setupViews() {
        setupRecyclerView()
    }

    private fun setupListeners() {
        movieAdapter.addLoadStateListener(loadStateListener)
    }

    private fun setupRecyclerView() = with(binding.recyclerView) {
        adapter = movieAdapter.withLoadStateFooter(MovieLoadStateAdapter { movieAdapter.retry() })
        layoutManager = createMovieGridLayoutManager(requireContext(), movieAdapter)
        setHasFixedSize(true)
        itemAnimator = null
        setItemViewCacheSize(0)
    }

    private fun observeViewModel() = with(viewModel) {
        launchAndRepeatWithViewLifecycle {
            launch { movies.collect { movieAdapter.submitData(it) } }
            launch { uiState.collect { handleFeedUiState(it) } }
            launch { navigationState.collect { handleNavigationState(it) } }
            launch { networkMonitor.networkState.collect { handleNetworkState(it) } }
        }
    }

    private fun handleNetworkState(state: NetworkMonitor.NetworkState) {
        if (state.isAvailable() && viewModel.uiState.value.errorMessage != null) movieAdapter.retry()
    }

    private fun handleFeedUiState(it: MovieViewModel.MoviesUiState) {
        binding.progressBar.isVisible = it.showLoading && binding.recyclerView.isEmpty()
        if (it.errorMessage != null) Toast.makeText(
            requireActivity().applicationContext,
            it.errorMessage,
            Toast.LENGTH_LONG
        ).show()
    }

    private fun handleNavigationState(state: MovieViewModel.NavigationState) = when (state) {
        is MovieViewModel.NavigationState.MovieDetails -> showOrNavigateToMovieDetails(state.movieId)
    }

    private fun showOrNavigateToMovieDetails(movieId: Int) = if (binding.root.isSlideable) {
        navigateToMovieDetails(movieId)
    } else {
        showMovieDetails(movieId)
    }

    private fun navigateToMovieDetails(movieId: Int) = findNavController().navigate(
        MovieFragmentDirections.toMovieDetailsActivity(movieId)
    )

    private fun showMovieDetails(movieId: Int) = detailsNavController.navigate(
        MovieDetailsGraphDirections.toMovieDetails(movieId)
    )

    override fun onDestroyView() {
        super.onDestroyView()
        movieAdapter.removeLoadStateListener(loadStateListener)
    }

    private fun getImageFixedSize(): Int =
        requireContext().applicationContext.resources.displayMetrics.widthPixels / 3

}