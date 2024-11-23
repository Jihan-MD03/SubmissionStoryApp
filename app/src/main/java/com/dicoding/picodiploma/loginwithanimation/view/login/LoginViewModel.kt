package com.dicoding.picodiploma.loginwithanimation.view.login

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dicoding.picodiploma.loginwithanimation.data.StoryRepository
import com.dicoding.picodiploma.loginwithanimation.data.UserRepository
import com.dicoding.picodiploma.loginwithanimation.data.pref.UserModel
import kotlinx.coroutines.launch

class LoginViewModel(private val userRepository: UserRepository) : ViewModel() {

    // Fungsi untuk login
    fun login(email: String, password: String) {
        viewModelScope.launch {
            try {
                // Memanggil fungsi login dari UserRepository
                val token = userRepository.login(email, password)

                // Jika login berhasil dan token tidak kosong
                if (!token.isNullOrEmpty()) {
                    // Menyimpan token ke dalam preference
                    userRepository.saveToken(token)

                    // Membuat objek UserModel dan menyimpan sesi pengguna
                    val userModel = UserModel(email = email, token, password)
                    saveSession(userModel)
                }

            } catch (e: Exception) {
                // Menangani error jika login gagal
                // Anda bisa menampilkan pesan kesalahan di sini
            }
        }
    }

    // Fungsi untuk menyimpan sesi pengguna
    private fun saveSession(user: UserModel) {
        viewModelScope.launch {
            userRepository.saveSession(user) // Simpan sesi pengguna
        }
    }
}

