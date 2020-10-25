package com.lolo.io.onelist

import android.app.NotificationManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import androidx.core.app.NotificationCompat

const val notificationId = 1
const val channelId = "channel1"
const val titleExtra = "titleExtra"
const val messageExtra = "Message"


class Notification : BroadcastReceiver(){
    override fun onReceive(context: Context?, intent: Intent?) {
        val notification = context?.let {
            NotificationCompat.Builder(it, channelId)
                .setSmallIcon(R.drawable.ic_onelist_icon)
                .setContentTitle(intent?.getStringExtra(titleExtra))
                .setContentText(intent?.getStringExtra(messageExtra))
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .build()
        }

        val manager = context?.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        manager.notify(notificationId,notification)
    }

}