package com.harsh.streamingapp.network

import android.app.Activity
import android.content.Intent
import android.os.Handler
import android.widget.Toast
import com.harsh.streamingapp.models.User
import com.harsh.streamingapp.ui.MainActivity
import com.sneva.easyprefs.EasyPrefs
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class ApiHandler(val activity: Activity) {
    fun registerUser(
        name: String,
        username: String,
        profile: String,
        email: String,
        password: String,
        location: String,
        gender: String,
        age: Long,
        about: String,
    ) {
        val user = User(name, username, profile, email, password, location, gender, age, about)

        ApiClient.instance.register(user).enqueue(object : Callback<RegisterResponse> {
            override fun onResponse(
                call: Call<RegisterResponse>,
                response: Response<RegisterResponse>
            ) {
                if (response.isSuccessful) {
                    Toast.makeText(activity, "Registration successful!", Toast.LENGTH_SHORT).show()

                    Handler().postDelayed({loginUser(email, password)}, 1000)
                } else {
                    Toast.makeText(activity, "Registration failed!", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<RegisterResponse>, t: Throwable) {
                Toast.makeText(activity, "Error: ${t.message}", Toast.LENGTH_SHORT).show()
            }
        })
    }

    fun loginUser(email: String, password: String) {
        val credentials = mapOf("email" to email, "password" to password)

        ApiClient.instance.login(credentials).enqueue(object : Callback<LoginResponse> {
            override fun onResponse(call: Call<LoginResponse>, response: Response<LoginResponse>) {
                if (response.isSuccessful) {
                    val loginResponse = response.body()

                    EasyPrefs.use().setObject("user", loginResponse?.user)
                    EasyPrefs.use().setString("token", loginResponse?.token)
                    EasyPrefs.use().signedIn = true

                    Toast.makeText(activity, "Login Successful!", Toast.LENGTH_SHORT).show()

                    activity.startActivity(Intent(activity, MainActivity::class.java))
                    activity.finish()
                } else {
                    Toast.makeText(activity, "Login Failed", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<LoginResponse>, t: Throwable) {
                Toast.makeText(activity, "Error: ${t.message}", Toast.LENGTH_SHORT).show()
            }
        })
    }
}