package com.example.movielist.ui

import android.app.Activity.RESULT_OK
import android.os.Bundle
import android.view.View
import androidx.activity.result.contract.ActivityResultContract
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.ActionBar
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.example.movielist.R
import com.example.movielist.databinding.FragmentLoginBinding
import com.example.movielist.model.ApiStatus
import com.example.movielist.model.ApiStatus.*
import com.example.movielist.utils.isEmailValid
import com.example.movielist.utils.isPasswordValid
import com.example.movielist.utils.showToast
import com.example.movielist.viewmodel.MovieViewModel
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import dagger.hilt.android.AndroidEntryPoint
import timber.log.Timber
import java.lang.Exception
import javax.inject.Inject

@AndroidEntryPoint
class LoginFragment : Fragment(R.layout.fragment_login) {

    private lateinit var binding: FragmentLoginBinding
    private val movieViewModel by activityViewModels<MovieViewModel>()
    private val gso by lazy {
        GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.default_web_client_id))
            .requestEmail()
            .build()
    }
    private lateinit var mGoogleSignInClient: GoogleSignInClient

    @Inject lateinit var progressDialog: CustomProgressDialog

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        binding = FragmentLoginBinding.bind(view)

        binding.tvSignUp.setOnClickListener {
            findNavController().navigate(R.id.signupFragment)
        }

        binding.btnGoogleSignIn.setOnClickListener {
            googleSignIn()
        }

        binding.btnLogin.setOnClickListener {
            when {
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

    private fun googleSignIn() {
        mGoogleSignInClient = GoogleSignIn.getClient(requireContext(), gso)
        val signInIntent = mGoogleSignInClient.signInIntent
        googleSignInResult.launch(signInIntent)
    }

    private val googleSignInResult =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if(result.resultCode == RESULT_OK) {
                val task = GoogleSignIn.getSignedInAccountFromIntent(result.data)
                try {
                    val idToken = task.result?.idToken
                    idToken?.let {
                        movieViewModel.onFirebaseAuthWithGoogle(it).observe(viewLifecycleOwner, { result ->
                            progressDialog.isVisible(result.status == LOADING)
                            when(result.status) {
                                SUCCESS -> {
                                    "Successfully Log In".showToast(requireContext())
                                    findNavController().navigateUp()
                                }
                                ERROR -> result.message?.showToast(requireContext())
                                LOADING -> Unit
                            }

                        })
                    }
                }catch (e: Exception) {
                    Timber.e("Google Sign in Failed - $e")
                }
            }
        }

    private fun loginUser(email: String, password: String) {
        movieViewModel.onSignIn(email, password).observe(viewLifecycleOwner, { result ->
            progressDialog.isVisible(result.status == LOADING)
            when (result.status) {
                SUCCESS -> {
                    "Successfully Log In".showToast(requireContext())
                    findNavController().navigateUp()
                }
                ERROR -> result.message?.showToast(requireContext())
                LOADING -> Unit
            }
        })
    }
}