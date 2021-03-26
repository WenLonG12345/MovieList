package com.example.movielist.ui

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.example.movielist.R
import com.example.movielist.databinding.FragmentLoginBinding
import com.example.movielist.utils.isEmailValid
import com.example.movielist.utils.isPasswordValid
import com.example.movielist.utils.showToast
import com.example.movielist.viewmodel.MovieViewModel

class LoginFragment: Fragment(R.layout.fragment_login) {

    private lateinit var binding: FragmentLoginBinding
    private val movieViewModel by activityViewModels<MovieViewModel>()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        binding = FragmentLoginBinding.bind(view)

        binding.tvSignUp.setOnClickListener {
            findNavController().navigate(R.id.signupFragment)
        }

        binding.btnLogin.setOnClickListener {
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
                    loginUser(binding.etEmail.text.toString(), binding.etPassword.text.toString())
                }
            }
        }
    }

    private fun loginUser(email: String, password: String) {
        movieViewModel.auth.signInWithEmailAndPassword(email, password).addOnCompleteListener { task ->
            try{
                if(task.isSuccessful) {
                    "Successfully Log In".showToast(requireContext())
                    findNavController().navigateUp()

                } else {
                    task.exception?.let {
                        throw it
                    }
                }
            }catch (e: Exception) {
                e.message?.showToast(requireContext())
            }
        }
    }
}