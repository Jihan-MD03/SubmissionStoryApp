package com.dicoding.picodiploma.loginwithanimation.data

import android.util.Log
import com.dicoding.picodiploma.loginwithanimation.data.remote.StoryApiService
import com.dicoding.picodiploma.loginwithanimation.data.remote.responses.AddNewStoryResponse
import com.dicoding.picodiploma.loginwithanimation.data.remote.responses.ListStoryItem
import com.dicoding.picodiploma.loginwithanimation.data.remote.responses.Story
import okhttp3.MultipartBody
import okhttp3.RequestBody


class StoryRepository private constructor(
    private val storyApiService: StoryApiService) {

    suspend fun uploadStory(description: RequestBody, photo: MultipartBody.Part): AddNewStoryResponse {
        return storyApiService.uploadStory(photo, description)
    }


    suspend fun getStories(): List<ListStoryItem> {
        return try {
            val storyResponse = storyApiService.getStories()
            storyResponse.listStory // Langsung akses `listStory`

        } catch (e: Exception) {
            Log.e("StoryRepository", "Error fetching stories", e)
            throw Exception("Error fetching stories: ${e.message}")
        }
    }

    suspend fun getDetailStory(id: String): Story {
        return try {
            val response = storyApiService.getStoryDetail(id)
            response.story
        } catch (e: Exception) {
            Log.e("StoryRepository", "Error fetching stories", e)
            throw Exception("Error fetching stories: ${e.message}")
        }
    }


    companion object {
        @Volatile
        private var instance: StoryRepository? = null

        fun getInstance(storyApiService: StoryApiService): StoryRepository =
            instance ?: synchronized(this) {
                instance ?: StoryRepository(storyApiService).also { instance = it }
            }
    }
}

