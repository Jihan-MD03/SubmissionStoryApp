package com.dicoding.picodiploma.loginwithanimation.data.pref

data class UserModel(
    val email: String,
    val token: String,
    val password: String, // Tambahkan properti password di sini
    val isLogin: Boolean = false
)