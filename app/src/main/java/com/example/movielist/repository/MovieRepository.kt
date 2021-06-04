package com.example.movielist.repository

import androidx.lifecycle.LiveData
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.liveData
import com.example.movielist.BuildConfig
import com.example.movielist.model.Movie
import com.example.movielist.network.ApiService
import kotlinx.coroutines.flow.Flow
import timber.log.Timber
import javax.inject.Inject

class MovieRepository @Inject constructor(
    private val apiService: ApiService
) {

    fun getMovieListStream(): LiveData<PagingData<Movie>> {
        return Pager(
            config = PagingConfig(PAGE_SIZE),
            pagingSourceFactory = { MoviePagingSource(apiService)}
        ).liveData
    }

    fun getSearchMovieListStream(query: String) =
        Pager(
            config = PagingConfig(PAGE_SIZE),
            pagingSourceFactory = { SearchPagingSource(apiService, query)}
        ).liveData

    companion object {
        private const val PAGE_SIZE = 50
    }

    suspend fun getUpcomingMovie() {
        val call = apiService.getUpcomingMovie(BuildConfig.TMDB_API_KEY, 1)
        if(call.isSuccessful) {
            Timber.i("MOVIE - ${call.body()?.results}")
        } else {
            Timber.i("LOAD ERROR")
        }
    }
}