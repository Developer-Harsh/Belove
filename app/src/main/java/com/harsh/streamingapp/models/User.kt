package com.harsh.streamingapp.models

import java.io.Serializable

data class User(
    val name: String? = "",
    val username: String? = "",
    val profile: String? = "",
    val email: String? = "",
    val password: String? = "",
    val location: String? = "",
    val gender: String? = "",
    val age: Long? = 18,
    val about: String? = "",
    val tokenFCM: String? = ""
): Serializable
