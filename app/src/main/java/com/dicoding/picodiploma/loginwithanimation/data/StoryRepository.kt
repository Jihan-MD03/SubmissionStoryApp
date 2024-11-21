package com.dicoding.picodiploma.loginwithanimation.data

import com.dicoding.picodiploma.loginwithanimation.data.remote.ApiService
import com.dicoding.picodiploma.loginwithanimation.data.remote.responses.ListStoryItem

class StoryRepository private constructor(private val apiService: ApiService) {

    suspend fun getStories(token: String): List<ListStoryItem> {
        return try {
            val response = apiService.getStories("Bearer $token") // Menggunakan token untuk akses API
            response.listStory // Asumsikan API mengembalikan data berupa listStory
        } catch (e: Exception) {
            throw Exception("Error fetching stories")
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
