package com.beyondthecode.shared.data.userevent

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.beyondthecode.model.Session
import com.beyondthecode.model.SessionId
import com.beyondthecode.model.userdata.UserEvent
import com.beyondthecode.shared.data.document2019
import com.beyondthecode.shared.domain.internal.DefaultScheduler
import com.beyondthecode.shared.domain.users.ReservationRequestAction
import com.beyondthecode.shared.domain.users.StarUpdatedStatus
import com.beyondthecode.shared.domain.users.SwapRequestAction
import com.beyondthecode.shared.result.Result
import com.google.firebase.firestore.*
import timber.log.Timber
import javax.inject.Inject

/**
 * The data source for user data stored in firestore. It observes user data and also updates
 * stars and reservations.
 * */
class FirestoreUserEventDataSource @Inject constructor(
    val firestore: FirebaseFirestore
): UserEventDataSource{

    companion object{
        /**
         * Firestore constants.
         * */
        private const val USERS_COLLECTION = "users"
        private const val EVENTS_COLLECTION = "events"
        private const val QUEUE_COLLECTION = "queue"
        internal const val ID ="id"
        internal const val IS_ISTARRED = "isStarred"
        internal const val REVIEWED = "reviewed"

        internal const val RESERVATION_REQUEST_KEY = "reservationRequest"

        private const val RESERVE_REQ_ACTION = "RESERVE_REQUESTED"
        private const val RESERVE_CANCEL_ACTION = "CANCEL_REQUESTED"

        internal const val RESERVATION_REQUEST_ACTION_KEY = "action"
        internal const val RESERVATION_REQUEST_REQUEST_ID_KEY = "requestId"
        private const val RESERVATION_REQUEST_TIMESTAMP_KEY = "timestamp"

        private const val REQUEST_QUEUE_ACTION_KEY = "action"
        private const val REQUEST_QUEUE_SESSION_KEY = "sessionId"
        private const val REQUEST_QUEUE_REQUEST_ID_KEY = "requestId"
        private const val REQUEST_QUEUE_ACTION_RESERVE = "RESERVE"
        private const val REQUEST_QUEUE_ACTION_CANCEL = "CANCEL"
        private const val REQUEST_QUEUE_ACTION_SWAP = "SWAP"
        private const val SWAP_QUEUE_RESERVE_SESSION_ID_KEY = "reserveSessionId"
        private const val SWAP_QUEUE_CANCEL_SESSION_ID_KEY = "cancelSessionId"

        internal const val RESERVATION_RESULT_KEY = "reservationResult"
        internal const val RESERVATION_RESULT_TIME_KEY = "timestamp"
        internal const val RESERVATION_RESULT_RESULT_KEY = "requestResult"
        internal const val RESERVATION_RESULT_REQ_ID_KEY = "requestId"

        internal const val RESERVATION_STATUS_KEY = "reservationStatus"

    }

    //Null if the listener is not yet added
    private var eventsChangedListenerSubscription: ListenerRegistration? = null

    //Observable events
    private val resultEvents = MutableLiveData<UserEventsResult>()

    /**
     * Asynchronous method to get the user events.
     *
     * This method generates important messages to the user if a reservation is confirmed or
     * waitlisted
     * */
    override fun getObservableUserEvents(userId: String): LiveData<UserEventsResult> {
        if(userId.isEmpty()){
            resultEvents.postValue(UserEventsResult(emptyList()))
            return resultEvents
        }

        registerListenerForEvents(resultEvents, userId)
        return resultEvents
    }

    override fun getObservableUserEvent(
        userId: String,
        eventId: SessionId
    ): LiveData<UserEventResult> {
        if(userId.isEmpty()){
            return MutableLiveData<UserEventResult>().apply {
                UserEventResult(userEvent = null)
            }
        }
        return object : LiveData<UserEventResult>(){
            private var subscription: ListenerRegistration? = null

            private val singleEventListener: (DocumentSnapshot?, FirebaseFirestoreException?)
                    -> Unit = listener@{ snapshot, _ ->
                        snapshot ?: return@listener

                DefaultScheduler.execute {
                    Timber.d("Event changes detected on session: $eventId")

                    //If oldValue doesn't exist, it's the first run so don't generate messages.
                    val userMessage = value?.userEvent?.let{ oldValue: UserEvent ->

                        //Generate message if the reservation changed
                        if(snapshot.exists()){
                            getUserMessageFromChange(oldValue, snapshot, eventId)
                        }else{
                            null
                        }
                    }

                    val userEvent = if (snapshot.exists()){
                        parseUserEvent(snapshot)
                    }else{
                        UserEvent(id = eventId)
                    }

                    val userEventResult = UserEventResult(
                        userEvent = userEvent,
                        userEventMessage = userMessage
                    )

                    postValue(userEventResult)
                }
            }

            val eventDocument = firestore
                .document2019()
                .collection(USERS_COLLECTION)
                .document(userId)
                .collection(EVENTS_COLLECTION)
                .document(eventId)

            override fun onActive() {
                subscription = eventDocument.addSnapshotListener(singleEventListener)
            }

            override fun onInactive() {
                subscription?.remove()
            }
        }
    }

    override fun getUserEvents(userId: String): List<UserEvent> {
        //TODO: completar codigo
        return emptyList()
    }

    override fun getUserEvent(userId: String, eventId: SessionId): UserEvent? {
        //TODO: completar codigo
        return null
    }

    private fun registerListenerForEvents(
        result: MutableLiveData<UserEventsResult>,
        userId: String
    ){
        val eventsListener: (QuerySnapshot?, FirebaseFirestoreException?) -> Unit =
            listener@{ snapshot, _ ->
                snapshot ?: return@listener

                DefaultScheduler.execute {
                    Timber.d("Events changes detected: ${snapshot.documentChanges.size}")

                    //Generate important user messages, like new reservations, if any.
                    val userMessage = generateReservationChangeMsg(snapshot, result.value )
                    val userEventsResult = UserEventsResult(
                        userEvents = snapshot.documents.map{ parseUserEvent(it) },
                        userEventsMessage = userMessage
                    )
                    result.postValue(userEventsResult)
                }
            }

        val eventsCollection = firestore
            .document2019()
            .collection(USERS_COLLECTION)
            .document(userId)
            .collection(EVENTS_COLLECTION)

        eventsChangedListenerSubscription?.remove() //Remove in case userId changes.
        /*Set a value in case there're no changes to the data on start. This needs to be set to
        avoid that the upper layer LiveData detects the old data as a new data. I.e., when
        addSource was called in DefaultSessionAndUserEventRepository#getObservableUserEvents,
        the old data was considered as a new data even though it's for another user's data.
        * */
        result.postValue(null)
        eventsChangedListenerSubscription = eventsCollection.addSnapshotListener(eventsListener)
    }

    override fun clearSingleEventSubscriptions() {
        //TODO: completar codigo
    }

    override fun starEvent(
        userId: String,
        userEvent: UserEvent
    ): LiveData<Result<StarUpdatedStatus>> {

        val result = MutableLiveData<Result<StarUpdatedStatus>>()
        //TODO: completar codigo

        return result
    }

    override fun recordFeedbackSent(userId: String, userEvent: UserEvent): LiveData<Result<Unit>> {

        val result = MutableLiveData<Result<Unit>>()
        //TODO: completar codigo
        return result

    }

    override fun requestReservation(
        userId: String,
        session: Session,
        action: ReservationRequestAction
    ): LiveData<Result<ReservationRequestAction>> {

        val result = MutableLiveData<Result<ReservationRequestAction>>()
        //TODO: completar codigo

        return result
    }

    override fun swapReservation(
        userId: String,
        fromSession: Session,
        toSession: Session
    ): LiveData<Result<SwapRequestAction>> {

        val result = MutableLiveData<Result<SwapRequestAction>>()
        //TODO: completar codigo

        return result
    }




}