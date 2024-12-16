package com.dicoding.picodiploma.loginwithanimation.view.story

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import com.dicoding.picodiploma.loginwithanimation.data.StoryRepository
import com.dicoding.picodiploma.loginwithanimation.data.remote.responses.AddNewStoryResponse
import com.dicoding.picodiploma.loginwithanimation.data.remote.responses.ListStoryItem
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch
import okhttp3.MultipartBody
import okhttp3.RequestBody

class StoryViewModel(private val storyRepository: StoryRepository) : ViewModel() {


    private val _error = MutableLiveData<String>()
    val error: LiveData<String> get() = _error

    private val _uploadSuccess = MutableLiveData<AddNewStoryResponse>()
    val uploadSuccess: LiveData<AddNewStoryResponse> get() = _uploadSuccess

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> get() = _isLoading

    val pagedStories: Flow<PagingData<ListStoryItem>> = storyRepository.getPagedStories()

    private val _storiesWithLocation = MutableLiveData<List<ListStoryItem>>()
    val storiesWithLocation: LiveData<List<ListStoryItem>> get() = _storiesWithLocation

    private val _uploadStoryResponse = MutableLiveData<Result<String>>()
    val uploadStoryResponse: LiveData<Result<String>> get() = _uploadStoryResponse

    fun fetchStoriesWithLocation() {
        viewModelScope.launch {
            try {
                val response = storyRepository.getStoriesWithLocation()
                _storiesWithLocation.postValue(response.listStory)
            } catch (e: Exception) {
                Log.e("StoryViewModel", "Error fetching stories with location", e)
            }
        }
    }

    fun uploadStory(photo: MultipartBody.Part, description: RequestBody) {
        viewModelScope.launch {
            try {

                _isLoading.postValue(true)
                val response = storyRepository.uploadStory(description, photo)
                _uploadSuccess.postValue(response)
            } catch (e: Exception) {
                _error.postValue("Gagal mengunggah cerita: ${e.message}")
            } finally {
                _isLoading.postValue(false)
            }
        }
    }

    fun logout() = viewModelScope.launch {
        try {
            storyRepository.logout()
        } catch (e: Exception) {
            _error.postValue("Logout error: ${e.message}")
        }
    }
}

