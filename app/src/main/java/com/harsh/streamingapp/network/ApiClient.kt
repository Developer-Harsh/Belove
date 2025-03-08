package com.harsh.streamingapp.network

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object ApiClient {
    const val BASE_URL = "http://192.168.31.211:5000/"
    private const val FCM_URL = "https://fcm.googleapis.com"

    val instance: ApiService by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(ApiService::class.java)
    }

    val fcmClient : ApiService by lazy {
        Retrofit.Builder()
            .baseUrl(FCM_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(ApiService::class.java)
    }
}