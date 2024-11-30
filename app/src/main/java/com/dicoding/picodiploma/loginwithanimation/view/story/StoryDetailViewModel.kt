package com.dicoding.picodiploma.loginwithanimation.view.story

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.dicoding.picodiploma.loginwithanimation.data.StoryRepository
import com.dicoding.picodiploma.loginwithanimation.data.remote.responses.Story

class StoryDetailViewModel(private val storyRepository: StoryRepository) : ViewModel() {
    private val _story = MutableLiveData<Story>()
    val story: LiveData<Story> get() = _story

    private val _error = MutableLiveData<String>()
    val error: LiveData<String> get() = _error

    suspend fun fetchStoryDetail(storyId: String) {
        // Logika untuk mengambil detail story berdasarkan storyId
        try {
            // Misalnya, ambil detail story dari repository
            val storyDetail = storyRepository.getStoryDetail(storyId)
            _story.value = storyDetail!!
        } catch (e: Exception) {
            _error.value = "Error: ${e.message}"
        }
    }
}