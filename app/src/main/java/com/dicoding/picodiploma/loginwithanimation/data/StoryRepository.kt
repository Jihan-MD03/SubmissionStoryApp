package com.dicoding.picodiploma.loginwithanimation.data

import com.dicoding.picodiploma.loginwithanimation.data.pref.UserModel
import com.dicoding.picodiploma.loginwithanimation.data.pref.UserPreference
import com.dicoding.picodiploma.loginwithanimation.data.remote.ApiService
import com.dicoding.picodiploma.loginwithanimation.data.remote.responses.ListStoryItem
import com.dicoding.picodiploma.loginwithanimation.data.remote.responses.StoryResponse

    /*suspend fun getStories(token: String): List<ListStoryItem> {
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
    }*/
    class StoryRepository private constructor(private val apiService: ApiService) {

        suspend fun getStories(token: String): List<ListStoryItem> {
            return try {
                val response = apiService.getStories("Bearer $token")  // Menggunakan token untuk akses API
                if (response.isSuccessful) {
                    response.body() ?: emptyList() // Ambil langsung listStory dari body
                } else {
                    throw Exception("Failed to fetch stories: ${response.message()}")
                }
            } catch (e: Exception) {
                // Menangani error
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
