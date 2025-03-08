package com.harsh.streamingapp.network

import com.harsh.streamingapp.models.Notification
import com.harsh.streamingapp.models.Room
import com.harsh.streamingapp.models.User
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Headers
import retrofit2.http.PATCH
import retrofit2.http.POST
import retrofit2.http.Path

interface ApiService {
    @POST("profiles/register")
    fun register(@Body profile: User): Call<RegisterResponse>

    @POST("profiles/login")
    fun login(@Body credentials: Map<String, String>): Call<LoginResponse>

    @GET("profiles")
    fun getProfiles(): Call<List<User>>

    @GET("profiles/{username}")
    fun getProfile(@Path("username") username: String): Call<User>

    @GET("rooms")
    fun getRooms(): Call<List<Room>>

    @POST("rooms/{roomId}")
    fun updateRoom(@Path("roomId") roomId: String, @Body room: Room): Call<Room>

    @POST("/v1/projects/videortc-d83ba/messages:send")
    @Headers(
        "Content-Type: application/json",
        "Accept: application/json"
    )
    fun sendRemoteMessage(
        @Body message: Notification,
        @Header("Authorization") accessToken: String = "Bearer ${FirebaseAccessToken.getAccessToken()}",
    ): Call<Notification>

    @PATCH("profiles/{username}")
    fun updateProfile(
        @Path("username") username: String,
        @Body updateData: Map<String, String>
    ): Call<ProfileResponse>
}