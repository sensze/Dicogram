package com.ifkusyoba.dicogram.services.paging

import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.paging.LoadState
import androidx.paging.LoadStateAdapter
import androidx.recyclerview.widget.RecyclerView
import com.ifkusyoba.dicogram.databinding.ItemLoadingBinding

class PagingLoadingAdapter(private val retry: () -> Unit): LoadStateAdapter<PagingLoadingAdapter.PagingLoadingStateViewHolder>() {
    override fun onBindViewHolder(
        holder: PagingLoadingAdapter.PagingLoadingStateViewHolder,
        loadState: LoadState
    ) {
        holder.bind(loadState)
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        loadState: LoadState
    ): PagingLoadingAdapter.PagingLoadingStateViewHolder {
        val binding = ItemLoadingBinding.inflate(
            android.view.LayoutInflater.from(parent.context),
            parent,
            false
        )
        return PagingLoadingStateViewHolder(binding, retry)
    }
    class PagingLoadingStateViewHolder(private val binding: ItemLoadingBinding, retry: () -> Unit): RecyclerView.ViewHolder(binding.root){
        init {
            binding.btnRetry.setOnClickListener { retry.invoke() }
        }
        fun bind(loadState: LoadState) {
            if (loadState is LoadState.Error) {
                binding.messageError.text = loadState.error.localizedMessage
            }
            binding.messageError.isVisible = loadState is LoadState.Error
            binding.loading.root.isVisible = loadState is LoadState.Loading
            binding.btnRetry.isVisible = loadState is LoadState.Error
        }
    }
}