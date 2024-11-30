package com.dicoding.picodiploma.loginwithanimation.view.story

import android.graphics.drawable.Drawable
import android.os.Bundle
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import com.bumptech.glide.Glide
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.target.Target
import com.dicoding.picodiploma.loginwithanimation.R
import com.dicoding.picodiploma.loginwithanimation.view.ViewModelFactory
import kotlinx.coroutines.launch

class StoryDetailActivity : AppCompatActivity() {

    private lateinit var storyImageView: ImageView
    private lateinit var storyNameTextView: TextView
    private lateinit var storyDescriptionTextView: TextView
    private lateinit var progressBar: View

    private lateinit var viewModel: StoryDetailViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_story_detail)

        // Aktifkan shared element transition
        supportPostponeEnterTransition()

        storyImageView = findViewById(R.id.story_image)
        storyNameTextView = findViewById(R.id.story_name)
        storyDescriptionTextView = findViewById(R.id.story_description)
        progressBar = findViewById(R.id.progressBar)

        // Inisialisasi ViewModel
        viewModel = ViewModelProvider(
            this,
            ViewModelFactory(application)
        )[StoryDetailViewModel::class.java]

        // Get story ID from intent
        val storyId = intent.getStringExtra("story_id")
        if (storyId.isNullOrEmpty()) {
            Toast.makeText(this, "Story ID is missing", Toast.LENGTH_SHORT).show()
            finish()
            return
        }

        progressBar.visibility = View.VISIBLE

        // Fetch story detail using ViewModel
        lifecycleScope.launch {
            viewModel.fetchStoryDetail(storyId)
        }

        // Observe story details
        viewModel.story.observe(this) { story ->
            progressBar.visibility = View.GONE

            Glide.with(this)
                .load(story.photoUrl)
                .listener(object : RequestListener<Drawable> {
                    override fun onLoadFailed(
                        e: GlideException?,
                        model: Any?,
                        target: Target<Drawable>?,
                        isFirstResource: Boolean
                    ): Boolean {
                        supportStartPostponedEnterTransition()
                        return false
                    }

                    override fun onResourceReady(
                        resource: Drawable?,
                        model: Any?,
                        target: Target<Drawable>?,
                        dataSource: DataSource?,
                        isFirstResource: Boolean
                    ): Boolean {
                        supportStartPostponedEnterTransition()
                        return false
                    }
                })
                .into(storyImageView)

            storyNameTextView.text = story.name
            storyDescriptionTextView.text = story.description
        }

        // Observe error message
        viewModel.error.observe(this) { message ->
            progressBar.visibility = View.GONE
            Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
        }
    }
}
