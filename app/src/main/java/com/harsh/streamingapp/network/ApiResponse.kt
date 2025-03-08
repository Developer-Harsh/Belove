package com.harsh.streamingapp.network

import com.harsh.streamingapp.models.User

data class LoginResponse(
    val message: String,
    val token: String,
    val user: User,
)

data class RegisterResponse(val message: String)

data class ProfileResponse(
    val message: String,
    val updatedProfile: UpdatedProfile?
)

data class UpdatedProfile(
    val name: String?,
    val username: String,
    val profile: String?,
    val email: String?,
    val location: String?,
    val gender: String?,
    val age: Int?,
    val about: String?
)