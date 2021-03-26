package com.example.movielist.repository

import androidx.lifecycle.LiveData
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.liveData
import com.example.movielist.model.Movie
import com.example.movielist.network.ApiService
import kotlinx.coroutines.flow.Flow

object MovieRepository {

    private const val PAGE_SIZE = 50

    private val apiService = ApiService.create()

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
}