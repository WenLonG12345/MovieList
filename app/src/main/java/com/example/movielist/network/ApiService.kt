package com.example.movielist.network

import com.example.movielist.model.MovieResponse
import com.example.movielist.model.VideoListResponse
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface ApiService {

    @GET("movie/upcoming")
    suspend fun getUpcomingMovies(
        @Query("api_key") apiKey: String,
        @Query("page") page: Int
    ) : MovieResponse

    @GET("movie/popular")
    suspend fun getPopularMovies(
        @Query("api_key") apiKey: String,
        @Query("page") page: Int
    ) : MovieResponse

    @GET("movie/top_rated")
    suspend fun getTopRatedMovies(
        @Query("api_key") apiKey: String,
        @Query("page") page: Int
    ) : MovieResponse

    @GET("search/movie")
    suspend fun searchMovieByQuery(
        @Query("api_key") apiKey: String,
        @Query("query") query: String
    ) : MovieResponse

    @GET("movie/{movie_id}/videos")
    suspend fun fetchTrailer(
        @Path("movie_id") movieId: Int,
        @Query("api_key") apiKey: String
    ): Response<VideoListResponse>

}