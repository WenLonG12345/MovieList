package com.example.movielist.ui

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.movielist.R
import com.example.movielist.databinding.FragmentFavoriteMovieBinding
import com.example.movielist.model.Movie
import com.example.movielist.ui.adapter.FavoriteMovieAdapter
import com.example.movielist.utils.hide
import com.example.movielist.utils.show
import com.example.movielist.utils.showToast
import com.example.movielist.viewmodel.MovieViewModel


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

        movieViewModel.auth.currentUser?.email?.let { email ->
            movieViewModel.db.collection(email).get()
                .addOnCompleteListener { task ->
                    binding.progressBar.hide()
                    val movieList = mutableListOf<Movie>()
                    if(task.isSuccessful) {
                        if(task.result != null) {
                            for (document in task.result!!) {
                                val movie = document.toObject(Movie::class.java)
                                movieList.add(movie)
                            }

                            if(movieList.isNotEmpty()) {
                                binding.tvEmptySearch.hide()
                                binding.rvFavoriteMovies.show()
                                favoriteMovieAdapter.submitList(movieList)
                            } else {
                                binding.tvEmptySearch.show()
                                binding.rvFavoriteMovies.hide()
                            }
                        }
                    } else {
                        "Fetch data failed".showToast(requireContext())
                    }
                }
        }
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