package com.harsh.streamingapp.ui.call

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Bundle
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.harsh.streamingapp.R
import com.harsh.streamingapp.databinding.ActivityIncomingBinding
import com.harsh.streamingapp.models.Notification
import com.harsh.streamingapp.models.NotificationData
import com.harsh.streamingapp.network.ApiClient
import com.harsh.streamingapp.ui.webrtc.LivestreamActivity
import com.harsh.streamingapp.utils.Constants
import jp.wasabeef.glide.transformations.BlurTransformation
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class IncomingActivity : AppCompatActivity() {

    lateinit var binding: ActivityIncomingBinding
    private var meetingType: String? = null
    private var name: String? = null
    private var email: String? = null
    private var profile: String? = null
    private var inviterToken: String? = null
    private var room: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityIncomingBinding.inflate(layoutInflater)
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        meetingType = intent.getStringExtra(Constants.REMOTE_MSG_MEETING_TYPE)
        name = intent.getStringExtra(Constants.KEY_NAME)
        email = intent.getStringExtra(Constants.KEY_EMAIL)
        profile = intent.getStringExtra(Constants.KEY_PROFILE)
        inviterToken = intent.getStringExtra(Constants.REMOTE_MSG_INVITER_TOKEN)
        room = intent.getStringExtra(Constants.REMOTE_MSG_MEETING_ROOM)

        Glide.with(this)
            .load(profile)
            .apply(RequestOptions.bitmapTransform(BlurTransformation(5, 5)))
            .into(binding.bgProfile)

        Glide.with(this)
            .load(profile)
            .into(binding.profile)

        binding.name.text = name

        binding.reject.setOnClickListener {
            sendInvitationResponse(Constants.REMOTE_MSG_INVITATION_REJECTED,
                intent.getStringExtra(Constants.REMOTE_MSG_INVITER_TOKEN).toString()
            )
        }

        binding.answer.setOnClickListener {
            sendInvitationResponse(Constants.REMOTE_MSG_INVITATION_ACCEPTED,
                intent.getStringExtra(Constants.REMOTE_MSG_INVITER_TOKEN).toString()
            )
        }
    }

    private fun sendInvitationResponse(type: String, receiverToken: String) {
        val data = JSONObject()
        data.put(Constants.REMOTE_MSG_TYPE, Constants.REMOTE_MSG_INVITATION_RESPONSE)
        data.put(Constants.REMOTE_MSG_INVITATION_RESPONSE, type)

        val notification = Notification(
            message = NotificationData(
                token = receiverToken,
                data = hashMapOf(
                    "title" to "Call response",
                    "body" to data.toString()
                )
            )
        )

        sendRemoteMessage(notification, type)
    }

    private fun sendRemoteMessage(remoteMessageBody: Notification, type: String) {
        ApiClient.fcmClient.sendRemoteMessage(remoteMessageBody).enqueue(object :
            Callback<Notification> {
            override fun onResponse(p0: Call<Notification>, response: Response<Notification>) {
                if (response.isSuccessful) {
                    if (type == Constants.REMOTE_MSG_INVITATION_ACCEPTED) {
                        startCalling()
                        Toast.makeText(this@IncomingActivity, "Invitation Sent Successfully", Toast.LENGTH_SHORT).show()
                    } else {
                        Toast.makeText(this@IncomingActivity, "Invitation ended", Toast.LENGTH_SHORT).show()
                        finish()
                    }
                } else {
                    Toast.makeText(this@IncomingActivity, "Error: " + response.message(), Toast.LENGTH_SHORT).show()
                    finish()
                }
            }

            override fun onFailure(p0: Call<Notification>, p1: Throwable) {
                Toast.makeText(this@IncomingActivity, p1.message.toString(), Toast.LENGTH_SHORT).show()
                finish()
            }
        })
    }

    private fun startCalling() {
        try {
            val intent: Intent = if (meetingType == "video") {
                Intent(
                    this@IncomingActivity,
                    LivestreamActivity::class.java
                )
            } else {
                Intent(
                    this@IncomingActivity,
                    LivestreamActivity::class.java
                )
            }
            intent.putExtra(Constants.REMOTE_MSG_MEETING_ROOM, room)
            startActivity(intent)
            finish()
        } catch (exception: java.lang.Exception) {
            Toast.makeText(this, exception.message, Toast.LENGTH_SHORT).show()
        }
    }

    private val invitationResponseReceiver: BroadcastReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            val type = intent.getStringExtra(Constants.REMOTE_MSG_INVITATION_RESPONSE)
            if (type != null) {
                if (type == Constants.REMOTE_MSG_INVITATION_CANCELLED) {
                    Toast.makeText(context, "invitation Accepted", Toast.LENGTH_SHORT).show()
                    finish()
                }
            }
        }
    }

    override fun onStart() {
        super.onStart()
        LocalBroadcastManager.getInstance(applicationContext)
            .registerReceiver(
                invitationResponseReceiver,
                IntentFilter(Constants.REMOTE_MSG_INVITATION_RESPONSE)
            )
    }

    override fun onStop() {
        super.onStop()
        LocalBroadcastManager.getInstance(applicationContext)
            .unregisterReceiver(invitationResponseReceiver)
    }
}