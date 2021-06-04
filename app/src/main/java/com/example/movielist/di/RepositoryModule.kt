package com.example.movielist.di

import com.example.movielist.network.ApiService
import com.example.movielist.repository.MovieRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ViewModelComponent
import dagger.hilt.android.scopes.ViewModelScoped


@Module
@InstallIn(ViewModelComponent::class)
object RepositoryModule {

    @Provides
    @ViewModelScoped
    fun provideMovieRepository(
        apiService: ApiService
    ): MovieRepository = MovieRepository(apiService)
}