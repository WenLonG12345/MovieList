package com.example.movielist

import android.os.Bundle
import android.view.View
import androidx.appcompat.widget.SearchView
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import androidx.paging.LoadState
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.movielist.databinding.FragmentMovieBinding
import com.example.movielist.model.Movie
import com.example.movielist.utils.hide
import com.example.movielist.utils.show
import com.example.movielist.utils.showToast
import com.example.movielist.viewmodel.MovieViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MovieFragment: Fragment(R.layout.fragment_movie) {

    private lateinit var binding: FragmentMovieBinding
    private val movieAdapter: MovieAdapter by lazy { MovieAdapter{ navigateToDetailsFragment(it)} }
    private val movieViewModel by activityViewModels<MovieViewModel>()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        binding = FragmentMovieBinding.bind(view)

        binding.rvMovies.run {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = movieAdapter
        }

        movieViewModel.movies.observe(viewLifecycleOwner, { pagingData ->
            movieAdapter.submitData(viewLifecycleOwner.lifecycle, pagingData)
        })

        movieViewModel.auth.currentUser?.let { user ->
            binding.ivProfile.setImageURI(user.photoUrl)
        }

        movieAdapter.addLoadStateListener { loadState ->
            when(loadState.refresh) {
                is LoadState.NotLoading -> {
                    binding.progressBar.hide()
                    binding.rvMovies.show()
                }
                is LoadState.Loading -> {
                    binding.progressBar.show()
                    binding.rvMovies.hide()
                }
                is LoadState.Error -> {
                    val state = loadState.refresh as LoadState.Error
                    binding.progressBar.hide()
                    "Load Error: ${state.error.message}".showToast(requireContext())
                }
            }

            // Empty View
            if(loadState.source.refresh is LoadState.NotLoading &&
                    loadState.append.endOfPaginationReached &&
                    movieAdapter.itemCount < 1) {
                binding.rvMovies.hide()
                binding.tvEmptySearch.show()
            } else {
                binding.tvEmptySearch.hide()
            }
        }

        binding.ivProfile.setOnClickListener {
            if(movieViewModel.auth.currentUser == null) {
                findNavController().navigate(R.id.loginFragment)
            } else {
                findNavController().navigate(R.id.profileFragment)
            }

        }

        binding.movieSearchView.apply {
            setOnQueryTextListener(object: SearchView.OnQueryTextListener{
                override fun onQueryTextSubmit(query: String?): Boolean {
                    if(query != null) {
                        binding.rvMovies.scrollToPosition(0)
                        movieViewModel.setSearchQuery(query)
                        binding.movieSearchView.clearFocus()
                    }
                    return true
                }

                override fun onQueryTextChange(newText: String?): Boolean {
                    return true
                }
            })
        }

        binding.movieSearchView.setOnCloseListener {
            movieViewModel.setSearchQuery("")
            false
        }
    }


    private fun navigateToDetailsFragment(movie: Movie) {
        val action = MovieFragmentDirections.actionMovieFragmentToDetailsMovieFragment(movie, false)
        findNavController().navigate(action)
    }
}