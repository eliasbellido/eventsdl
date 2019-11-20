package com.beyondthecode.shared.data.userevent

import androidx.annotation.VisibleForTesting
import com.beyondthecode.model.userdata.UserEvent
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.QuerySnapshot

/**
 * Go through all the changes and generate user messages in ase there're reservation changes.
 * */
//TODO: falta terminar acá
fun generateReservationChangeMsg(
    snapshot: QuerySnapshot,
    oldValue: UserEventsResult?
): UserEventMessage?{

    //if oldValue doesn't exist, it's the first run so don't generate messages.
    if(oldValue == null) return null

    var userMessage: UserEventMessage? ? = null

    snapshot.documentChanges.forEach { change ->

        val changedId: String = change.document.data[FirestoreUserEventDataSource.ID]
                as? String ?: return null

        val eventOldValue = oldValue.userEvents.firstOrNull{ it.id == changedId } ?: return null

        val newMessage = getUserMessageFromChange(eventOldValue, change.document, changedId)

        //If there're multiple messages, show just one according to order in enum.
        if (newMessage != null){
            val userMessageSnapshot = userMessage
            //Order in enum is definition order
            if(userMessageSnapshot == null || newMessage.type < userMessageSnapshot.type){
                userMessage = newMessage
            }
        }
    }
    return userMessage
}

/**
 * Given a change in a document, generate a user message to indicate a change in reservations.
 * */
fun getUserMessageFromChange(
    oldValue: UserEvent,
    documentSnapshot: DocumentSnapshot,
    changeId: String
): UserEventMessage? {

    //Get the new state
    val newState = parseUserEvent(documentSnapshot)

    return compareOldAndNewUserEvents(oldValue, newState, changeId)
}

@VisibleForTesting
fun compareOldAndNewUserEvents(
    oldState: UserEvent,
    newState: UserEvent,
    changedId: String
): UserEventMessage? {
    //TODO: completar codigo, revisar el repo de iosched
    return null
}


data class UserEventMessage(
    val type: UserEventMessageChangeType,
    val sessionId: String? = null,
    val changeRequestId: String? = null
)

/**
 * Enum of messages notified to the end user.
 * Need to be ordered by importance
 * (e.g. CHANGE_IN_RESERVATIONS is more important than CHANGES_IN_WAITLIST)
 * */
enum class UserEventMessageChangeType{
    CHANGES_IN_RESERVATIONS,
    RESERVATIONS_REPLACED,
    CHANGES_IN_WAITLIST,
    RESERVATION_CANCELED,
    WAITLIST_CANCELED,
    RESERVATION_DENIED_CUTOFF,
    RESERVATION_DENIED_CLASH,
    RESERVATION_DENIED_UNKNOWN,
    CANCELLATION_DENIED_CUTOFF,
    CANCELLATION_DENIED_UNKNOWN
}