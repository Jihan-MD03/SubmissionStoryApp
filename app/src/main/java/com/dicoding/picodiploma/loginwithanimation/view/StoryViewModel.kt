package com.dicoding.picodiploma.loginwithanimation.view

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dicoding.picodiploma.loginwithanimation.data.UserRepository
import com.dicoding.picodiploma.loginwithanimation.data.remote.responses.ListStoryItem
import kotlinx.coroutines.launch

class StoryViewModel(private val userRepository: UserRepository) : ViewModel() {

    private val _stories = MutableLiveData<List<ListStoryItem>>()
    val stories: LiveData<List<ListStoryItem>> get() = _stories

    private val _error = MutableLiveData<String>()
    val error: LiveData<String> get() = _error

    fun getStories(token: String) {
        viewModelScope.launch {
            try {
                val data = userRepository.getStories(token)
                _stories.postValue(data)
            } catch (e: Exception) {
                _error.postValue(e.message)
            }
        }
    }
}
