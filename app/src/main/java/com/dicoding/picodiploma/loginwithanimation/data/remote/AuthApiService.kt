package com.dicoding.picodiploma.loginwithanimation.data.remote

import com.dicoding.picodiploma.loginwithanimation.data.remote.responses.LoginResponse
import com.dicoding.picodiploma.loginwithanimation.data.remote.responses.RegisterResponse
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.POST

interface AuthApiService {
    @FormUrlEncoded
    @POST("register")
    suspend fun register(
        @Field("name") name: String,
        @Field("email") email: String,
        @Field("password") password: String
    ): RegisterResponse

    @FormUrlEncoded
    @POST("login")
    suspend fun login(
        @Field("email") email: String,
        @Field("password") password: String
    ): LoginResponse
}