package com.beyondthecode.shared.data.userevent

import androidx.lifecycle.LiveData
import com.beyondthecode.model.Session
import com.beyondthecode.model.SessionId
import com.beyondthecode.model.userdata.UserEvent
import com.beyondthecode.shared.domain.users.ReservationRequestAction
import com.beyondthecode.shared.domain.users.StarUpdatedStatus
import com.beyondthecode.shared.domain.users.SwapRequestAction
import com.beyondthecode.shared.result.Result

interface UserEventDataSource{

    fun getObservableUserEvents(userId: String): LiveData<UserEventsResult>

    fun getObservableUserEvent(userId: String, eventId: SessionId): LiveData<UserEventResult>

    fun getUserEvents(userId: String): List<UserEvent>

    fun getUserEvent(userId: String, eventId: SessionId): UserEvent?

    /**
     * Toggle the isStarred status for an event.
     *
     * @param userId the userId ([FirebaseUser#uid]) of the current logged in user
     * @param userEvent the [UserEvent], which isStarred is going to be the updated status
     * @return the LiveData that represents the status of the star operation.
     * */
    fun starEvent(userId: String, userEvent: UserEvent): LiveData<Result<StarUpdatedStatus>>

    fun recordFeedbackSent(
        userId: String,
        userEvent: UserEvent
    ): LiveData<Result<Unit>>

    fun requestReservation(
        userId: String,
        session: Session,
        action: ReservationRequestAction
    ): LiveData<Result<ReservationRequestAction>>

    fun swapReservation(
        userId: String,
        fromSession: Session,
        toSession: Session
    ): LiveData<Result<SwapRequestAction>>

    fun clearSingleEventSubscriptions()
}

data class UserEventsResult(
    val userEvents: List<UserEvent>,
    val userEventsMessage: UserEventMessage? = null
)

data class UserEventResult(
    val userEvent: UserEvent?,
    val userEventMessage: UserEventMessage? = null
)