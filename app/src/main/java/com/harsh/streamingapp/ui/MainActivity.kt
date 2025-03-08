package com.harsh.streamingapp.ui

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.os.StrictMode
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.google.firebase.messaging.FirebaseMessaging
import com.harsh.streamingapp.R
import com.harsh.streamingapp.databinding.ActivityMainBinding
import com.harsh.streamingapp.models.Room
import com.harsh.streamingapp.models.User
import com.harsh.streamingapp.network.ApiClient
import com.harsh.streamingapp.network.ProfileResponse
import com.harsh.streamingapp.ui.fragment.HomeFragment
import com.harsh.streamingapp.ui.fragment.MatchesFragment
import com.harsh.streamingapp.ui.fragment.ProfileFragment
import com.harsh.streamingapp.ui.fragment.SearchFragment
import com.harsh.streamingapp.ui.webrtc.LivestreamActivity
import com.sneva.easyprefs.EasyPrefs
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.util.UUID

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private val user: User = EasyPrefs.use().getObject("user", User::class.java)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        changeFragment(HomeFragment())

        binding.live.setOnClickListener {
            createRoom()
        }

        binding.bottomNavigation.setOnItemSelectedListener {
            when (it.itemId) {
                R.id.discover -> {
                    changeFragment(HomeFragment())
                    true
                }
                R.id.search -> {
                    changeFragment(SearchFragment())
                    true
                }
                R.id.li -> {
                    createRoom()
                    true
                }
                R.id.matches -> {
                    changeFragment(MatchesFragment())
                    true
                }
                R.id.profile -> {
                    changeFragment(ProfileFragment())
                    true
                }
                else -> {
                    changeFragment(HomeFragment())
                    true
                }
            }
        }

        getToken()
    }

    private fun createRoom() {
        val user = EasyPrefs.use().getObject("user", User::class.java)
        val room  = Room(
            user.username.toString(),
            user.username.toString(),
            1
        )

        ApiClient.instance.updateRoom(user.username.toString(), room).enqueue(object : Callback<Room> {
            override fun onResponse(call: Call<Room>, response: Response<Room>) {
                if (response.isSuccessful) {
                    val data = response.body()!!
                    val intent = Intent(this@MainActivity, LivestreamActivity::class.java)
                    intent.putExtra("isHost", true)
                    intent.putExtra("url", "/index.html")
                    intent.putExtra("room", data)
                    intent.putExtra("user", user)
                    startActivity(intent)
                }
            }

            override fun onFailure(call: Call<Room>, t: Throwable) {
                Toast.makeText(this@MainActivity, "Cannot create room!", Toast.LENGTH_SHORT).show()
            }
        })
    }

    // get token
    private fun getToken() {
        val policy = StrictMode.ThreadPolicy.Builder().permitAll().build()
        StrictMode.setThreadPolicy(policy)

        FirebaseMessaging.getInstance().token.addOnCompleteListener {
            if (it.isSuccessful) {
                EasyPrefs.use().setString("tokenFCM", it.result)
                val updates = mapOf(
                    "tokenFCM" to it.result,
                )
                updateProfile(user.username.toString(), updates)
            }
        }
    }

    // we will fix some issues in next video :: GOOD BYE
    // we observed that when we send some kind of response to inviter : it doesn't affect receiver means invitor side screen but we want it to finish activity or start calling


    // to update token
    private fun updateProfile(username: String, updates: Map<String, String>) {
        val call = ApiClient.instance.updateProfile(username, updates)

        call.enqueue(object : Callback<ProfileResponse> {
            override fun onResponse(call: Call<ProfileResponse>, response: Response<ProfileResponse>) {
                if (response.isSuccessful) {
                    response.body()?.let {
                        EasyPrefs.use().setObject("user", it.updatedProfile)
                    }
                } else {
                    println("Failed to update profile: ${response.message()}")
                }
            }

            override fun onFailure(call: Call<ProfileResponse>, t: Throwable) {
                println("Error: ${t.message}")
            }
        })
    }

    private fun changeFragment(fragment: Fragment) {
        supportFragmentManager.beginTransaction()
            .replace(binding.fragments.id, fragment)
            .commit()
    }
}