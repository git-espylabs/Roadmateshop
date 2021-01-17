package com.roadmate.shop.firebase

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.media.RingtoneManager
import android.os.Build
import androidx.core.app.NotificationCompat
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import com.roadmate.shop.R
import com.roadmate.shop.log.AppLogger
import com.roadmate.shop.preference.FcmDetails
import com.roadmate.shop.ui.activities.SplashActivity

class FcmService: FirebaseMessagingService() {

    var context: Context? = null

    override fun onMessageReceived(remoteMessage: RemoteMessage) {
        context = this

        if (remoteMessage.data.isNotEmpty()) {
            createNotification(remoteMessage.data.toString())
        }

        if (remoteMessage.notification != null) {
            createNotification(remoteMessage.notification?.body!!)
        }
    }

    override fun onNewToken(p0: String) {
        super.onNewToken(p0)
        AppLogger.info("FCM Token", p0)
        FcmDetails().fcmToken = p0
    }

    private fun createNotification(messageBody: String) {
        val channelId = "roadmate_channel_09"
        val name: CharSequence = getString(R.string.app_name)
        val importance = NotificationManager.IMPORTANCE_HIGH
        val intent = Intent(context, SplashActivity::class.java)
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
        val resultIntent = PendingIntent.getActivity(
            context, 0, intent,
            PendingIntent.FLAG_ONE_SHOT
        )
        val notificationSoundURI =
            RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)
        val mNotificationBuilder =
            NotificationCompat.Builder(context!!, channelId)
                .setSmallIcon(R.mipmap.ic_launcher_round)
                .setContentTitle(getString(R.string.app_name))
                .setContentText(messageBody)
                .setAutoCancel(true)
                .setSound(notificationSoundURI)
                .setChannelId(channelId)
                .setContentIntent(resultIntent)
        val notificationManager =
            getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val mChannel = NotificationChannel(channelId, name, importance)
            notificationManager.createNotificationChannel(mChannel)
        }
        notificationManager.notify(0, mNotificationBuilder.build())
    }
}