package com.example.movielist.ui

import android.os.Bundle
import android.view.View
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import coil.load
import com.example.movielist.R
import com.example.movielist.databinding.FragmentDetailsMovieBinding
import com.example.movielist.model.ApiStatus
import com.example.movielist.model.ApiStatus.*
import com.example.movielist.model.FirebaseEvent
import com.example.movielist.model.Movie
import com.example.movielist.ui.adapter.VideoListAdapter
import com.example.movielist.utils.Constants.IMAGE_DOMAIN
import com.example.movielist.utils.hide
import com.example.movielist.utils.show
import com.example.movielist.utils.showToast
import com.example.movielist.viewmodel.MovieViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collect
import javax.inject.Inject

@AndroidEntryPoint
class DetailsMovieFragment: Fragment(R.layout.fragment_details_movie) {

    private lateinit var binding: FragmentDetailsMovieBinding
    private val args by navArgs<DetailsMovieFragmentArgs>()
    private val movieViewModel by activityViewModels<MovieViewModel>()
    private val videoListAdapter by lazy { VideoListAdapter() }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        binding = FragmentDetailsMovieBinding.bind(view)

        binding.toolbar.setNavigationOnClickListener {
            findNavController().navigateUp()
        }
        val movie = args.movie
        initView(movie)
        setupRecyclerView()
        movieViewModel.onCheckFavMovieInFirestore(movie)

        // Hide favorite button if coming from favorite movielist
        binding.btnAddToFavorite.isVisible = !args.isFromFavorite

        binding.btnAddToFavorite.setOnClickListener {
            if(movie != null) {
                movieViewModel.onAddFavMovieToFirestore(movie)
            }
        }

        binding.btnRemoveFromFavorite.setOnClickListener {
            if(movie != null) {
                movieViewModel.onDeleteFavMovieFromFirestore(movie)
            }
        }

        lifecycleScope.launchWhenStarted {
            movieViewModel.firebaseEvent.collect { event ->
                when(event) {
                    is FirebaseEvent.AddFavMovieToFirestore -> {
                        event.msg?.showToast(requireContext())
                        if(event.status) {
                            setFavoriteMovieUI()
                        }
                    }
                    is FirebaseEvent.DeleteFavMovieFromFirestore -> {
                        event.msg?.showToast(requireContext())
                        if(event.status) {
                            setNormalMovieUI()
                        }
                    }
                    is FirebaseEvent.NavigateToLoginFragment -> {
                        event.msg.showToast(requireContext())
                        findNavController().navigate(R.id.loginFragment)
                    }
                    is FirebaseEvent.CheckIsFavMovie -> {
                        if(event.status) {
                            setFavoriteMovieUI()
                        } else setNormalMovieUI()
                    }
                }
            }
        }
    }

    private fun setupRecyclerView() {
        binding.rvTrailer.apply {
            adapter = videoListAdapter
            layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
        }

        movieViewModel.trailer.observe(viewLifecycleOwner, {
            videoListAdapter.submitList(it)
        })
    }

    private fun initView(movie: Movie?) {
        binding.progressBar.hide()
        movie?.let {
            movieViewModel.movieId.value = it.id
            binding.imageDetail.load(IMAGE_DOMAIN + it.backdrop_path)
            binding.tvRealName.text = it.original_title
            binding.tvReleaseDate.text = it.release_date

            binding.tvOverview.text = it.overview
            binding.rbRating.rating = getRatingStar(it)
        }
    }

    private fun setFavoriteMovieUI() {
        binding.btnAddToFavorite.hide()
        binding.btnRemoveFromFavorite.show()
        binding.ivFavoriteStars.show()
    }

    private fun setNormalMovieUI() {
        binding.btnAddToFavorite.show()
        binding.btnRemoveFromFavorite.hide()
        binding.ivFavoriteStars.hide()
    }

    private fun getRatingStar(movie: Movie): Float {
        return (movie.vote_average / 2).toFloat()
    }
}