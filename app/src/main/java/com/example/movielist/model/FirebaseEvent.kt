package com.example.movielist.model

sealed class FirebaseEvent{
    data class AddMovieToFirestore(val msg: String): FirebaseEvent()
}
