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
import com.harsh.streamingapp.databinding.ActivityRegisterBinding
import com.harsh.streamingapp.network.ApiHandler
import java.util.UUID

class RegisterActivity : AppCompatActivity() {

    private lateinit var binding: ActivityRegisterBinding
    private lateinit var apiHandler: ApiHandler

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityRegisterBinding.inflate(layoutInflater)
        setContentView(binding.root)

        apiHandler = ApiHandler(this)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        binding.register.setOnClickListener {
            finish()
        }

        binding.signin.setOnClickListener {
            val name = binding.name.editText?.text.toString()
            val email = binding.email.editText?.text.toString()
            val password = binding.password.editText?.text.toString()

            if (name.isEmpty())
                Toast.makeText(this@RegisterActivity, "Name field is empty!", Toast.LENGTH_SHORT).show()
            else if (name.length < 3)
                Toast.makeText(this@RegisterActivity, "Name must be 3 chars or more!", Toast.LENGTH_SHORT).show()
            else if (email.isEmpty())
                Toast.makeText(this@RegisterActivity, "Email field is empty!", Toast.LENGTH_SHORT).show()
            else if (!Patterns.EMAIL_ADDRESS.matcher(email).matches())
                Toast.makeText(this@RegisterActivity, "Email field is invalid!", Toast.LENGTH_SHORT).show()
            else if (password.isEmpty())
                Toast.makeText(this@RegisterActivity, "Password field is empty!", Toast.LENGTH_SHORT).show()
            else if (password.length < 6)
                Toast.makeText(this@RegisterActivity, "Password must be 6 digits or more!", Toast.LENGTH_SHORT).show()
            else
                apiHandler.registerUser(name, UUID.randomUUID().toString(), "NO", email, password, "Beloveland", "Male", 18, "Hey, im here on belove app.")
        }
    }
}