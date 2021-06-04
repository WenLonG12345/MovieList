package com.example.movielist.model

sealed class FirebaseEvent{
    data class AddFavMovieToFirestore(val status: Boolean, val msg: String?): FirebaseEvent()
    data class DeleteFavMovieFromFirestore(val status: Boolean, val msg: String?): FirebaseEvent()
    data class NavigateToLoginFragment(val msg: String): FirebaseEvent()
    data class CheckIsFavMovie(val status: Boolean): FirebaseEvent()
}
