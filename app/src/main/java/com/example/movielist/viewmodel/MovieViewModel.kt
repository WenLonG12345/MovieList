package com.example.movielist.viewmodel

import androidx.hilt.Assisted
import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.*
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.example.movielist.model.Movie
import com.example.movielist.repository.MovieRepository
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.QuerySnapshot
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

@HiltViewModel
class MovieViewModel @Inject constructor(
    private val movieRepository: MovieRepository
) : ViewModel(){

    var auth: FirebaseAuth = FirebaseAuth.getInstance()
    var db = FirebaseFirestore.getInstance()

    private val searchQuery = MutableLiveData("")

    val movies: LiveData<PagingData<Movie>> =
        searchQuery.switchMap { query ->
            if(query.isNotEmpty()) {
                movieRepository.getSearchMovieListStream(query).cachedIn(viewModelScope)
            } else {
                movieRepository.getMovieListStream().cachedIn(viewModelScope)
            }
        }

    fun setSearchQuery(query: String) {
        searchQuery.value = query
    }


    fun getMovieFromFirestore(movie: Movie?): LiveData<List<DocumentSnapshot>> {
        val result = MutableLiveData<List<DocumentSnapshot>>()
        viewModelScope.launch {
            auth.currentUser?.email?.let {
                val data = db.collection(it)
                    .whereEqualTo("id", movie?.id)
                    .get()
                    .await()
                    .documents

                result.postValue(data)
            }
        }
        return result
    }
}