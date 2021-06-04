package com.example.movielist.model

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize

@Parcelize
data class Movie(
    @SerializedName("id") val id: Int = 0,
    @SerializedName("original_title") val original_title: String = "",
    @SerializedName("backdrop_path") val backdrop_path: String = "",
    @SerializedName("poster_path") val poster_path: String = "",
    @SerializedName("overview") val overview: String = "",
    @SerializedName("adult") val adult: Boolean = false,
    @SerializedName("release_date") val release_date: String = "",
    @SerializedName("original_language") val original_language: String = "",
    @SerializedName("popularity") val popularity: Double = 0.0,
    @SerializedName("vote_average") val vote_average: Double = 0.0,
    @SerializedName("vote_count") val vote_count: Int = 0
): Parcelable


data class MovieResponse(
    @SerializedName("page") val page: Int,
    @SerializedName("results") val results: List<Movie>,
    @SerializedName("total_pages") val total_pages: Int,
    @SerializedName("total_results") val total_results: Int
)