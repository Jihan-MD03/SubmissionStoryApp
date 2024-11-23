package com.dicoding.picodiploma.loginwithanimation.data

import com.dicoding.picodiploma.loginwithanimation.data.pref.UserModel
import com.dicoding.picodiploma.loginwithanimation.data.pref.UserPreference
import com.dicoding.picodiploma.loginwithanimation.data.remote.ApiService
import com.dicoding.picodiploma.loginwithanimation.data.remote.responses.ErrorResponse
import com.dicoding.picodiploma.loginwithanimation.data.remote.responses.ListStoryItem
import com.google.gson.Gson
import kotlinx.coroutines.flow.Flow
import retrofit2.HttpException

class UserRepository private constructor(
    private val userPreference: UserPreference,
    private val apiService: ApiService // Menambahkan ApiService
) {

    // Fungsi untuk register
    suspend fun register(name: String, email: String, password: String): String {
        return try {
            val response = apiService.register(name, email, password)
            response.message // Berhasil, ambil pesan sukses
        } catch (e: HttpException) {
            val errorBody = e.response()?.errorBody()?.string()
            val errorResponse = Gson().fromJson(errorBody, ErrorResponse::class.java)
            throw Exception(errorResponse?.message ?: "Unknown error occurred") // Lempar pesan error
        }
    }

    // Fungsi untuk login
    suspend fun login(email: String, password: String): String {
        return try {
            val response = apiService.login(email, password)
            response.loginResult.token // Berhasil, ambil token
        } catch (e: HttpException) {
            val errorBody = e.response()?.errorBody()?.string()
            val errorResponse = Gson().fromJson(errorBody, ErrorResponse::class.java)
            throw Exception(errorResponse?.message ?: "Unknown error occurred") // Lempar pesan error
        }
    }

    // Fungsi untuk menyimpan token ke dalam DataStore
    suspend fun saveToken(token: String) {
        userPreference.saveToken(token)
    }

    // Untuk menyimpan sesi pengguna
    suspend fun saveSession(user: UserModel) {
        userPreference.saveSession(user)
    }

    fun getUserSession(): Flow<UserModel> {
        return userPreference.getSession()
    }


    // Untuk mendapatkan sesi
    fun getSession(): Flow<UserModel> {
        return userPreference.getSession()
    }

    // Untuk Logout
    suspend fun logout() {
        userPreference.logout()
    }

    suspend fun getStories(token: String): List<ListStoryItem> {
        return try {
            val response = apiService.getStories("Bearer $token") // Memanggil API
            if (response.isSuccessful) {
                response.body() ?: emptyList() // Respons langsung berupa List<ListStoryItem>
            } else {
                throw Exception("Failed to fetch stories: ${response.message()}")
            }
        } catch (e: Exception) {
            throw Exception("Error fetching stories: ${e.message}")
        }
    }


    companion object {
        @Volatile
        private var instance: UserRepository? = null
        fun getInstance(
            userPreference: UserPreference,
            apiService: ApiService // Urutan parameter yang benar
        ): UserRepository =
            instance ?: synchronized(this) {
                instance ?: UserRepository(userPreference, apiService)
            }.also { instance = it }
    }
}