package com.dicoding.picodiploma.loginwithanimation.view

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.dicoding.picodiploma.loginwithanimation.data.StoryRepository
import com.dicoding.picodiploma.loginwithanimation.data.UserRepository
import com.dicoding.picodiploma.loginwithanimation.data.pref.UserModel
import com.dicoding.picodiploma.loginwithanimation.data.remote.Result
import com.dicoding.picodiploma.loginwithanimation.data.remote.responses.LoginResult
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

class AuthViewModel(private val userRepository: UserRepository) : ViewModel() {


    fun register(name: String, email: String, password: String, onResult: (Boolean, String) -> Unit) {
        viewModelScope.launch {
            try {
                val message = userRepository.register(name, email, password)
                onResult(true, message.toString()) // Tampilkan pesan sukses
            } catch (e: Exception) {
                onResult(false, e.message ?: "Registration failed") // Tampilkan pesan error
            }
        }
    }

    fun login(email: String, password: String): LiveData<Result<LoginResult>> {
        return userRepository.login(email, password).asLiveData()
    }

    // Fungsi untuk menyimpan token
    fun saveToken(token: String) {
        viewModelScope.launch {
            try {
                userRepository.saveToken(token)
            } catch (e: Exception) {
                // Handle error jika diperlukan
            }
        }
    }
    // Mengambil sesi pengguna yang disimpan (email, token, status login)
    fun getUserSession(onResult: (UserModel?) -> Unit) {
        viewModelScope.launch {
            val userSession = userRepository.getUserSession().first() // Mengambil nilai pertama
            onResult(userSession)
        }
    }
    fun saveSession(userModel: UserModel) = viewModelScope.launch {
        userRepository.saveSession(userModel)
    }
}


