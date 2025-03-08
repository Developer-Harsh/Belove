package com.harsh.streamingapp.ui.fragment

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.widget.addTextChangedListener
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.harsh.streamingapp.R
import com.harsh.streamingapp.adapters.NearbyAdapter
import com.harsh.streamingapp.adapters.UserAdapter
import com.harsh.streamingapp.databinding.FragmentSearchBinding
import com.harsh.streamingapp.models.User
import com.harsh.streamingapp.network.ApiClient
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class SearchFragment : Fragment() {

    private lateinit var binding: FragmentSearchBinding
    private lateinit var adapter: UserAdapter
    private lateinit var users: List<User>
    private lateinit var filteredList: List<User>

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentSearchBinding.inflate(inflater, container, false)

        users = ArrayList()

        getUsers()

        return binding.root
    }

    private fun initSearchBar() {
        binding.search.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {

            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                val query = s.toString().trim()

                filteredList = if (query.isEmpty()) {
                    users
                } else {
                    users.filter {
                        it.name!!.contains(query, ignoreCase = true)
                    }
                }

                setData(filteredList)
            }

            override fun afterTextChanged(s: Editable?) {
            }
        })
    }

    private fun getUsers() {
        ApiClient.instance.getProfiles().enqueue(object : Callback<List<User>> {
            override fun onResponse(call: Call<List<User>>, response: Response<List<User>>) {
                if (response.isSuccessful) {
                    users = response.body()!!
                    Log.e("USERS_SEARCH", users.toString())

                    setData(users)
                    initSearchBar()
                } else  {
                    Toast.makeText(requireContext(), "Failed to fetch users data!", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<List<User>>, t: Throwable) {
                Toast.makeText(requireContext(), "Error: ${t.message}", Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun setData(users: List<User>) {
        adapter = UserAdapter(requireContext(), users)
        binding.list.setHasFixedSize(false)
        binding.list.layoutManager = LinearLayoutManager(requireContext()).apply {
            orientation = LinearLayoutManager.VERTICAL
        }
        binding.list.adapter = adapter
    }
}