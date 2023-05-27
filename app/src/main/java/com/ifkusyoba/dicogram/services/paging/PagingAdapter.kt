package com.ifkusyoba.dicogram.services.paging

import android.app.Activity
import android.content.Intent
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.app.ActivityOptionsCompat
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.ifkusyoba.dicogram.databinding.RvStoryImageBinding
import com.ifkusyoba.dicogram.views.detailstory.DetailStoryActivity
import com.ifkusyoba.dicogram.models.Story
import androidx.core.util.Pair

class PagingAdapter : PagingDataAdapter<Story, PagingAdapter.StoryViewHolder>(DIFF_CALLBACK) {
    inner class StoryViewHolder(private val binding: RvStoryImageBinding) :
        RecyclerView.ViewHolder(binding.root) {
        internal fun bind(story: Story) {
            binding.tvStory.text = story.name
            Glide.with(itemView.context)
                .load(story.photoUrl)
                .into(binding.imgStory)

            // *Onclick
            binding.root.setOnClickListener { view ->
                // *Intent
                val intent = Intent(itemView.context, DetailStoryActivity::class.java).apply {
                    putExtra(DetailStoryActivity.EXTRA_DETAIL, story)
                }
                // *OptionsCompat
                val optionsCompat: ActivityOptionsCompat =
                    ActivityOptionsCompat.makeSceneTransitionAnimation(
                        itemView.context as Activity,
                        Pair(binding.tvStory, "name"),
                        /*Pair(binding.imgStory, "image")*/
                    )
                view.context.startActivity(intent, optionsCompat.toBundle())
            }
        }
    }

    override fun onBindViewHolder(holder: PagingAdapter.StoryViewHolder, position: Int) {
        val data = getItem(position)
        if (data != null) {
            holder.bind(data)
        }
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): PagingAdapter.StoryViewHolder {
        val view = RvStoryImageBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return StoryViewHolder(view)
    }

    companion object {
        val DIFF_CALLBACK = object : DiffUtil.ItemCallback<Story>() {
            override fun areItemsTheSame(
                oldItem: Story,
                newItem: Story
            ): Boolean {
                return oldItem == newItem
            }

            override fun areContentsTheSame(
                oldItem: Story,
                newItem: Story
            ): Boolean {
                return oldItem.id == newItem.id
            }
        }
    }
}