package com.harsh.streamingapp.ui.fragment

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.google.android.material.tabs.TabLayoutMediator
import com.harsh.streamingapp.R
import com.harsh.streamingapp.adapters.PageAdapter
import com.harsh.streamingapp.databinding.FragmentHomeBinding

class HomeFragment : Fragment() {

    private lateinit var binding: FragmentHomeBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentHomeBinding.inflate(inflater, container, false)

        val adapter = PageAdapter(this)
        binding.viewpager.adapter = adapter

        TabLayoutMediator(binding.tabs, binding.viewpager) { tab, position ->
            tab.text = when (position) {
                0 -> "For You"
                1 -> "Nearby"
                else -> ""
            }
        }.attach()

        return binding.root
    }
}