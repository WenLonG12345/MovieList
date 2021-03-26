package com.example.movielist.network

import com.example.movielist.model.MovieListResponse
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Query

interface ApiService {

    @GET("movie/upcoming")
    suspend fun getUpcomingMovies(
        @Query("api_key") apiKey: String,
        @Query("page") page: Int
    ) : MovieListResponse

    @GET("search/movie")
    suspend fun searchMovieByQuery(
        @Query("api_key") apiKey: String,
        @Query("query") query: String
    ) : MovieListResponse


    companion object{
        private const val BASE_URL = "https://api.themoviedb.org/3/"

        fun create(): ApiService {
            return Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build()
                .create(ApiService::class.java)
        }
    }
}