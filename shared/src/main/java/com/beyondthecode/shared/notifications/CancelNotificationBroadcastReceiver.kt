package com.beyondthecode.shared.notifications

import android.app.NotificationManager
import android.content.Context
import android.content.Intent
import androidx.core.content.getSystemService
import dagger.android.DaggerBroadcastReceiver
import timber.log.Timber

import javax.inject.Inject

/**
* Receives broadcast intents with information to hide notifications
* */
class CancelNotificationBroadcastReceiver :DaggerBroadcastReceiver(){

    companion object{
        const val NOTIFICATION_ID_EXTRA = "notification_id_extra"
    }

    @Inject
    lateinit var context: Context

    override fun onReceive(context: Context, intent: Intent) {
        super.onReceive(context, intent)

        val notificationId = intent.getIntExtra(NOTIFICATION_ID_EXTRA, 0)

        Timber.d("Hiding notification for $notificationId")

        val notificationManager: NotificationManager = context.getSystemService()
            ?: throw Exception("Notification manager not found.")

        notificationManager.cancel(notificationId)
    }
}