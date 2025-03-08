package com.harsh.streamingapp.adapters

import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.harsh.streamingapp.ui.fragment.ForyouFragment
import com.harsh.streamingapp.ui.fragment.NearbyFragment

class PageAdapter(fragment: Fragment): FragmentStateAdapter(fragment) {
    override fun getItemCount(): Int {
        return 2
    }

    override fun createFragment(position: Int): Fragment {
        return when (position) {
            0 -> ForyouFragment()
            1 -> NearbyFragment()
            else -> ForyouFragment()
        }
    }
}