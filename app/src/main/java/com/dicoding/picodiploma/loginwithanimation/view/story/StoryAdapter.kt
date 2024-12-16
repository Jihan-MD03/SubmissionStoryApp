package com.dicoding.picodiploma.loginwithanimation.view.story

import android.app.Activity
import android.content.Intent
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.dicoding.picodiploma.loginwithanimation.data.remote.responses.ListStoryItem
import com.dicoding.picodiploma.loginwithanimation.databinding.ItemStoryBinding


class StoryAdapter : PagingDataAdapter<ListStoryItem, StoryAdapter.StoryViewHolder>(StoryDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): StoryViewHolder {
        val binding = ItemStoryBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return StoryViewHolder(binding)
    }

    override fun onBindViewHolder(holder: StoryViewHolder, position: Int) {
        val story = getItem(position)
        if (story != null) {
            holder.bind(story)
        }
    }

    inner class StoryViewHolder(private val binding: ItemStoryBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(story: ListStoryItem) {
            binding.storyTitle.text = story.name
            binding.storyDescription.text = story.description

            Glide.with(binding.root.context)
                .load(story.photoUrl)
                .into(binding.ivStoryLogo)

            binding.root.setOnClickListener {
                val intent = Intent(binding.root.context, StoryDetailActivity::class.java)
                intent.putExtra("story_id", story.id)
                binding.root.context.startActivity(intent)
            }
        }
    }

    class StoryDiffCallback : DiffUtil.ItemCallback<ListStoryItem>() {
        override fun areItemsTheSame(oldItem: ListStoryItem, newItem: ListStoryItem): Boolean =
            oldItem.id == newItem.id

        override fun areContentsTheSame(oldItem: ListStoryItem, newItem: ListStoryItem): Boolean =
            oldItem == newItem
    }
}


