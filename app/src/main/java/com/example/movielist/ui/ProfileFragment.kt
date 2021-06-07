package com.example.movielist.ui

import android.os.Bundle
import android.view.View
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import coil.load
import com.example.movielist.R
import com.example.movielist.databinding.FragmentProfileBinding
import com.example.movielist.utils.showToast
import com.example.movielist.viewmodel.MovieViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ProfileFragment: Fragment(R.layout.fragment_profile) {

    private lateinit var binding: FragmentProfileBinding
    private val movieViewModel by activityViewModels<MovieViewModel>()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        binding = FragmentProfileBinding.bind(view)

        binding.rlProfile.isVisible = movieViewModel.auth.currentUser != null
        binding.llLoginFirst.isVisible = movieViewModel.auth.currentUser == null

        movieViewModel.auth.currentUser?.let { user ->
            binding.ivUserAvatar.load(user.photoUrl) {
                placeholder(R.drawable.ic_baseline_person_24)
            }
            binding.tvUserName.text = user.email
        }

        binding.btnLogout.setOnClickListener {
            movieViewModel.auth.signOut()
            "Successfully Sign Out".showToast(requireContext())
            findNavController().navigateUp()
        }

        binding.btnLogin.setOnClickListener {
            val action = ProfileFragmentDirections.actionProfileFragmentToLoginFragment()
            findNavController().navigate(action)
        }
    }
}