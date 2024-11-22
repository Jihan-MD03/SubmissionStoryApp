package com.dicoding.picodiploma.loginwithanimation.di

import android.content.Context
import com.dicoding.picodiploma.loginwithanimation.data.UserRepository
import com.dicoding.picodiploma.loginwithanimation.data.pref.UserPreference
import com.dicoding.picodiploma.loginwithanimation.data.pref.dataStore
import com.dicoding.picodiploma.loginwithanimation.data.remote.ApiConfig
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking

fun provideRepository(context: Context): UserRepository { // Ganti StoryRepository dengan UserRepository
    val pref = UserPreference.getInstance(context.dataStore)
    val user = runBlocking { pref.getSession().first() }
    val apiService = ApiConfig.getApiService(user.token)
    return UserRepository.getInstance(pref, apiService) // Gunakan UserRepository
}

    /*fun provideUserRepository(context: Context): UserRepository {
        val pref = UserPreference.getInstance(context.dataStore) // UserPreference
        val apiService = ApiConfig.getApiService("token")
        return UserRepository.getInstance(pref, apiService) // Memastikan urutan parameter benar
    }

    fun provideRepository(context: Context):UserRepository {
        val database = UserDatabase.getInstance(context)
        val dao = database.userDao()
        return UserRepository.getInstance(dao)
    }

    fun provideStoryRepository(context: Context): StoryRepository {
        val pref = UserPreference.getInstance(context.dataStore)
        val user = runBlocking { pref.getSession().first() }
        val apiService = ApiConfig.getApiService(user.token) // Menggunakan token dari sesi pengguna
        return StoryRepository.getInstance(apiService)
    }
}*/

