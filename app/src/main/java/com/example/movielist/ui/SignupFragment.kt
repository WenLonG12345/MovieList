package com.example.movielist.ui

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.example.movielist.R
import com.example.movielist.databinding.FragmentSignUpBinding
import com.example.movielist.model.ApiStatus
import com.example.movielist.model.ApiStatus.*
import com.example.movielist.utils.isEmailValid
import com.example.movielist.utils.isPasswordValid
import com.example.movielist.utils.showToast
import com.example.movielist.viewmodel.MovieViewModel
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class SignupFragment: Fragment(R.layout.fragment_sign_up) {

    private lateinit var binding: FragmentSignUpBinding
    private val movieViewModel by activityViewModels<MovieViewModel>()

    @Inject lateinit var progressDialog: CustomProgressDialog

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        binding = FragmentSignUpBinding.bind(view)

        binding.btnSignUp.setOnClickListener {
            when{
                binding.etEmail.text.toString().isEmpty() -> {
                    binding.etEmail.error = "Email cannot be empty"
                }
                binding.etPassword.text.toString().isEmpty() -> {
                    binding.etPassword.error = "Password cannot be empty"
                }
                binding.etEmail.text.toString().isEmailValid() -> {
                    binding.etEmail.error = "Invalid email"
                }
                !binding.etPassword.text.toString().isPasswordValid() -> {
                    binding.etPassword.error = "Password size must be more than 7"
                }
                else -> {
                    signUpUser(binding.etEmail.text.toString(), binding.etPassword.text.toString())
                }
            }
        }

        binding.tvBackToLogin.setOnClickListener {
            findNavController().navigateUp()
        }

    }

    private fun signUpUser(email: String, password: String) {
        movieViewModel.onCreateNewUser(email, password).observe(viewLifecycleOwner, { result ->
            progressDialog.isVisible(result.status == LOADING)
            when(result.status) {
                SUCCESS -> {
                    "Successfully Registered".showToast(requireContext())
                    val action = SignupFragmentDirections.actionSignupFragmentToMovieFragment()
                    findNavController().navigate(action)
                }
                ERROR -> result.message?.showToast(requireContext())
                LOADING -> Unit
            }
        })
    }
}