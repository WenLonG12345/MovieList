package com.example.movielist

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import coil.load
import com.example.movielist.databinding.FragmentDetailsMovieBinding
import com.example.movielist.model.Movie
import com.example.movielist.utils.hide
import com.example.movielist.utils.show
import com.example.movielist.utils.showToast
import com.example.movielist.viewmodel.MovieViewModel


class DetailsMovieFragment: Fragment(R.layout.fragment_details_movie) {

    private lateinit var binding: FragmentDetailsMovieBinding
    private val args by navArgs<DetailsMovieFragmentArgs>()
    private val movieViewModel by activityViewModels<MovieViewModel>()

    companion object{
        const val IMAGE_DOMAIN = "https://image.tmdb.org/t/p/w500/"
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        binding = FragmentDetailsMovieBinding.bind(view)

        binding.toolbar.apply {
            setNavigationIcon(R.drawable.ic_baseline_arrow_back_24)
            setNavigationOnClickListener {
                findNavController().navigateUp()
            }
        }

        val movie = args.movie
        movie?.let {
            binding.imageDetail.load(IMAGE_DOMAIN + it.backdrop_path)
            binding.tvRealName.text = it.original_title
            binding.tvReleaseDate.text = it.release_date

            binding.tvOverview.text = it.overview
            binding.rbRating.rating = getRatingStar(it)
        }

        binding.btnAddToFavourite.setOnClickListener {
            if(movieViewModel.auth.currentUser == null) {
                "Please login first".showToast(requireContext())
                findNavController().navigate(R.id.loginFragment)
            } else {
                // ADD FAVOURTIE MOVIE INTO FIREBASE
                if (movie != null) {
                    // Use logged in user's email as collection's key
                    movieViewModel.db.collection(movieViewModel.auth.currentUser.email)
                        .add(movie)
                        .addOnSuccessListener {
                            "Added to Favourite".showToast(requireContext())
                        }
                        .addOnFailureListener {
                            it.showToast(requireContext())
                        }
                }
            }
        }

        // Hide favourite button if coming from favourite movielist
        if(args.isFromFavorite) {
            binding.btnAddToFavourite.hide()
        } else binding.btnAddToFavourite.show()

    }

    private fun getRatingStar(movie: Movie): Float {
        return (movie.vote_average / 2).toFloat()
    }
}