package com.harsh.streamingapp.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.harsh.streamingapp.R
import com.harsh.streamingapp.databinding.ItemNearbyBinding
import com.harsh.streamingapp.databinding.ItemUserBinding
import com.harsh.streamingapp.models.User

class UserAdapter(
    private val context: Context,
    private val list: List<User>,
): RecyclerView.Adapter<UserAdapter.ViewHolder>() {
    class ViewHolder(private val binding: ItemUserBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(user: User, context: Context) {
            Glide.with(context).load(user.profile.toString()).placeholder(R.drawable.ic_launcher_background).into(binding.profile)
            binding.name.text = user.name
            binding.location.text = user.location
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemUserBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(list[position], context)
    }

    override fun getItemCount(): Int = list.size
}