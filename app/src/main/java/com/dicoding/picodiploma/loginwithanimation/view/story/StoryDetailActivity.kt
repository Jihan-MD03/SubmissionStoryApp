package com.dicoding.picodiploma.loginwithanimation.view.story

import android.os.Bundle
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.dicoding.picodiploma.loginwithanimation.R
import com.dicoding.picodiploma.loginwithanimation.data.remote.ApiService
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class StoryDetailActivity : AppCompatActivity() {

    private lateinit var storyImageView: ImageView
    private lateinit var storyNameTextView: TextView
    private lateinit var storyDescriptionTextView: TextView

    private lateinit var apiService: ApiService

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_story_detail)

        storyImageView = findViewById(R.id.story_image)
        storyNameTextView = findViewById(R.id.story_name)
        storyDescriptionTextView = findViewById(R.id.story_description)

        // Ambil ID story dari intent
        val storyId = intent.getStringExtra("story_id") ?: return

        // Inisialisasi API Service
        val retrofit = Retrofit.Builder()
            .baseUrl("https://story-api.dicoding.dev/v1/stories/{id}/") // Ganti dengan URL API Anda
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        apiService = retrofit.create(ApiService::class.java)

        // Panggil API untuk mendapatkan detail story
        fetchStoryDetail(storyId)
    }

    private fun fetchStoryDetail(storyId: String) {
        val token = "Bearer ${getTokenFromSharedPreferences()}" // Ganti dengan fungsi pengambil token
        CoroutineScope(Dispatchers.IO).launch {
            try {

                val response = apiService.getStoryDetail(token, storyId)
                withContext(Dispatchers.Main) {
                    if (!response.error) {
                        val story = response.story
                        Glide.with(this@StoryDetailActivity).load(story.photoUrl).into(storyImageView)
                        storyNameTextView.text = story.name
                        storyDescriptionTextView.text = story.description
                    } else {
                        Toast.makeText(this@StoryDetailActivity, "Failed to load story detail", Toast.LENGTH_SHORT).show()
                    }
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    Toast.makeText(this@StoryDetailActivity, "Error: ${e.localizedMessage}", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    private fun getTokenFromSharedPreferences(): String {
        val sharedPreferences = getSharedPreferences("user_session", MODE_PRIVATE)
        return sharedPreferences.getString("token", "") ?: ""
    }
}
