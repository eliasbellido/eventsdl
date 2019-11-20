package com.beyondthecode.shared.domain.users

import com.beyondthecode.shared.data.userevent.SessionAndUserEventRepository
import javax.inject.Inject

/**
 * Sends a request to reserve or cancel a reservation for a session.
 * */
open class ReservationActionUseCase @Inject constructor(
    private val repository: SessionAndUserEventRepository
 //   private val alarmUpdater: StarReserveNotificationAlarmUpdater
)

sealed class ReservationRequestAction{
    class RequestAction : ReservationRequestAction(){
        override fun equals(other: Any?): Boolean {
            return other is RequestAction
        }

        @Suppress("redundant")
        override fun hashCode(): Int {
            return super.hashCode()
        }
    }

    class CancelAction : ReservationRequestAction(){
        override fun equals(other: Any?): Boolean {
            return other is CancelAction
        }

        @Suppress("redundant")
        override fun hashCode(): Int {
            return super.hashCode()
        }
    }

    /**
     * The action when the user is trying to reserve a session, but there is already an overlapping
     * reservation
     * */
    class SwapAction(val parameters: SwapRequestParameters) : ReservationRequestAction()
}