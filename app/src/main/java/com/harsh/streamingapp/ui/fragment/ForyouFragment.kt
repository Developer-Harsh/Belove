package com.harsh.streamingapp.ui.fragment

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.bumptech.glide.Glide
import com.harsh.streamingapp.R
import com.harsh.streamingapp.databinding.FragmentForyouBinding
import com.harsh.streamingapp.models.Room
import com.harsh.streamingapp.models.User
import com.harsh.streamingapp.network.ApiClient
import com.harsh.streamingapp.ui.call.OutgoingActivity
import com.harsh.streamingapp.ui.webrtc.LivestreamActivity
import com.sneva.easyprefs.EasyPrefs
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class ForyouFragment : Fragment() {

    private lateinit var binding: FragmentForyouBinding
    private var liveUsers: List<User> = ArrayList()
    private var rooms: List<Room> = ArrayList()
    private var counter = 0

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentForyouBinding.inflate(inflater, container, false)

        getUsers()

        return binding.root
    }

    private fun getUsers() {
        ApiClient.instance.getProfiles().enqueue(object : Callback<List<User>> {
            override fun onResponse(call: Call<List<User>>, response: Response<List<User>>) {
                if (response.isSuccessful) {
                    val users = response.body() ?: emptyList()
                    getLives(users)
                } else  {
                    Toast.makeText(requireContext(), "Failed to fetch users data!", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<List<User>>, t: Throwable) {
                Toast.makeText(requireContext(), "Error: ${t.message}", Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun getLives(users: List<User>) {
        ApiClient.instance.getRooms().enqueue(object : Callback<List<Room>> {
            override fun onResponse(call: Call<List<Room>>, response: Response<List<Room>>) {
                if (response.isSuccessful) {
                    rooms = response.body() ?: emptyList()
                    liveUsers = users.filter { user -> rooms.any { it.userId == user.username } }
                    if (liveUsers.isNotEmpty()) {
                        setData()
                    } else {
                        Toast.makeText(requireContext(), "No live users available!", Toast.LENGTH_SHORT).show()
                    }
                }
            }

            override fun onFailure(call: Call<List<Room>>, t: Throwable) {
                Toast.makeText(requireContext(), "Error: ${t.message}", Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun setData() {
        if (liveUsers.isNotEmpty()) {
            profile(liveUsers[counter])
        }

        binding.next.setOnClickListener {
            if (counter + 1 >= liveUsers.size) {
                Toast.makeText(requireContext(), "No more live users available!", Toast.LENGTH_SHORT).show()
            } else {
                counter++
                profile(liveUsers[counter])
            }
        }
    }

    private fun profile(user: User) {
        val room = rooms.find { it.userId == user.username }

        Glide.with(requireContext()).load(user.profile).placeholder(R.drawable.ic_launcher_background).into(binding.profile)
        binding.name.text = "${user.name}, ${user.age}"
        binding.location.text = user.location
        binding.about.text = user.about

        binding.openLive.setOnClickListener {
            val intent = Intent(context, LivestreamActivity::class.java)
            intent.putExtra("isHost", false)
            intent.putExtra("url", "/viewer.html")
            intent.putExtra("room", room)
            intent.putExtra("user", user)
            startActivity(intent)
        }
    }
}