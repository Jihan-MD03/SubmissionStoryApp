package com.dicoding.picodiploma.loginwithanimation.data.remote.responses

data class DetailResponse(
	val error: Boolean,
	val message: String,
	val story: Story
)

data class Story(
	val photoUrl: String,
	val createdAt: String,
	val name: String,
	val description: String,
	val lon: Any,
	val id: String,
	val lat: Any
)

