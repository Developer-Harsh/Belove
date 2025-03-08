package com.harsh.streamingapp.ui.webrtc

import android.Manifest
import android.annotation.SuppressLint
import android.content.pm.PackageManager
import android.os.Bundle
import android.view.View
import android.webkit.PermissionRequest
import android.webkit.WebChromeClient
import android.webkit.WebResourceError
import android.webkit.WebResourceRequest
import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.harsh.streamingapp.databinding.ActivityLivestreamBinding
import com.harsh.streamingapp.models.Room
import com.harsh.streamingapp.models.User
import com.harsh.streamingapp.network.ApiClient.BASE_URL

class LivestreamActivity : AppCompatActivity() {

    private lateinit var binding: ActivityLivestreamBinding
    private var permissions: Array<String> = arrayOf(Manifest.permission.CAMERA, Manifest.permission.RECORD_AUDIO)
    private lateinit var room: Room
    private lateinit var user: User
    private var isHost = true

    @SuppressLint("NewApi")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityLivestreamBinding.inflate(layoutInflater)
        setContentView(binding.root)

        askPermission()

        room = intent.getSerializableExtra("room", Room::class.java)!!
        user = intent.getSerializableExtra("user", User::class.java)!!
        isHost = intent.getBooleanExtra("isHost", false)

        binding.name.text = user.name.toString()

        if (isHost) {
            binding.controls.visibility = View.VISIBLE
            binding.startLL.visibility = View.VISIBLE
        }

        binding.close.setOnClickListener {
            finish()
        }

        setupStream()
    }

    private fun setupStream() {
        binding.webView.settings.javaScriptEnabled = true
        binding.webView.settings.domStorageEnabled = true
        binding.webView.settings.mediaPlaybackRequiresUserGesture = false
        binding.webView.settings.allowFileAccessFromFileURLs = true
        binding.webView.settings.allowUniversalAccessFromFileURLs = true

        binding.webView.webChromeClient = object : WebChromeClient() {
            override fun onPermissionRequest(request: PermissionRequest) {
                runOnUiThread {
                    request.grant(request.resources)
                }
            }
        }

        binding.webView.webViewClient = object : WebViewClient() {
            override fun onReceivedError(
                view: WebView?,
                request: WebResourceRequest?,
                error: WebResourceError?
            ) {
                super.onReceivedError(view, request, error)
            }

            override fun onPageFinished(view: WebView?, url: String?) {
                super.onPageFinished(view, url)
                binding.webView.post { binding.webView.evaluateJavascript("javascript:init(\"${room.roomId}\");", null) }
            }
        }

        binding.webView.loadUrl("https://960d-2409-40d2-2066-6baf-1996-a9a8-4f3d-30ae.ngrok-free.app/" + intent.getStringExtra("url").toString())
    }

    private fun askPermission() {
        if (
            ContextCompat.checkSelfPermission(this, permissions[0]) != PackageManager.PERMISSION_GRANTED ||
            ContextCompat.checkSelfPermission(this, permissions[1]) != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(
                this,
                permissions,
                200
            )
        } else {
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray,
        deviceId: Int
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults, deviceId)
        if (requestCode == 200) {
            if ((grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED)) {
                setupStream()
            } else {
                Toast.makeText(this, "Permission Denied", Toast.LENGTH_SHORT).show()
                askPermission()
            }
        }
    }
}