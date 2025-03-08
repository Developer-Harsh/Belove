package com.harsh.streamingapp.ui.profile

import android.os.Bundle
import android.util.Patterns
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.bumptech.glide.Glide
import com.harsh.streamingapp.R
import com.harsh.streamingapp.databinding.ActivityEditProfileBinding
import com.harsh.streamingapp.models.User
import com.harsh.streamingapp.network.ApiClient
import com.harsh.streamingapp.network.ProfileResponse
import com.sneva.easyprefs.EasyPrefs
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class EditProfileActivity : AppCompatActivity() {

    lateinit var binding: ActivityEditProfileBinding
    private val user: User = EasyPrefs.use().getObject("user", User::class.java)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityEditProfileBinding.inflate(layoutInflater)
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.editProfile)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        Glide.with(this).load(user.profile).placeholder(R.drawable.ic_launcher_background).into(binding.profile)
        binding.name.editText!!.setText(user.name)
        binding.email.editText!!.setText(user.email)
        binding.location.editText!!.setText(user.location)

        binding.edit.setOnClickListener {
            val name = binding.name.editText!!.text.toString()
            val email = binding.email.editText!!.text.toString()
            val location = binding.location.editText!!.text.toString()

            if (name.isEmpty())
                Toast.makeText(this@EditProfileActivity, "Full name is empty!", Toast.LENGTH_SHORT).show()
            else if (name.length < 3)
                Toast.makeText(this@EditProfileActivity, "Full name must be 3 chars or more!", Toast.LENGTH_SHORT).show()
            else if (email.isEmpty())
                Toast.makeText(this@EditProfileActivity, "Email field is empty!", Toast.LENGTH_SHORT).show()
            else if (!Patterns.EMAIL_ADDRESS.matcher(email).matches())
                Toast.makeText(this@EditProfileActivity, "Email address is invalid!", Toast.LENGTH_SHORT).show()
            else if (location.isEmpty())
                Toast.makeText(this@EditProfileActivity, "Location is empty!", Toast.LENGTH_SHORT).show()
            else updateProfile(user.username!!, mapOf(
                "name" to name,
                "email" to email,
                "location" to location,
            ))
        }
    }

    private fun updateProfile(username: String, updates: Map<String, String>) {
        val call = ApiClient.instance.updateProfile(username, updates)

        call.enqueue(object : Callback<ProfileResponse> {
            override fun onResponse(call: Call<ProfileResponse>, response: Response<ProfileResponse>) {
                if (response.isSuccessful) {
                    response.body()?.let {
                        EasyPrefs.use().setObject("user", it.updatedProfile)
                        Toast.makeText(this@EditProfileActivity, "Profile updated!!", Toast.LENGTH_SHORT).show()
                    }
                } else {
                    println("Failed to update profile: ${response.message()}")
                    Toast.makeText(this@EditProfileActivity, "Profile updated!!", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<ProfileResponse>, t: Throwable) {
                println("Error: ${t.message}")
            }
        })
    }
}