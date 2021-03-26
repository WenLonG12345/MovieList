package com.example.movielist

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.example.movielist.databinding.FragmentProfileBinding
import com.example.movielist.utils.showToast
import com.example.movielist.viewmodel.MovieViewModel

class ProfileFragment: Fragment(R.layout.fragment_profile) {

    private lateinit var binding: FragmentProfileBinding
    private val movieViewModel by activityViewModels<MovieViewModel>()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        binding = FragmentProfileBinding.bind(view)

        movieViewModel.auth.currentUser?.let { user ->
            binding.ivUserAvatar.setImageURI(user.photoUrl)
            binding.tvUserName.text = user.email
        }

        binding.btnLogout.setOnClickListener {
            movieViewModel.auth.signOut()
            "Successfully Sign Out".showToast(requireContext())
            findNavController().navigateUp()
        }

        binding.rlFavouriteMovie.setOnClickListener {
            findNavController().navigate(R.id.favouriteMovieFragment)
        }

    }
}