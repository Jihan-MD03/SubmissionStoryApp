package com.dicoding.picodiploma.loginwithanimation.view.story

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dicoding.picodiploma.loginwithanimation.data.StoryRepository
import com.dicoding.picodiploma.loginwithanimation.data.remote.responses.Story
import kotlinx.coroutines.launch

class StoryDetailViewModel(private val storyRepository: StoryRepository) : ViewModel() {
    private val _story = MutableLiveData<Story>()
    val story: LiveData<Story> get() = _story

    private val _error = MutableLiveData<String>()
    val error: LiveData<String> get() = _error

    fun getDetailStory(id: String) = viewModelScope.launch {
        try {
            val response = storyRepository.getDetailStory(id)
            _story.postValue(response)
        } catch (e: Exception) {
            _error.postValue("Failed to load stories: ${e.message}")
            Log.e("StoryViewModel", "Error fetching stories", e)
        }
    }
}