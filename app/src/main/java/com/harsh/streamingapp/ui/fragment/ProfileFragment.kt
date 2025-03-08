package com.harsh.streamingapp.ui.fragment

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.net.toUri
import com.bumptech.glide.Glide
import com.harsh.streamingapp.R
import com.harsh.streamingapp.databinding.FragmentProfileBinding
import com.harsh.streamingapp.models.User
import com.harsh.streamingapp.ui.MainActivity
import com.harsh.streamingapp.ui.auth.LoginActivity
import com.harsh.streamingapp.ui.profile.EditProfileActivity
import com.sneva.easyprefs.EasyPrefs

class ProfileFragment : Fragment() {

    private lateinit var binding: FragmentProfileBinding
    private val user: User = EasyPrefs.use().getObject("user", User::class.java)

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentProfileBinding.inflate(inflater, container, false)

        Glide.with(requireContext()).load(user.profile)
            .placeholder(R.drawable.ic_launcher_background).into(binding.profile)
        binding.name.text = "${user.name}, ${user.age}"
        binding.location.text = user.location
        binding.about.text = user.about

        // here replace it with your terms url
        binding.terms.setOnClickListener { openBrowser("https://google.com/") }

        // this one to privacy policy url
        binding.privacy.setOnClickListener { openBrowser("https://google.com/") }

        // this one is for logout
        binding.logout.setOnClickListener {
            EasyPrefs.use().clearPrefs()
            val intent = Intent(requireContext(), LoginActivity::class.java)
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
            startActivity(intent)
            requireActivity().finish()
        }

        // this one for edit profile
        binding.edit.setOnClickListener {
            startActivity(
                Intent(
                    requireContext(),
                    EditProfileActivity::class.java
                )
            )
        }

        return binding.root
    }

    fun openBrowser(url: String) {
        val intent = Intent(Intent.ACTION_VIEW)
        intent.setData(url.toUri())
        startActivity(intent)
    }
}