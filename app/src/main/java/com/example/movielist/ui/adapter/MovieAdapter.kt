package com.example.movielist.ui.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import coil.load
import com.example.movielist.databinding.MovieItemBinding
import com.example.movielist.model.Movie
import com.example.movielist.utils.Constants.IMAGE_DOMAIN

class MovieAdapter(
    val onClick: (Movie) -> Unit
): PagingDataAdapter<Movie, MovieAdapter.MovieVH>(DiffUtils) {

    inner class MovieVH(private val binding: MovieItemBinding): RecyclerView.ViewHolder(binding.root) {
        fun bind(movie: Movie?) {
            with(binding) {
                movie?.let{ movie ->
                    tvMoviesTitle.text = movie.original_title
                    ivMoviesPoster.load(IMAGE_DOMAIN + movie.backdrop_path)

                    rlMovie.setOnClickListener {
                        onClick(movie)
                    }
                }

            }
        }
    }

    override fun onBindViewHolder(holder: MovieVH, position: Int) {
        holder.bind(getItem(position))
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MovieVH {
        val binding = MovieItemBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return MovieVH(binding)
    }

    companion object{
        private val DiffUtils = object: DiffUtil.ItemCallback<Movie>() {
            override fun areItemsTheSame(oldItem: Movie, newItem: Movie): Boolean {
                return oldItem.id == newItem.id
            }

            override fun areContentsTheSame(oldItem: Movie, newItem: Movie): Boolean {
                return oldItem == newItem
            }
        }
    }
}