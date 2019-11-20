package com.beyondthecode.model.userdata

import com.beyondthecode.model.SessionId
import com.beyondthecode.model.reservations.ReservationRequest
import com.beyondthecode.model.reservations.ReservationRequestResult
import com.beyondthecode.model.reservations.ReservationRequest.ReservationRequestEntityAction.*
import com.beyondthecode.model.reservations.ReservationRequestResult.ReservationRequestStatus.*

/**
 * Data for a user's personalized event stored in a Firestore document.
 * */
data class UserEvent (

    //the unique ID for the event
    val id: SessionId,

    //tracks whether the user has starred the event
    val isStarred: Boolean = false,

    //tracks whether the user has provided feedback for the event
    val isReviewed: Boolean = false,

    //source of truth of the state of a reservation
    private val reservationStatus: ReservationStatus? = null,

    //stores the resuolt of a reservation request for the event
    private val reservationRequestResult: ReservationRequestResult? = null,

    /**Stores the user's latest reservation action*/
    private val reservationRequest: ReservationRequest? = null

){
    fun isPinned(): Boolean{
        return isStarred || isReserved() || isWaitListed()
    }

    /**
     * An request is pending if the result has a saved request with a different ID than the
     * latest request made by the user
     * */
    private fun isPending(): Boolean{
        //Request but no result = pending
        if(reservationRequest != null && reservationRequestResult == null) return true

        //if request and result exist they need to have different IDs to be pending.
        return reservationRequest != null &&
                reservationRequest.requestId != reservationRequestResult?.requestId
    }

    fun isReserved(): Boolean{
        return reservationStatus == ReservationStatus.RESERVED
    }

    fun isWaitListed(): Boolean{
        return reservationStatus == ReservationStatus.WAITLISTED
    }

    fun getReservationRequestResultId(): String?{
        return reservationRequestResult?.requestId
    }

    fun isDifferentRequestResult(otherId: String?): Boolean{
        return reservationRequestResult?.requestId != otherId
    }

    fun isReservedAndPendingCancel(): Boolean{
        return isReserved() && isCancelPending()
    }

    fun isWaitlistedAndPendingCancel(): Boolean{
        return isWaitListed() && isCancelPending()
    }

    fun hasRequestResulterror(): Boolean{
        return hasRequestResulterror() != null
    }

    fun requestResultError(): ReservationRequestResult.ReservationRequestStatus?{
        //The request result is garbage if there's a pending request
        if(isPending()) return null

        return when(reservationRequestResult?.requestResult){
            null -> null //If there's no request result, there's no error
            RESERVE_SUCCEEDED -> null
            RESERVE_WAITLISTED -> null
            CANCEL_SUCCEEDED -> null
            SWAP_SUCCEEDED -> null
            SWAP_WAITLISTED -> null
            else -> reservationRequestResult.requestResult
        }
    }

    fun isRequestResulterrorReserveDeniedCutoff(): Boolean{
        val e = requestResultError()
        return e == RESERVE_DENIED_CUTOFF || e == SWAP_DENIED_CUTOFF
    }

    fun isRequestResultErrorReserveDeniedClash(): Boolean{
        val e = requestResultError()
        return e == RESERVE_DENIED_CLASH || e == SWAP_DENIED_CLASH
    }

    fun isRequestResultErrorReserveDeniedUnknown(): Boolean{
        val e = requestResultError()
        return e == RESERVE_DENIED_UNKNOWN || e == SWAP_DENIED_UNKNOWN
    }

    fun isRequestResultErrorCancelDeniedCutoff(): Boolean{
        val e = requestResultError()
        return e == CANCEL_DENIED_CUTOFF || e == SWAP_DENIED_CUTOFF
    }

    fun isRequestResultErrorCancelDeniedUnknown(): Boolean{
        return requestResultError() == CANCEL_DENIED_UNKNOWN
    }

    fun isReservationPending(): Boolean{
        return (reservationStatus == ReservationStatus.NONE
                || reservationStatus == null )
                &&  isPending()
                && reservationRequest?.action == RESERVE_REQUESTED
    }

    fun isCancelPending(): Boolean{
        return reservationStatus != ReservationStatus.NONE &&
                isPending() &&
                reservationRequest?.action == CANCEL_REQUESTED
    }

    fun isLastRequestResultBySwap(): Boolean{
        val r = reservationRequestResult?.requestResult ?: return false
        return r == SWAP_SUCCEEDED
                || r == SWAP_WAITLISTED
                || r == SWAP_DENIED_CLASH
                || r == SWAP_DENIED_CUTOFF
                || r == SWAP_DENIED_UNKNOWN
    }

    fun isPreSessionNotificationRequired(): Boolean{
        return isStarred || isReserved()
    }

    /**
     * The source of truth for a reservation status
     * */
    enum class ReservationStatus{

        //The reservation was granted
        RESERVED,

        //The reservation request was granted but the user was placed on a waitlist.
        WAITLISTED,

        //The reservation request was denied because it was too close to the start of the event
        NONE;

        companion object{

            fun getIfPresent(string: String): ReservationStatus?{
                return try{
                    valueOf(string)
                }catch (e: IllegalArgumentException){
                    null
                }
            }
        }
    }
}