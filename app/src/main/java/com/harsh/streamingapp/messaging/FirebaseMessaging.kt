package com.harsh.streamingapp.messaging

import android.Manifest
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Handler
import android.os.Looper
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.graphics.drawable.IconCompat
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import com.harsh.streamingapp.R
import com.harsh.streamingapp.ui.call.IncomingActivity
import com.harsh.streamingapp.utils.Constants
import org.json.JSONObject
import kotlin.random.Random

class FirebaseMessaging : FirebaseMessagingService() {

    private val channelId = "Messaging"
    private val channelName = "Messaging_Call_Service"

    private val notificationManager: NotificationManager by lazy {
        getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
    }

    override fun onNewToken(token: String) {
        super.onNewToken(token)
    }

    override fun onMessageReceived(remoteMessage: RemoteMessage) {
        super.onMessageReceived(remoteMessage)
        remoteMessage.data.let { data ->
            val title = data["title"]
            val body = data["body"]
            val jsonData = JSONObject(body ?: "{}")
            val type = jsonData.optString(Constants.REMOTE_MSG_TYPE)

            when (type) {
                Constants.REMOTE_MSG_INVITATION -> {
                    val meetingType = jsonData.optString(Constants.REMOTE_MSG_MEETING_TYPE)
                    val inviterToken = jsonData.optString(Constants.REMOTE_MSG_INVITER_TOKEN)
                    val meetingRoom = jsonData.optString(Constants.REMOTE_MSG_MEETING_ROOM)
                    val name = jsonData.optString(Constants.KEY_NAME)
                    val email = jsonData.optString(Constants.KEY_EMAIL)
                    val profile = jsonData.optString(Constants.KEY_PROFILE)

                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                        createNotificationChannel()
                    }

                    val intent = Intent(applicationContext, IncomingActivity::class.java)
                    intent.putExtra(Constants.REMOTE_MSG_MEETING_TYPE, meetingType)
                    intent.putExtra(Constants.KEY_NAME, name)
                    intent.putExtra(Constants.KEY_EMAIL, email)
                    intent.putExtra(Constants.KEY_PROFILE, profile)
                    intent.putExtra(Constants.REMOTE_MSG_INVITER_TOKEN, inviterToken)
                    intent.putExtra(Constants.REMOTE_MSG_MEETING_ROOM, meetingRoom)
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)
                    val pendingIntent = PendingIntent.getActivity(applicationContext, 0, intent, PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT)

                    val builder = NotificationCompat.Builder(applicationContext, channelId)
                        .setSmallIcon(IconCompat.createWithResource(applicationContext, R.drawable.ic_launcher_foreground))
                        .setColor(applicationContext.getColor(R.color.black))
                        .setContentTitle(title.toString())
                        .setContentText("$name is calling to you, answer or reject your call.")
                        .setPriority(NotificationCompat.PRIORITY_HIGH)
                        .setContentIntent(pendingIntent)
                        .setBadgeIconType(R.drawable.ic_launcher_foreground)
                        .setAutoCancel(true)

                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU){
                        with(NotificationManagerCompat.from(applicationContext)) {
                            if (ActivityCompat.checkSelfPermission(
                                    applicationContext,
                                    Manifest.permission.POST_NOTIFICATIONS
                                ) != PackageManager.PERMISSION_GRANTED
                            ) {
                                return
                            }
                            notify(Random.nextInt(3000), builder.build())
                        }
                    } else{
                        NotificationManagerCompat.from(applicationContext).notify(Random.nextInt(3000), builder.build())
                    }

                    Handler(Looper.getMainLooper()).postDelayed({
                        notificationManager.cancel(1)
                    }, 5000)

                    startActivity(intent)
                }
                Constants.REMOTE_MSG_INVITATION_RESPONSE -> {
                    val intent = Intent(Constants.REMOTE_MSG_INVITATION_RESPONSE)
                    intent.putExtra(
                        Constants.REMOTE_MSG_INVITATION_RESPONSE,
                        jsonData.optString(Constants.REMOTE_MSG_INVITATION_RESPONSE)
                    )
                    LocalBroadcastManager.getInstance(applicationContext).sendBroadcast(intent)
                }
                else -> {
                    Log.e("EMPTY", "NANA")
                }
            }
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun createNotificationChannel() {
        val channel = NotificationChannel(
            channelId,
            channelName,
            NotificationManager.IMPORTANCE_HIGH,
        )
        notificationManager.createNotificationChannel(channel)
    }
}