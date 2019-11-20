package com.beyondthecode.shared.data.userevent

import com.beyondthecode.model.reservations.ReservationRequest
import com.beyondthecode.model.reservations.ReservationRequestResult
import com.beyondthecode.model.userdata.UserEvent
import com.google.firebase.firestore.DocumentSnapshot

import com.beyondthecode.shared.data.userevent.FirestoreUserEventDataSource.Companion.RESERVATION_REQUEST_ACTION_KEY
import com.beyondthecode.shared.data.userevent.FirestoreUserEventDataSource.Companion.RESERVATION_REQUEST_KEY
import com.beyondthecode.shared.data.userevent.FirestoreUserEventDataSource.Companion.RESERVATION_REQUEST_REQUEST_ID_KEY
import com.beyondthecode.shared.data.userevent.FirestoreUserEventDataSource.Companion.RESERVATION_RESULT_KEY
import com.beyondthecode.shared.data.userevent.FirestoreUserEventDataSource.Companion.RESERVATION_RESULT_REQ_ID_KEY
import com.beyondthecode.shared.data.userevent.FirestoreUserEventDataSource.Companion.RESERVATION_RESULT_RESULT_KEY
import com.beyondthecode.shared.data.userevent.FirestoreUserEventDataSource.Companion.RESERVATION_RESULT_TIME_KEY

import com.beyondthecode.shared.data.userevent.FirestoreUserEventDataSource.Companion.RESERVATION_STATUS_KEY
import timber.log.Timber

/**
 * Parse a user event that includes information about the reservation status.
 * */
fun parseUserEvent(snapshot: DocumentSnapshot): UserEvent{

    val reservationRequestResult: ReservationRequestResult? = generateReservationRequestResult(snapshot)

    val reservationRequest = parseReservationRequest(snapshot)

    val reservationStatus = (snapshot[RESERVATION_STATUS_KEY] as? String)?.let {
        UserEvent.ReservationStatus.getIfPresent(it)
    }

    return UserEvent(
        id = snapshot.id,
        reservationRequestResult = reservationRequestResult,
        reservationStatus = reservationStatus,
        isStarred = snapshot[FirestoreUserEventDataSource.IS_ISTARRED] as? Boolean ?: false,
        reservationRequest = reservationRequest,
        isReviewed = snapshot[FirestoreUserEventDataSource.REVIEWED] as? Boolean ?: false
    )
}

/**
 * Parse the result of a reservation request.
 * */
private fun generateReservationRequestResult(snapshot: DocumentSnapshot): ReservationRequestResult? {

    (snapshot[RESERVATION_RESULT_KEY] as? Map<*, *>)?.let { reservation ->
        val requestResult = (reservation[RESERVATION_RESULT_RESULT_KEY] as? String)
            ?.let { ReservationRequestResult.ReservationRequestStatus.getIfPresent(it) }

        val requestId = (reservation[RESERVATION_RESULT_REQ_ID_KEY] as? String)

        val timestamp = reservation[RESERVATION_RESULT_TIME_KEY] as? Long ?: -1

        //Mandatory fields or fail:
        if (requestResult == null || requestId == null){
            Timber.e("Error parsing reservation request result: some fields null")
            return null
        }

        return ReservationRequestResult(
            requestResult = requestResult,
            requestId = requestId,
            timestamp = timestamp

        )
    }
    //if there's no reservation:
    return null
}

/**
 * Parse the reservation request.
 * */
private fun parseReservationRequest(snapshot: DocumentSnapshot): ReservationRequest? {

    (snapshot[RESERVATION_REQUEST_KEY] as? Map<*, *>)?.let { request ->
        val action = (request[RESERVATION_REQUEST_ACTION_KEY] as? String)?.let {
            ReservationRequest.ReservationRequestEntityAction.getIfPresent(it)
        }

        val requestId = (request[RESERVATION_REQUEST_REQUEST_ID_KEY] as? String)

        //Mandatory fields or fail:
        if(action == null || requestId == null){
            Timber.e("Error parsing reservation request from Firestore")
            return null
        }

        return ReservationRequest(action, requestId)
    }
    //if there's no reservation request:
    return null
}