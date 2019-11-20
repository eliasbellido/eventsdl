package com.beyondthecode.shared.notifications


import android.app.*
import android.content.Context

import android.content.Intent
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.annotation.WorkerThread
import androidx.core.app.NotificationCompat
import androidx.core.content.getSystemService
import androidx.core.net.toUri
import com.beyondthecode.model.Session
import com.beyondthecode.model.userdata.UserSession
import com.beyondthecode.shared.R
import com.beyondthecode.shared.data.prefs.SharedPreferenceStorage
import com.beyondthecode.shared.data.signin.datasources.AuthIdDataSource
import com.beyondthecode.shared.domain.internal.DefaultScheduler
import com.beyondthecode.shared.domain.sessions.LoadSessionSyncUseCase
import com.beyondthecode.shared.domain.sessions.LoadUserSessionSyncUseCase
import com.beyondthecode.shared.result.Result
import dagger.android.DaggerBroadcastReceiver
import org.threeten.bp.Instant
import timber.log.Timber
import java.util.concurrent.TimeUnit
import javax.inject.Inject

/**
 * Receives broadcast intents with information for session notifications.
 * */
class AlarmBroadcastReceiver: DaggerBroadcastReceiver(){

    @Inject
    lateinit var sharedPreferencesStorage: SharedPreferenceStorage

    @Inject
    lateinit var loadUserSession: LoadUserSessionSyncUseCase

    @Inject
    lateinit var loadSession: LoadSessionSyncUseCase

    @Inject
    lateinit var alarmManager: SessionAlarmManager

    @Inject
    lateinit var authIdDataSource: AuthIdDataSource

    override fun onReceive(context: Context, intent: Intent) {
        super.onReceive(context, intent)
        Timber.d("Alarm received")

        val sessionId = intent.getStringExtra(EXTRA_SESSION_ID) ?: return
        val userId = authIdDataSource.getUserId() ?: run {
            Timber.e("No user ID, not showing notification")
            return
        }

        val channel = intent.getStringExtra(EXTRA_NOTIFICATION_CHANNEL)
        when(channel){
            CHANNEL_ID_UPCOMING -> {
                DefaultScheduler.execute {
                    checkThenShowPreSessionNotification(context, sessionId, userId)
                }
            }
            CHANNEL_ID_FEEDBACK -> {
                DefaultScheduler.execute {
                    checkThenShowPostSessionNotification(context, sessionId, userId)
                }
            }
            else -> {
                //This shouldn't happen, but we don't want the app to crash. Only logging
                Timber.w("Broadcast with unknown channel received: $channel")
            }
        }
    }

    @WorkerThread
    private fun checkThenShowPostSessionNotification(
        context: Context,
        sessionId: String,
        userId: String
    ){
        if(!sharedPreferencesStorage.preferToReceiveNotifications){
            Timber.d("User disabled notifications, not showing")
            return
        }

        Timber.d("Showing post session notification for $sessionId, user $userId")

        val userEvent: Result<UserSession>? = getUserEvent(userId, sessionId)
        //Only notify the user when the user Event is retrieved successfully because if the user
        //is offline, they can¿t send  the feedback anyway
        val userSession = (userEvent as? Result.Success<UserSession>)?.data ?: return
        val now = Instant.now()

        if(userSession.isPostSessionNotificationRequired() &&
                now.isAfter(userSession.session.endTime.toInstant())){
            try{
                showPostSessionNotification(context, userEvent.data.session)
            }catch(ex:Exception){
                Timber.e(ex)
                return
            }
        }
    }

    @WorkerThread
    private fun showPostSessionNotification(context: Context, session: Session): Int{
        val notificationManager: NotificationManager = context.getSystemService() ?: throw Exception("Notification Manager not found.")

        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
            makeNotificationChannelForPostSession(context, notificationManager)
        }

