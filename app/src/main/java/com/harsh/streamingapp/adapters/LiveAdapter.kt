package com.harsh.streamingapp.adapters

import android.content.Context
import android.content.Intent
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.harsh.streamingapp.R
import com.harsh.streamingapp.databinding.ItemLivesBinding
import com.harsh.streamingapp.databinding.ItemNearbyBinding
import com.harsh.streamingapp.models.Room
import com.harsh.streamingapp.models.User
import com.harsh.streamingapp.network.ApiClient
import com.harsh.streamingapp.ui.webrtc.LivestreamActivity
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class LiveAdapter(
    private val context: Context,
    private val list: List<Room>,
    private val users: List<User>,
): RecyclerView.Adapter<LiveAdapter.ViewHolder>() {
    class ViewHolder(private val binding: ItemLivesBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(context: Context, user: User, room: Room) {
            Glide.with(context).load(user.profile.toString()).placeholder(R.drawable.ic_launcher_background).into(binding.profile)
            binding.location.text = user.name.toString()

            binding.root.setOnClickListener {
                val intent = Intent(context, LivestreamActivity::class.java)
                intent.putExtra("isHost", false)
                intent.putExtra("url", "/viewer.html")
                intent.putExtra("room", room)
                intent.putExtra("user", user)
                context.startActivity(intent)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemLivesBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val room = list[position]
        val user = users.find { it.username == room.userId }

        if (user != null)
            holder.bind(context, user, room)
    }

    override fun getItemCount(): Int = list.size
}