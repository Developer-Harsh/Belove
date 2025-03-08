package com.harsh.streamingapp.ui.auth

import android.content.Intent
import android.os.Bundle
import android.util.Patterns
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.harsh.streamingapp.R
import com.harsh.streamingapp.databinding.ActivityLoginBinding
import com.harsh.streamingapp.network.ApiHandler

class LoginActivity : AppCompatActivity() {

    private lateinit var binding: ActivityLoginBinding
    private lateinit var apiHandler: ApiHandler

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        apiHandler = ApiHandler(this)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        binding.register.setOnClickListener {
            startActivity(Intent(this@LoginActivity, RegisterActivity::class.java))
        }

        binding.signin.setOnClickListener {
            val email = binding.email.editText?.text.toString()
            val password = binding.password.editText?.text.toString()

            if (email.isEmpty())
                Toast.makeText(this@LoginActivity, "Email field is empty!", Toast.LENGTH_SHORT).show()
            else if (!Patterns.EMAIL_ADDRESS.matcher(email).matches())
                Toast.makeText(this@LoginActivity, "Email field is invalid!", Toast.LENGTH_SHORT).show()
            else if (password.isEmpty())
                Toast.makeText(this@LoginActivity, "Password field is empty!", Toast.LENGTH_SHORT).show()
            else if (password.length < 6)
                Toast.makeText(this@LoginActivity, "Password must be 6 digits or more!", Toast.LENGTH_SHORT).show()
            else
                apiHandler.loginUser(email, password)
        }
    }
}