package com.example.movielist.viewmodel

import androidx.lifecycle.*
import androidx.paging.cachedIn
import com.example.movielist.model.*
import com.example.movielist.model.ApiStatus.*
import com.example.movielist.repository.MovieRepository
import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import timber.log.Timber
import java.lang.Exception
import javax.inject.Inject

@HiltViewModel
class MovieViewModel @Inject constructor(
    private val movieRepository: MovieRepository
) : ViewModel() {

    var auth: FirebaseAuth = FirebaseAuth.getInstance()
    private var fireStore = FirebaseFirestore.getInstance()

    val searchQuery = MutableStateFlow("")
    val sortOrder = MutableStateFlow(SortOrder.BY_UPCOMING)

    val movieId = MutableStateFlow(0)

    private val firebaseEventChannel = Channel<FirebaseEvent>()
    val firebaseEvent = firebaseEventChannel.receiveAsFlow()

    private val moviesFlow = combine(
        searchQuery,
        sortOrder
    ) { query, sortOrder ->
        Pair(query, sortOrder)
    }.flatMapLatest { (query, order) ->
        if (query.isNotEmpty()) {
            movieRepository.getSearchMovieListStream(query).cachedIn(viewModelScope)
        } else {
            movieRepository.getMovieListStream(order).cachedIn(viewModelScope)
        }
    }
    val movies = moviesFlow.asLiveData(viewModelScope.coroutineContext)

    private val trailerFlow = movieId.flatMapLatest {
        movieRepository.loadVideoList(it)
    }
    val trailer = trailerFlow.asLiveData(viewModelScope.coroutineContext)

    fun onCheckFavMovieInFirestore(movie: Movie?) {
        viewModelScope.launch {
            movie?.let { movie ->
                if (auth.currentUser == null) {
                    firebaseEventChannel.send(FirebaseEvent.CheckIsFavMovie(false))
                } else {
                    movieRepository.checkFavMovieInFirestore(fireStore, auth.currentUser, movie)
                        .collect { result ->
                            when (result.status) {
                                SUCCESS -> {
                                    result.data?.let {
                                        firebaseEventChannel.send(FirebaseEvent.CheckIsFavMovie(it))
                                    }
                                }
                                ERROR -> Timber.e(result.message)
                                LOADING -> Unit
                            }
                        }
                }
            }
        }
    }

    fun getMovieFromFirestore(movie: Movie?): LiveData<List<DocumentSnapshot>> {
        val result = MutableLiveData<List<DocumentSnapshot>>()
        viewModelScope.launch {
            auth.currentUser?.email?.let {
                val data = fireStore.collection(it)
                    .whereEqualTo("id", movie?.id)
                    .get()
                    .await()
                    .documents

                result.postValue(data)
            }
        }
        return result
    }

    fun onAddFavMovieToFirestore(movie: Movie) {
        viewModelScope.launch {
            if (auth.currentUser?.email == null) {
                firebaseEventChannel.send(FirebaseEvent.NavigateToLoginFragment("Please Login First"))
            } else {
                movieRepository.addFavMovToFirestore(fireStore, auth.currentUser, movie)
                    .collect { result ->
                        when (result.status) {
                            SUCCESS -> {
                                firebaseEventChannel.send(
                                    FirebaseEvent.AddFavMovieToFirestore(
                                        true,
                                        "Add to Favourite"
                                    )
                                )
                            }
                            ERROR -> {
                                firebaseEventChannel.send(
                                    FirebaseEvent.AddFavMovieToFirestore(
                                        false,
                                        result.message
                                    )
                                )
                            }
                            LOADING -> Unit
                        }
                    }
            }
        }
    }

    fun onDeleteFavMovieFromFirestore(movie: Movie) {
        viewModelScope.launch {
            if (auth.currentUser?.email == null) {
                firebaseEventChannel.send(FirebaseEvent.NavigateToLoginFragment("Remove from Favourite"))
            } else {
                movieRepository.deleteFavMovieFromFirestore(fireStore, auth.currentUser, movie)
                    .collect { result ->
                        when (result.status) {
                            SUCCESS -> {
                                firebaseEventChannel.send(
                                    FirebaseEvent.DeleteFavMovieFromFirestore(
                                        true,
                                        "Add to Favourite"
                                    )
                                )
                            }
                            ERROR -> {
                                firebaseEventChannel.send(
                                    FirebaseEvent.DeleteFavMovieFromFirestore(
                                        false,
                                        result.message
                                    )
                                )
                            }
                            LOADING -> Unit
                        }
                    }
            }
        }
    }

    fun onGetFavMovieFromFirestore(): LiveData<ApiResult<List<Movie>>> {
        if (auth.currentUser?.email == null) {
            viewModelScope.launch {
                firebaseEventChannel.send(FirebaseEvent.NavigateToLoginFragment("Please Login First"))
            }
        }

        return movieRepository.getFavMovieFromFirestore(fireStore, auth.currentUser)
            .asLiveData(viewModelScope.coroutineContext)
    }

    fun onSignIn(email: String, password: String): LiveData<ApiResult<AuthResult>> {
        return movieRepository.signIn(auth, email, password).asLiveData(viewModelScope.coroutineContext)
    }

   fun onCreateNewUser(email: String, password: String): LiveData<ApiResult<AuthResult>> {
        return movieRepository.createNewUser(auth, email, password).asLiveData(viewModelScope.coroutineContext)
    }

    fun onFirebaseAuthWithGoogle(idToken: String) =
        movieRepository.firebaseAuthWithGoogle(auth, idToken).asLiveData()

}