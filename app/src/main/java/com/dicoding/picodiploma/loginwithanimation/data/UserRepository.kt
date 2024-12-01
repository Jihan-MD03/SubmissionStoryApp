package com.dicoding.picodiploma.loginwithanimation.data

import com.dicoding.picodiploma.loginwithanimation.data.pref.UserModel
import com.dicoding.picodiploma.loginwithanimation.data.pref.UserPreference
import com.dicoding.picodiploma.loginwithanimation.data.remote.AuthApiService
import com.dicoding.picodiploma.loginwithanimation.data.remote.responses.ErrorResponse
import com.dicoding.picodiploma.loginwithanimation.data.remote.responses.LoginResult
import com.dicoding.picodiploma.loginwithanimation.data.remote.Result
import com.google.gson.Gson
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import retrofit2.HttpException

class UserRepository private constructor(
    private val userPreference: UserPreference,
    private val authApiService: AuthApiService // Menambahkan ApiService
) {

    // Fungsi untuk register
    suspend fun register(name: String, email: String, password: String): String {
        return try {
            val response = authApiService.register(name, email, password)
            response.message // Berhasil, ambil pesan sukses
        } catch (e: HttpException) {
            val errorBody = e.response()?.errorBody()?.string()
            val errorResponse = Gson().fromJson(errorBody, ErrorResponse::class.java)
            throw Exception(errorResponse?.message ?: "Gagal mendaftar, coba lagi nanti.")
        }
    }

    // Fungsi untuk login
    fun login(email: String, password: String): Flow<Result<LoginResult>> = flow {
        emit(Result.Loading)
        try {
            val response = authApiService.login(email, password)
            val result = response.loginResult
            saveToken(result.token)
            emit(Result.Success(result))
        } catch (e: HttpException) {
            emit(Result.Error(e.message()))
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
    companion object {
        @Volatile
        private var instance: UserRepository? = null
        fun getInstance(
            userPreference: UserPreference,
            authApiService: AuthApiService // Urutan parameter yang benar
        ): UserRepository =
            instance ?: synchronized(this) {
                instance ?: UserRepository(userPreference, authApiService)
            }.also { instance = it }
    }
}