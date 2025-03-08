package com.harsh.streamingapp.ui.call

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Build
import android.os.Bundle
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.google.firebase.messaging.FirebaseMessaging
import com.harsh.streamingapp.R
import com.harsh.streamingapp.databinding.ActivityOutgoingBinding
import com.harsh.streamingapp.models.Notification
import com.harsh.streamingapp.models.NotificationData
import com.harsh.streamingapp.models.User
import com.harsh.streamingapp.network.ApiClient
import com.harsh.streamingapp.utils.Constants
import com.sneva.easyprefs.EasyPrefs
import jp.wasabeef.glide.transformations.BlurTransformation
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.util.UUID
import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid

class OutgoingActivity : AppCompatActivity() {

    lateinit var binding: ActivityOutgoingBinding
    private val currUser: User = EasyPrefs.use().getObject("user", User::class.java)
    lateinit var user: User
    private var inviterToken: String? = null
    private var meetingRoom: String? = null
    private var meetingType: String? = null
    private var rejectionCount = 0
    private var totalReceivers = 0

    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityOutgoingBinding.inflate(layoutInflater)
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        user = intent.getSerializableExtra("user", User::class.java)!!
        meetingType = intent.getStringExtra("type").toString()

        Glide.with(this)
            .load(user.profile)
            .apply(RequestOptions.bitmapTransform(BlurTransformation(5, 5)))
            .into(binding.bgProfile)

        Glide.with(this)
            .load(user.profile)
            .into(binding.profile)

        binding.name.text = user.name.toString()
        binding.email.text = user.email.toString()

        binding.reject.setOnClickListener {
            cancelInvitation(user.tokenFCM)
        }

        FirebaseMessaging.getInstance().getToken().addOnCompleteListener { task ->
            if (task.isSuccessful && task.result != null) {
                inviterToken = task.result
                initiateMeeting(meetingType!!, user.tokenFCM)
            }
        }
    }

    private fun initiateMeeting(
        meetingType: String,
        receiverToken: String?,
    ) {
        val data = JSONObject()
        data.put(Constants.REMOTE_MSG_TYPE, Constants.REMOTE_MSG_INVITATION)
        data.put(Constants.REMOTE_MSG_MEETING_TYPE, meetingType)
        data.put(Constants.KEY_NAME, currUser.name)
        data.put(Constants.KEY_EMAIL, currUser.email)
        data.put(Constants.REMOTE_MSG_INVITER_TOKEN, inviterToken)
        data.put(Constants.REMOTE_MSG_MEETING_ROOM, currUser.username + "_" + UUID.randomUUID().toString().substring(0, 5))

        val notification = Notification(
            message = NotificationData(
                token = receiverToken,
                data = hashMapOf(
                    "title" to "Incoming call from ${user.name}",
                    "body" to data.toString()
                )
            )
        )

        sendRemoteMessage(notification, Constants.REMOTE_MSG_INVITATION)
    }

    private fun sendRemoteMessage(remoteMessageBody: Notification, type: String) {
        ApiClient.fcmClient.sendRemoteMessage(remoteMessageBody).enqueue(object : Callback<Notification> {
            override fun onResponse(p0: Call<Notification>, response: Response<Notification>) {
                if (response.isSuccessful) {
                    if (type == Constants.REMOTE_MSG_INVITATION) {
                        Toast.makeText(this@OutgoingActivity, "Invitation Sent Successfully", Toast.LENGTH_SHORT).show()
                    } else if (type == Constants.REMOTE_MSG_INVITATION_RESPONSE) {
                        Toast.makeText(this@OutgoingActivity, "Response Sent Successfully", Toast.LENGTH_SHORT).show()
                        finish()
                    }
                } else {
                    Toast.makeText(this@OutgoingActivity, "Error: " + response.message(), Toast.LENGTH_SHORT).show()
                    finish()
                }
            }

            override fun onFailure(p0: Call<Notification>, p1: Throwable) {
                Toast.makeText(this@OutgoingActivity, p1.message.toString(), Toast.LENGTH_SHORT).show()
                finish()
            }
        })
    }

    private fun cancelInvitation(receiverToken: String?) {
        val data = JSONObject()
        data.put(Constants.REMOTE_MSG_TYPE, Constants.REMOTE_MSG_INVITATION_RESPONSE)
        data.put(
            Constants.REMOTE_MSG_INVITATION_RESPONSE,
            Constants.REMOTE_MSG_INVITATION_CANCELLED
        )

        val notification = Notification(
            message = NotificationData(
                token = receiverToken,
                data = hashMapOf(
                    "title" to "Incoming call from ${currUser.name}",
                    "body" to data.toString()
                )
            )
        )

        sendRemoteMessage(notification, Constants.REMOTE_MSG_INVITATION_RESPONSE)
    }

    private fun startCalling() {
        try {
            val intent: Intent = if (meetingType == "video") {
                Intent(
                    this@OutgoingActivity,
                    CallActivity::class.java
                )
            } else {
                Intent(
                    this@OutgoingActivity,
                    CallActivity::class.java
                )
            }
            intent.putExtra(Constants.REMOTE_MSG_MEETING_ROOM, meetingRoom)
            startActivity(intent)
            finish()
        } catch (exception: java.lang.Exception) {
            Toast.makeText(this, exception.message, Toast.LENGTH_SHORT).show()
        }
    }

    private val invitationResponseReceiver: BroadcastReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            val response = intent.getStringExtra(Constants.REMOTE_MSG_INVITATION_RESPONSE)
            if (response != null) {
                if (response == Constants.REMOTE_MSG_INVITATION_ACCEPTED) {
                    startCalling()
                } else if (response == Constants.REMOTE_MSG_INVITATION_REJECTED) {
                    rejectionCount++
                    if (rejectionCount == totalReceivers) {
                        Toast.makeText(context, "Invitation Rejected", Toast.LENGTH_SHORT).show()
                        finish()
                    }
                }
            }
        }
    }

    override fun onResume() {
        super.onResume()
        LocalBroadcastManager.getInstance(applicationContext).registerReceiver(
            invitationResponseReceiver,
            IntentFilter(Constants.REMOTE_MSG_INVITATION_RESPONSE)
        )
    }

    override fun onPause() {
        super.onPause()
        LocalBroadcastManager.getInstance(applicationContext)
            .unregisterReceiver(invitationResponseReceiver)
    }

    override fun onStop() {
        super.onStop()
        cancelInvitation(user.tokenFCM)
    }
}