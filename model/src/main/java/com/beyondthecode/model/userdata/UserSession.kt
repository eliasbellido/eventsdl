package com.beyondthecode.model.userdata

import com.beyondthecode.model.Session
import com.beyondthecode.model.SessionType

/**
 * Wrapper class to hold the [Session] and associating [UserEvent]*/
data class UserSession (
    val session: Session,
    val userEvent: UserEvent
){

    fun isPostSessionNotificationRequired(): Boolean{
        return userEvent.isReserved() &&
                !userEvent.isReviewed &&
                session.type == SessionType.SESSION
    }
}