        val intent = Intent(
            Intent.ACTION_VIEW,
            "eventsdl://sessions?$QUERY_SESSION_ID=${session.id}".toUri()
        ).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK
            putExtra(EXTRA_SHOW_RATE_SESSION_FLAG, true)
        }

        //Create the TaskStackBuilder
        val resultPendingIntent: PendingIntent? = TaskStackBuilder.create(context)
            //Add the intent which inflates the back stack
            .addNextIntentWithParentStack(intent)
            //Get the PendingIntent containing the entire back stack
            .getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT)

        val notification = NotificationCompat.Builder(context, CHANNEL_ID_FEEDBACK)
            .setContentTitle(session.title)
            .setContentText(context.getString(R.string.please_rate_session))
            .setSmallIcon(R.drawable.ic_notification_io_logo)
            .setContentIntent(resultPendingIntent)
            .setTimeoutAfter(TimeUnit.MINUTES.toMillis(20)) //Backup (cancelled with receiver)
            .setAutoCancel(true)
            .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
            .build()

        val notificationId = session.id.hashCode()
        notificationManager.notify(notificationId, notification)
        return notificationId

    }

    @WorkerThread
    private fun checkThenShowPreSessionNotification(
        context: Context,
        sessionId: String,
        userId: String
    ){
        if(!sharedPreferencesStorage.preferToReceiveNotifications){
            Timber.d("User disabled notifications, not showing")
            return
        }

        Timber.d("Showing pre session notification for $sessionId, user $userId")

        val userEvent: Result<UserSession>? = getUserEvent(userId, sessionId)
        //Don't notify if for some reason the event is no longer starred or reserved.
        if(userEvent is Result.Success){
            val event = userEvent.data.userEvent
            if(event.isPreSessionNotificationRequired() &&
                    isSessionCurrent(userEvent.data.session)){
                try{
                    val notificationId = showPreSessionNotification(context, userEvent.data.session)
                    //Dismiss in any case
                    alarmManager.dismissNotificationInFiveMinutes(notificationId)

                }catch (ex: Exception){
                    Timber.e(ex)
                    return
                }
            }
        }else{
            //There was no way to get UserEvent info, notify in case of connectivity error.
            notifyWithoutUserEvent(sessionId, context)
        }
    }

    @WorkerThread
    private fun showPreSessionNotification(context: Context, session: Session): Int{
        val notificationManager: NotificationManager = context.getSystemService() ?: throw Exception("Notification Manager not found")

        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
            makeNotificationChannelForPreSession(context, notificationManager)
        }
        val intent = Intent(
            Intent.ACTION_VIEW,
            "eventsdl://sessions?$QUERY_SESSION_ID=${session.id}".toUri()
        )
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK

        //Create the TaskStackBuilder
        val resultPendingIntent: PendingIntent? = TaskStackBuilder.create(context)
             //Add the intent which inflates the back stack
            .addNextIntentWithParentStack(intent)
            //Get the pendingIntent containing the entire back stack
            .getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT)

        val notification = NotificationCompat.Builder(context, CHANNEL_ID_UPCOMING)
            .setContentTitle(session.title)
            .setContentText(context.getString(R.string.starting_soon))
            .setSmallIcon(R.drawable.ic_notification_io_logo)
            .setContentIntent(resultPendingIntent)
            .setTimeoutAfter(TimeUnit.MINUTES.toMillis(10)) //Backup (cancelled with receiver)
            .setAutoCancel(true)
            .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
            .build()

        val notificationId = session.id.hashCode()
        notificationManager.notify(notificationId, notification)
        return notificationId
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun makeNotificationChannelForPostSession(
        context: Context,
        notificationManager: NotificationManager
    ){
        notificationManager.createNotificationChannel(
            NotificationChannel(
                CHANNEL_ID_FEEDBACK,
                context.getString(R.string.session_feedback_notifications),
                NotificationManager.IMPORTANCE_LOW
            ).apply { lockscreenVisibility = Notification.VISIBILITY_PUBLIC }
        )
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun makeNotificationChannelForPreSession(
        context: Context,
        notificationManager: NotificationManager
    ){
        notificationManager.createNotificationChannel(
            NotificationChannel(
                CHANNEL_ID_UPCOMING,
                context.getString(R.string.session_notifications),
                NotificationManager.IMPORTANCE_DEFAULT
            ).apply { lockscreenVisibility = Notification.VISIBILITY_PUBLIC }
        )
    }

    private fun getUserEvent(userId: String, sessionId: String): Result<UserSession>?{
        return try{
            loadUserSession.executeNow(userId to sessionId)
        }catch (ex: Exception){
            Timber.e("""Session notification is set, however there was an error confirming
                |that the event is still starred. Showing notification""".trimMargin())
            null
        }
    }

    private fun notifyWithoutUserEvent(sessionId: String, context: Context){
        val session = loadSession.executeNow(sessionId)
        if(session is Result.Success){
            val notificationId = showPreSessionNotification(context, session.data)
            alarmManager.dismissNotificationInFiveMinutes(notificationId)
        }else{
            Timber.e("Session couldn't be loaded for notification")
        }
    }

    private fun isSessionCurrent(session: Session): Boolean{
        return session.startTime.toInstant().isAfter(Instant.now())
    }

    companion object{
        const val EXTRA_NOTIFICATION_CHANNEL = "notification_channel"
        const val EXTRA_SESSION_ID = "user_event_extra"

        /**If this flag is set to true in session detail, the show rate dialog should be opened*/
        const val EXTRA_SHOW_RATE_SESSION_FLAG = "show_rate_session_extra"

        const val QUERY_SESSION_ID = "session_id"
        const val CHANNEL_ID_UPCOMING = "upcoming_channel_id"
        const val CHANNEL_ID_FEEDBACK = "feedback_channel_id"
    }
}