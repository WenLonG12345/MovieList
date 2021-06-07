package com.example.movielist.ui.adapter

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import coil.load
import com.example.movielist.databinding.ItemVideoBinding
import com.example.movielist.model.Video
import com.example.movielist.utils.Constants.getYoutubeThumbnailPath
import com.example.movielist.utils.Constants.getYoutubeVideoPath
import timber.log.Timber

class VideoListAdapter: ListAdapter<Video, VideoListAdapter.VideoVH>(VideoDiffUtil) {

    inner class VideoVH(val binding: ItemVideoBinding): RecyclerView.ViewHolder(binding.root) {
        fun bind(video: Video) {
            with(binding) {
                ivVideoCover.load(getYoutubeThumbnailPath(video.key))

                cvVideo.setOnClickListener {
                    val playVideoIntent = Intent(Intent.ACTION_VIEW, Uri.parse(getYoutubeVideoPath(video.key)))
                    it.context.startActivity(playVideoIntent)
                }
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VideoVH {
        val binding = ItemVideoBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return VideoVH(binding)
    }

    override fun onBindViewHolder(holder: VideoVH, position: Int) {
        holder.bind(getItem(position))
    }

    companion object{
        private val VideoDiffUtil = object: DiffUtil.ItemCallback<Video>() {
            override fun areItemsTheSame(oldItem: Video, newItem: Video): Boolean {
                return oldItem.id == newItem.id
            }

            override fun areContentsTheSame(oldItem: Video, newItem: Video): Boolean {
                return oldItem == newItem
            }
        }
    }
}