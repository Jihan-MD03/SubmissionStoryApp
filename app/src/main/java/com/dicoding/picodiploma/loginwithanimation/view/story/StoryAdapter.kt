package com.dicoding.picodiploma.loginwithanimation.view.story

import android.app.Activity
import android.content.Intent
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.dicoding.picodiploma.loginwithanimation.data.remote.responses.ListStoryItem
import com.dicoding.picodiploma.loginwithanimation.databinding.ItemStoryBinding


class StoryAdapter : ListAdapter<ListStoryItem, StoryAdapter.StoryViewHolder>(StoryDiffCallback()) {


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): StoryViewHolder {
        val binding = ItemStoryBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return StoryViewHolder(binding)
    }

    override fun onBindViewHolder(holder: StoryViewHolder, position: Int) {
        val story = getItem(position)
        holder.bind(story)
    }

    override fun getItemCount(): Int = currentList.size

    inner class StoryViewHolder(private val binding: ItemStoryBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(story: ListStoryItem) {
            binding.storyTitle.text = story.name
            binding.storyDescription.text = story.description

            // Memuat gambar menggunakan Glide
            Glide.with(binding.root.context)
                .load(story.photoUrl)
                .into(binding.ivStoryLogo)

            // Menambahkan klik pada item story
            binding.root.setOnClickListener {
                // Mengirim ID story ke StoryDetailActivity
                val intent = Intent(binding.root.context, StoryDetailActivity::class.java)
                intent.putExtra("story_id", story.id) // Mengirimkan ID story

                // Membuat animasi shared element transition
                val options = androidx.core.app.ActivityOptionsCompat.makeSceneTransitionAnimation(
                    binding.root.context as Activity,
                    binding.ivStoryLogo,
                    "story_image_transition"
                )
                // Memulai activity dengan animasi
                binding.root.context.startActivity(intent, options.toBundle())
            }
        }
    }

    class StoryDiffCallback : DiffUtil.ItemCallback<ListStoryItem>() {
        override fun areItemsTheSame(oldItem: ListStoryItem, newItem: ListStoryItem): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: ListStoryItem, newItem: ListStoryItem): Boolean {
            return oldItem == newItem
        }
    }
}

