package com.harsh.streamingapp.ui.fragment

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.GridLayoutManager
import com.harsh.streamingapp.R
import com.harsh.streamingapp.adapters.NearbyAdapter
import com.harsh.streamingapp.databinding.FragmentNearbyBinding
import com.harsh.streamingapp.models.Room
import com.harsh.streamingapp.models.User
import com.harsh.streamingapp.network.ApiClient
import com.sneva.easyprefs.EasyPrefs
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class NearbyFragment : Fragment() {

    private lateinit var binding: FragmentNearbyBinding
    private lateinit var adapter: NearbyAdapter
    private lateinit var users: List<User>
    private lateinit var rooms: List<Room>

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentNearbyBinding.inflate(inflater, container, false)

        users = ArrayList()
        rooms = ArrayList()

        getUsers()

        return binding.root
    }

    private fun getUsers() {
        ApiClient.instance.getProfiles().enqueue(object : Callback<List<User>> {
            override fun onResponse(call: Call<List<User>>, response: Response<List<User>>) {
                if (response.isSuccessful) {
                    val user = EasyPrefs.use().getObject("user", User::class.java)
                    val filteredUsers = ArrayList<User>()

                    users = response.body()!!

                    users.forEach {
                        if (it.location == user.location)
                            filteredUsers.add(it)
                    }

                    if (filteredUsers.isNotEmpty()) {
                        getLives()
                    } else
                        Toast.makeText(requireContext(), "No matches available for you, rn!", Toast.LENGTH_SHORT).show()
                } else  {
                    Toast.makeText(requireContext(), "Failed to fetch users data!", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<List<User>>, t: Throwable) {
                Toast.makeText(requireContext(), "Error: ${t.message}", Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun getLives() {
        ApiClient.instance.getRooms().enqueue(object : Callback<List<Room>> {
            override fun onResponse(call: Call<List<Room>>, response: Response<List<Room>>) {
                if (response.isSuccessful) {
                    rooms = response.body()!!

                    updateRecyclerView(rooms)
                }
            }

            override fun onFailure(call: Call<List<Room>>, t: Throwable) {
                Toast.makeText(requireContext(), "Error: ${t.message}", Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun updateRecyclerView(rooms: List<Room>) {
        adapter = NearbyAdapter(requireContext(), rooms, users)
        binding.list.setHasFixedSize(false)
        binding.list.layoutManager = GridLayoutManager(requireContext(), 2).apply {
            orientation = GridLayoutManager.VERTICAL
        }
        binding.list.adapter = adapter
    }
}