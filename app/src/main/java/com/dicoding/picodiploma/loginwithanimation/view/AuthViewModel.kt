package com.dicoding.picodiploma.loginwithanimation.view

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dicoding.picodiploma.loginwithanimation.data.UserRepository
import com.dicoding.picodiploma.loginwithanimation.data.pref.UserModel
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

class AuthViewModel(private val userRepository: UserRepository) : ViewModel() {

    fun register(name: String, email: String, password: String, onResult: (Boolean, String) -> Unit) {
        viewModelScope.launch {
            try {
                val message = userRepository.register(name, email, password)
                onResult(true, message) // Tampilkan pesan sukses
            } catch (e: Exception) {
                onResult(false, e.message ?: "Registration failed") // Tampilkan pesan error
            }
        }
    }

    fun login(email: String, password: String, onResult: (Boolean, String) -> Unit) {
        viewModelScope.launch {
            try {
                val token = userRepository.login(email, password)
                onResult(true, "Login successful! Token: $token")
            } catch (e: Exception) {
                onResult(false, e.message ?: "Login failed") // Tampilkan pesan error
            }
        }
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

}

