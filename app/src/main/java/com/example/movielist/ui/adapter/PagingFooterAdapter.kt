package com.example.movielist.ui.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.paging.LoadState
import androidx.paging.LoadStateAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.movielist.databinding.PagingFooterItemBinding

class PagingFooterAdapter(
    val retry: () -> Unit
): LoadStateAdapter<PagingFooterAdapter.PagingFooterVH>() {

    class PagingFooterVH(val binding: PagingFooterItemBinding)
        : RecyclerView.ViewHolder(binding.root) {}

    override fun onBindViewHolder(holder: PagingFooterVH, loadState: LoadState) {
        holder.binding.progressBar.isVisible = loadState is LoadState.Loading
        holder.binding.retryButton.isVisible = loadState is LoadState.Error
        holder.binding.retryButton.setOnClickListener {
            retry()
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, loadState: LoadState): PagingFooterVH {
        val binding = PagingFooterItemBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return PagingFooterVH(binding)
    }
}