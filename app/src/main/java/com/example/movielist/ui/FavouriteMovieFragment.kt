package com.example.movielist.ui

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.movielist.ui.adapter.FavouriteMovieAdapter
import com.example.movielist.FavouriteMovieFragmentDirections
import com.example.movielist.R
import com.example.movielist.databinding.FragmentFavouriteMovieBinding
import com.example.movielist.model.Movie
import com.example.movielist.utils.hide
import com.example.movielist.utils.show
import com.example.movielist.utils.showToast
import com.example.movielist.viewmodel.MovieViewModel


class FavouriteMovieFragment: Fragment(R.layout.fragment_favourite_movie) {

    private lateinit var binding: FragmentFavouriteMovieBinding
    private val movieViewModel by activityViewModels<MovieViewModel>()
    private val favouriteMovieAdapter: FavouriteMovieAdapter by lazy {
        FavouriteMovieAdapter { navigateToDetailsFragment(it) }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        binding = FragmentFavouriteMovieBinding.bind(view)

        binding.rvFavouriteMovies.run {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = favouriteMovieAdapter
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
                                favouriteMovieAdapter.submitList(movieList)
                            } else {
                                binding.tvEmptySearch.show()
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
            FavouriteMovieFragmentDirections.actionFavouriteMovieFragmentToDetailsMovieFragment(
                movie,
                true
            )
        findNavController().navigate(action)
    }

}