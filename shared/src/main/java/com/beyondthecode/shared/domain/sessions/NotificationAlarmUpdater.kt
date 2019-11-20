package com.beyondthecode.shared.domain.sessions

import androidx.lifecycle.LiveData
import com.beyondthecode.model.userdata.UserSession
import com.beyondthecode.shared.data.userevent.ObservableUserEvents
import com.beyondthecode.shared.data.userevent.SessionAndUserEventRepository
import com.beyondthecode.shared.domain.internal.DefaultScheduler
import com.beyondthecode.shared.notifications.SessionAlarmManager
import com.beyondthecode.shared.result.Result
import timber.log.Timber
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Sets a notification for each session that is starred or reserved by the user.
 * */
@Singleton
class NotificationAlarmUpdater @Inject constructor(
    private val alarmManager: SessionAlarmManager,
    private val repository: SessionAndUserEventRepository
){
    var observer: ((Result<ObservableUserEvents>) -> Unit)? = null
    var userEvents: LiveData<Result<ObservableUserEvents>>? = null

    var cancelObserver: ((Result<ObservableUserEvents>) -> Unit)? = null
    var cancelUserEvents: LiveData<Result<ObservableUserEvents>>? = null

    fun updateAll(userId: String){
        //Go through every UserSession and make sure the alarm is set for the notification.

        val newObserver = { sessions: Result<ObservableUserEvents> ->
            when(sessions){
                is Result.Success -> processEvents(userId, sessions.data)
                is Error -> Timber.e(sessions.cause)
            }
        }

        userEvents = repository.getObservableUserEvents(userId).apply {
            observeForever(newObserver)
        }

        observer = newObserver
    }

    private fun processEvents(
        userId: String,
        sessions: ObservableUserEvents
    ){
        Timber.d("Setting all the alarms for user $userId")
        val startWork = System.currentTimeMillis()

        sessions.userSessions.forEach { session: UserSession ->
            if(session.userEvent.isStarred || session.userEvent.isReserved()){
                alarmManager.setAlarmForSession(session)
            }
        }
        Timber.d("Work finished in ${System.currentTimeMillis() - startWork} ms")
        clear()
    }

    fun clear(){

        observer?.let {
            userEvents?.removeObserver(it)
        }

        cancelObserver?.let{
            cancelUserEvents?.removeObserver(it)
        }

        observer = null
        cancelObserver = null
        userEvents = null
        cancelUserEvents = null
    }

    fun cancelAll(){

        val newObserver = { sessions: Result<ObservableUserEvents> ->
            when(sessions){
                is Result.Success -> DefaultScheduler.execute {
                    cancelAllSessions(sessions.data)
                }
                is Error -> Timber.e(sessions.cause)
            }
        }

        repository.getObservableUserEvents(null).observeForever(newObserver)
        cancelObserver = newObserver
        clear()

    }

    private fun cancelAllSessions(sessions: ObservableUserEvents){
        Timber.d("Cancelling all the alarms")
        sessions.userSessions.forEach {
            alarmManager.cancelAlarmForSession(it.session.id)
        }
    }
}

@Singleton
open class StarReserveNotificationAlarmUpdater @Inject constructor(
    private val alarmManager: SessionAlarmManager
){
    open fun updateSession(
        userSession: UserSession,
        requestNotification: Boolean
    ){
        if(requestNotification){
            alarmManager.setAlarmForSession(userSession)
        }else{
            alarmManager.cancelAlarmForSession(userSession.session.id)
        }
    }
}