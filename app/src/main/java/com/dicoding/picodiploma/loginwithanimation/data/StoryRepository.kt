package com.dicoding.picodiploma.loginwithanimation.data

import android.util.Log
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import com.dicoding.picodiploma.loginwithanimation.data.paging.StoryPagingSource
import com.dicoding.picodiploma.loginwithanimation.data.pref.UserPreference
import com.dicoding.picodiploma.loginwithanimation.data.remote.StoryApiService
import com.dicoding.picodiploma.loginwithanimation.data.remote.responses.AddNewStoryResponse
import com.dicoding.picodiploma.loginwithanimation.data.remote.responses.ListStoryItem
import com.dicoding.picodiploma.loginwithanimation.data.remote.responses.Story
import com.dicoding.picodiploma.loginwithanimation.data.remote.responses.StoryResponse
import kotlinx.coroutines.flow.Flow
import okhttp3.MultipartBody
import okhttp3.RequestBody


class StoryRepository private constructor(
    private val storyApiService: StoryApiService,
    private val userPreference: UserPreference,
) {
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

    fun getPagedStories(): Flow<PagingData<ListStoryItem>> {
        return Pager(
            config = PagingConfig(
                pageSize = 20, // Ukuran data per halaman
                enablePlaceholders = false
            ),
            pagingSourceFactory = { StoryPagingSource(storyApiService) }
        ).flow
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

    suspend fun getStoriesWithLocation(): StoryResponse {
        return storyApiService.getStoriesWithLocation()
    }

    // Untuk Logout
    suspend fun logout() {
        userPreference.logout()
    }


    companion object {

        fun getInstance(storyApiService: StoryApiService,
                        userPreference: UserPreference):
                StoryRepository = StoryRepository(storyApiService, userPreference)
            }
    }

