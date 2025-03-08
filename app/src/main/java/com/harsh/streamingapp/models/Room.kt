package com.harsh.streamingapp.models

import java.io.Serializable

data class Room(
    val roomId: String? = "",
    val userId: String? = "",
    val counts: Int? = 0
): Serializable
