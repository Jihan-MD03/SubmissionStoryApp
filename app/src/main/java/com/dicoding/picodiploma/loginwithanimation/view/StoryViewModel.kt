package com.dicoding.picodiploma.loginwithanimation.view

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dicoding.picodiploma.loginwithanimation.data.StoryRepository
import com.dicoding.picodiploma.loginwithanimation.data.remote.responses.AddNewStoryResponse
import com.dicoding.picodiploma.loginwithanimation.data.remote.responses.ListStoryItem
import kotlinx.coroutines.launch
import okhttp3.MultipartBody
import okhttp3.RequestBody

class StoryViewModel(private val storyRepository: StoryRepository) : ViewModel() {

    private val _stories = MutableLiveData<List<ListStoryItem>>()
    val stories: LiveData<List<ListStoryItem>> get() = _stories

    private val _error = MutableLiveData<String>()
    val error: LiveData<String> get() = _error

    private val _uploadSuccess = MutableLiveData<AddNewStoryResponse>()
    val uploadSuccess: LiveData<AddNewStoryResponse> get() = _uploadSuccess


    fun getStories() {
        viewModelScope.launch {
            try {
                val response = storyRepository.getStories()
                _stories.postValue(response)
            } catch (e: Exception) {
                _error.postValue("Failed to load stories: ${e.message}")
                // Tambahkan log di sini
                Log.e("StoryViewModel", "Error fetching stories", e)
            }
        }
    }

    fun uploadStory(photo: MultipartBody.Part, description: RequestBody) {
        viewModelScope.launch {
            try {
                val response = storyRepository.uploadStory(description, photo)
                _uploadSuccess.postValue(response)
            } catch (e: Exception) {
                _error.postValue("Gagal mengunggah cerita: ${e.message}")
            }
        }
    }

}

