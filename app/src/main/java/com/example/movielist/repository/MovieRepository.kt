package com.example.movielist.repository

import androidx.paging.Pager
import androidx.paging.PagingConfig
import com.example.movielist.BuildConfig
import com.example.movielist.model.ApiResult
import com.example.movielist.model.Movie
import com.example.movielist.model.SortOrder
import com.example.movielist.model.SortOrder.*
import com.example.movielist.model.Video
import com.example.movielist.network.ApiService
import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.tasks.await
import java.lang.Exception
import javax.inject.Inject
import kotlin.coroutines.suspendCoroutine

class MovieRepository @Inject constructor(
    private val apiService: ApiService
) {

    companion object {
        private const val PAGE_SIZE = 50
    }

    fun getMovieListStream(sortOrder: SortOrder) =
        when (sortOrder) {
            BY_UPCOMING -> Pager(
                config = PagingConfig(PAGE_SIZE),
                pagingSourceFactory = { UpcomingMoviePagingSource(apiService) }
            ).flow

            BY_POPULAR -> Pager(
                config = PagingConfig(PAGE_SIZE),
                pagingSourceFactory = { PopularMoviePagingSource(apiService) }
            ).flow

            BY_TOP_RATED -> Pager(
                config = PagingConfig(PAGE_SIZE),
                pagingSourceFactory = { TopRatedMoviePagingSource(apiService) }
            ).flow
        }

    fun getSearchMovieListStream(query: String) =
        Pager(
            config = PagingConfig(PAGE_SIZE),
            pagingSourceFactory = { SearchPagingSource(apiService, query) }
        ).flow


    fun getFavMovieFromFirestore(
        firestore: FirebaseFirestore,
        user: FirebaseUser?
    ): Flow<ApiResult<List<Movie>>> {
        return flow {
            try {
                emit(ApiResult.Loading(true))

                user?.email?.let { email ->
                    val movieList = mutableListOf<Movie>()

                    val docList = firestore.collection(email).get().await().documents

                    for (doc in docList) {
                        val movie = doc.toObject(Movie::class.java)
                        movie?.let { movieList.add(it) }
                    }

                    emit(ApiResult.Success(movieList))
                }
            } catch (e: Exception) {
                emit(ApiResult.Error(e.toString()))
            }
        }.flowOn(Dispatchers.IO)
    }

    fun addFavMovToFirestore(
        firestore: FirebaseFirestore,
        user: FirebaseUser?,
        movie: Movie
    ): Flow<ApiResult<Boolean>> {
        return flow {
            try {
                emit(ApiResult.Loading(true))

                user?.email?.let { email ->
                    firestore.collection(email)
                        .document(movie.id.toString())
                        .set(movie)
                        .await()

                    emit(ApiResult.Success(true))
                }

            } catch (e: Exception) {
                emit(ApiResult.Error(e.toString()))
            }
        }.flowOn(Dispatchers.IO)
    }

    fun deleteFavMovieFromFirestore(
        firestore: FirebaseFirestore,
        user: FirebaseUser?,
        movie: Movie
    ): Flow<ApiResult<Boolean>> {
        return flow {
            try {
                emit(ApiResult.Loading(true))

                user?.email?.let { email ->
                    firestore.collection(email)
                        .document(movie.id.toString())
                        .delete()
                        .await()

                    emit(ApiResult.Success(true))
                }

            } catch (e: Exception) {
                emit(ApiResult.Error(e.toString()))
            }
        }.flowOn(Dispatchers.IO)
    }

    fun checkFavMovieInFirestore(
        firestore: FirebaseFirestore,
        user: FirebaseUser?,
        movie: Movie
    ): Flow<ApiResult<Boolean>> {
        return flow {
            try {
                emit(ApiResult.Loading(true))

                user?.email?.let {
                    val result = firestore.collection(it)
                        .whereEqualTo("id", movie.id)
                        .get()
                        .await()
                        .documents

                    if (result.isNotEmpty()) {
                        emit(ApiResult.Success(true))
                    } else {
                        emit(ApiResult.Success(false))
                    }
                }
            } catch (e: Exception) {
                emit(ApiResult.Error(e.toString()))
            }
        }.flowOn(Dispatchers.IO)
    }

    fun signIn(auth: FirebaseAuth, email: String, password: String): Flow<ApiResult<AuthResult>> {
        return flow {
            emit(ApiResult.Loading(true))
            try {
                val result = auth.signInWithEmailAndPassword(email, password)
                    .await()
                emit(ApiResult.Success(result))
            } catch (e: Exception) {
                emit(ApiResult.Error(e.toString()))
            }
        }
    }

    fun createNewUser(
        auth: FirebaseAuth,
        email: String,
        password: String
    ): Flow<ApiResult<AuthResult>> {
        return flow {
            emit(ApiResult.Loading(true))
            try {
                val result = auth.createUserWithEmailAndPassword(email, password)
                    .await()
                emit(ApiResult.Success(result))
            } catch (e: Exception) {
                emit(ApiResult.Error(e.toString()))
            }
        }
    }

    fun loadVideoList(movieId: Int) = flow {
        val call = apiService.fetchTrailer(movieId, BuildConfig.TMDB_API_KEY)

        if (call.isSuccessful) {
            call.body()?.let {
                emit(it.results)
            }
        }
    }

    fun firebaseAuthWithGoogle(auth: FirebaseAuth, idToken: String) = flow {
        emit(ApiResult.Loading(true))
        val credential = GoogleAuthProvider.getCredential(idToken, null)

        try{
            val result = auth.signInWithCredential(credential).await()
            emit(ApiResult.Success(result))
        }catch (e: Exception) {
            emit(ApiResult.Error(e.toString()))
        }
    }
}