package com.dicoding.picodiploma.loginwithanimation.data.remote

import com.dicoding.picodiploma.loginwithanimation.data.remote.responses.AddNewStoryResponse
import com.dicoding.picodiploma.loginwithanimation.data.remote.responses.DetailResponse
import com.dicoding.picodiploma.loginwithanimation.data.remote.responses.StoryResponse
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.http.GET
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part
import retrofit2.http.Path

interface StoryApiService {
    @GET("stories")
    suspend fun getStories(): StoryResponse

    // Endpoint untuk mengunggah story
    @Multipart
    @POST("stories")
    suspend fun uploadStory(
        @Part file: MultipartBody.Part,
        @Part("description") description: RequestBody,
    ): AddNewStoryResponse

    @GET("stories/{id}")
    suspend fun getStoryDetail(
        @Path("id") id: String
    ) : DetailResponse
}