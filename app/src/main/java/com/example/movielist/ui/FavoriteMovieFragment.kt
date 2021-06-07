package com.example.movielist.ui

import android.os.Bundle
import android.view.View
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.movielist.R
import com.example.movielist.databinding.FragmentFavoriteMovieBinding
import com.example.movielist.model.ApiStatus
import com.example.movielist.model.Movie
import com.example.movielist.ui.adapter.FavoriteMovieAdapter
import com.example.movielist.utils.showToast
import com.example.movielist.viewmodel.MovieViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class FavoriteMovieFragment: Fragment(R.layout.fragment_favorite_movie) {

    private lateinit var binding: FragmentFavoriteMovieBinding
    private val movieViewModel by activityViewModels<MovieViewModel>()
    private val favoriteMovieAdapter: FavoriteMovieAdapter by lazy {
        FavoriteMovieAdapter { navigateToDetailsFragment(it) }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        binding = FragmentFavoriteMovieBinding.bind(view)

        binding.rvFavoriteMovies.run {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = favoriteMovieAdapter
        }

        binding.llLoginFirst.isVisible = movieViewModel.auth.currentUser == null
        binding.progressBar.isVisible = movieViewModel.auth.currentUser != null

        binding.btnLogin.setOnClickListener {
            val action = FavoriteMovieFragmentDirections.actionFavoriteMovieFragmentToLoginFragment()
            findNavController().navigate(action)
        }

        movieViewModel.onGetFavMovieFromFirestore().observe(viewLifecycleOwner, { result ->
            binding.progressBar.isVisible = result.status == ApiStatus.LOADING

            when(result.status) {
                ApiStatus.SUCCESS -> {
                    result.data?.let {
                        binding.tvEmptySearch.isVisible = it.isEmpty()
                        binding.rvFavoriteMovies.isVisible = it.isNotEmpty()
                        if(it.isNotEmpty()) {
                            favoriteMovieAdapter.submitList(it)
                        }
                    }
                }
                ApiStatus.ERROR -> result.message?.showToast(requireContext())
                ApiStatus.LOADING -> Unit
            }
        })
    }

    private fun navigateToDetailsFragment(movie: Movie) {
        val action =
            FavoriteMovieFragmentDirections.actionFavoriteMovieFragmentToDetailsMovieFragment(
                movie,
                true
            )
        findNavController().navigate(action)
    }

}