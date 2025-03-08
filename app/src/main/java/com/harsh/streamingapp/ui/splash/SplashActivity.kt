package com.harsh.streamingapp.ui.splash

import android.content.Intent
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import com.harsh.streamingapp.ui.MainActivity
import com.harsh.streamingapp.R
import com.harsh.streamingapp.ui.auth.LoginActivity
import com.sneva.easyprefs.EasyPrefs
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class SplashActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_splash)

        CoroutineScope(Dispatchers.IO).launch {
            delay(3000)
            if (EasyPrefs.use().signedIn) {
                startActivity(Intent(this@SplashActivity, MainActivity::class.java))
            } else {
                startActivity(Intent(this@SplashActivity, LoginActivity::class.java))
            }
            finish()
        }
    }
}