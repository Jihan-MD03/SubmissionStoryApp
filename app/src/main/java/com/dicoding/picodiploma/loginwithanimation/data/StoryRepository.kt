package com.dicoding.picodiploma.loginwithanimation.data

import android.util.Log
import com.dicoding.picodiploma.loginwithanimation.data.remote.ApiService
import com.dicoding.picodiploma.loginwithanimation.data.remote.responses.ListStoryItem
import com.dicoding.picodiploma.loginwithanimation.data.remote.responses.StoryResponse


    class StoryRepository private constructor(private val apiService: ApiService) {

        suspend fun getStories(token: String): List<ListStoryItem> {
            return try {
                Log.d("StoryRepository", "Token used: Bearer $token")
                val response = apiService.getStories("Bearer $token")  // Menggunakan token untuk akses API

                if (response.isSuccessful) {
                    val storyResponse = response.body() as StoryResponse
                    storyResponse.listStory ?: emptyList()
                } else {
                    throw Exception("Failed to fetch stories: ${response.message()}")
                }

            } catch (e: Exception) {
                // Menangani error
                Log.e("StoryRepository", "Error fetching stories", e)
                throw Exception("Error fetching stories: ${e.message}")
            }
        }

        companion object {
            @Volatile
            private var instance: StoryRepository? = null

            fun getInstance(apiService: ApiService): StoryRepository =
                instance ?: synchronized(this) {
                    instance ?: StoryRepository(apiService).also { instance = it }
                }
        }
    }
