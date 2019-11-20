package com.beyondthecode.shared.data.userevent

import androidx.annotation.WorkerThread
import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.MutableLiveData
import com.beyondthecode.model.ConferenceDay
import com.beyondthecode.model.Session
import com.beyondthecode.model.SessionId
import com.beyondthecode.model.userdata.UserEvent
import com.beyondthecode.model.userdata.UserSession
import com.beyondthecode.shared.data.session.SessionRepository
import com.beyondthecode.shared.domain.internal.DefaultScheduler
import com.beyondthecode.shared.domain.sessions.LoadUserSessionUseCaseResult
import com.beyondthecode.shared.domain.users.ReservationRequestAction
import com.beyondthecode.shared.domain.users.StarUpdatedStatus
import com.beyondthecode.shared.domain.users.SwapRequestAction
import com.beyondthecode.shared.domain.users.SwapRequestParameters
import com.beyondthecode.shared.result.Result
import timber.log.Timber
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Single point of access to user events data associated with a user for the presentation layer
 * */
@Singleton
open class DefaultSessionAndUserEventRepository @Inject constructor(
    private val userEventDataSource: UserEventDataSource,
    private val sessionRepository: SessionRepository
): SessionAndUserEventRepository{
    private val sessionsResult = MediatorLiveData<Result<ObservableUserEvents>>()

    //Keep a reference to the observable for a single event (from the details screen) so we can
    // stop observing when done.
    private var observableUserEvent: LiveData<UserEventResult>? = null

    override fun getObservableUserEvents(userId: String?): LiveData<Result<ObservableUserEvents>> {
        //If there's no logged-in user, return the map with null UserEvents
        if(userId == null){
            DefaultScheduler.execute {
                Timber.d("""EventRepository: No user logged in,
                                        |returning sessions without user events.""".trimMargin())
                val allSessions = sessionRepository.getSessions()
                val userSessions = mergeUserDataAndSessions(null, allSessions)
                sessionsResult.postValue(
                    Result.Success(
                        ObservableUserEvents(
                            userSessions = userSessions
                        )
                    )
                )
            }
            return sessionsResult
        }

        //Observes the user events and merges them with session data.
        val observableUserEvents = userEventDataSource.getObservableUserEvents(userId)
        sessionsResult.removeSource(observableUserEvents)
        sessionsResult.addSource(observableUserEvents){ userEvents ->
            DefaultScheduler.execute {
                try{
                    //not update the result when userEvents is null, otherwise the count of the
                    //filtered result in the use case is going to be 0, that results blur when
                    //the pinned item switch is toggled.
                    userEvents ?: return@execute

                    Timber.d("""EventRepository: Received ${userEvents.userEvents.size}
                        |user events changes""".trimMargin())

                    //Get the sessions, synchronously
                    val allSessions = sessionRepository.getSessions()
                    val userSessions = mergeUserDataAndSessions(userEvents, allSessions)

                    //TODO expose user events messages separately
                    val userEventsMessageSession = allSessions.firstOrNull{
                        it.id == userEvents.userEventsMessage?.sessionId
                    }
                    sessionsResult.postValue(
                        Result.Success(
                            ObservableUserEvents(
                                userSessions = userSessions,
                                userMessage = userEvents.userEventsMessage,
                                userMessageSession = userEventsMessageSession
                            )
                        )
                    )
                }catch (e: Exception){
                    sessionsResult.postValue(Result.Error(e))
                }
            }
        }
        return sessionsResult
    }

    override fun getObservableUserEvent(
        userId: String?,
        eventId: SessionId
    ): LiveData<Result<LoadUserSessionUseCaseResult>> {
        val sessionResult = MediatorLiveData<Result<LoadUserSessionUseCaseResult>>()

        //if there's no logged-in user, return the session with a null UserEvent
        if(userId == null){
            DefaultScheduler.execute {
                Timber.d("EventRepository: No user logged in, returning session without user event")

                val session = sessionRepository.getSession(eventId)

                sessionResult.postValue(
                    Result.Success(
                        LoadUserSessionUseCaseResult(
                            userSession = UserSession(session, createDefaultUserEvent(session)),
                            userMessage = null

                        )
                    )

                )
            }
            return sessionResult
        }

        //Observes the user events and merges them with session data.
        val newObservableUserEvent = userEventDataSource.getObservableUserEvent(userId, eventId)
        sessionResult.removeSource(newObservableUserEvent) //Avoid multiple subscriptions
        sessionResult.value = null //Prevent old data from being emitted
        sessionResult.addSource(newObservableUserEvent){ userEventResult ->
            if(userEventResult?.userEvent == null){
                return@addSource
            }
            DefaultScheduler.execute {
                try{
                    Timber.d("EventRepository: Received user event changes")
                    //Get the session, synchronously
                    val event = sessionRepository.getSession(eventId)

                    //Merges session with user data and emits the result
                    val userSession = UserSession(
                        event,
                        userEventResult.userEvent
                    )
                    sessionResult.postValue(
                        Result.Success(
                            LoadUserSessionUseCaseResult(
                                userSession = userSession,
                                userMessage = userEventResult.userEventMessage
                            )
                        )
                    )
                }catch (e: Exception){
                    sessionResult.postValue(Result.Error(e))
                }
            }
        }
        this.observableUserEvent = newObservableUserEvent
        return sessionResult
    }

    override fun getUserEvents(userId: String?): List<UserEvent> {
        return userEventDataSource.getUserEvents(userId ?: "")
    }

    override fun getUserSession(userId: String, sessionId: SessionId): UserSession {
        val session = sessionRepository.getSession(sessionId)
        val userEvent = userEventDataSource.getUserEvent(userId, sessionId) ?: throw Exception("UserEvent not found")

        return UserSession(
            session = session,
            userEvent = userEvent
        )
    }

    override fun starEvent(
        userId: String,
        userEvent: UserEvent
    ): LiveData<Result<StarUpdatedStatus>> {
        return userEventDataSource.starEvent(userId, userEvent)
    }

    override fun recordFeedbackSent(userId: String, userEvent: UserEvent): LiveData<Result<Unit>> {
        return userEventDataSource.recordFeedbackSent(userId, userEvent)
    }

    override fun changeReservation(
        userId: String,
        sessionId: SessionId,
        action: ReservationRequestAction
    ): LiveData<Result<ReservationRequestAction>> {
        val sessions = sessionRepository.getSessions().associateBy { it.id }
        val userEvents = getUserEvents(userId)
        val session = sessionRepository.getSession(sessionId)
        val overlappingId = findOverlappingReservationId(session, action, sessions, userEvents)
        if(overlappingId != null){
            //If there's already an overlapping reservation, return the result as
            //SwapAction is needed
            val result = MutableLiveData<Result<ReservationRequestAction>>()
            val overlappingSession = sessionRepository.getSession(overlappingId)
            Timber.d(""""User is trying to reserve a session that overlaps with the
                |session id: $overlappingId, title ${overlappingSession.title}""".trimMargin())
            result.postValue(
                Result.Success(
                    ReservationRequestAction.SwapAction(
                        SwapRequestParameters(
                            userId,
                            fromId = overlappingId,
                            fromTitle = overlappingSession.title,
                            toId = sessionId,
                            totTitle = session.title
                        )
                    )
                )
            )
            return result
        }
        return userEventDataSource.requestReservation(userId, session, action)
    }

    override fun swapReservation(
        userId: String,
        fromId: SessionId,
        toId: SessionId
    ): LiveData<Result<SwapRequestAction>> {
        val toSession = sessionRepository.getSession(toId)
        val fromSession = sessionRepository.getSession(fromId)
        return userEventDataSource.swapReservation(userId, fromSession, toSession)
    }

    private fun findOverlappingReservationId(
        session: Session,
        action: ReservationRequestAction,
        sessions: Map<String, Session>,
        userEvents: List<UserEvent>
    ): String?{
        if(action !is ReservationRequestAction.RequestAction) return null
        val overlappingUserEvent = userEvents.find{
            sessions[it.id]?.isOverlapping(session) == true &&
                    (it.isReserved() || it.isWaitListed())
        }
        return overlappingUserEvent?.id

    }

    private fun createDefaultUserEvent(session: Session): UserEvent{
        return UserEvent(id = session.id)
    }

    @WorkerThread
    private fun mergeUserDataAndSessions(
        userData: UserEventsResult?,
        allSessions: List<Session>
    ): List<UserSession>{
        //if there's no logged-in user, return the map with null userevents
        if(userData == null){
            return allSessions.map { UserSession(it, createDefaultUserEvent(it)) }
        }
        val (userEvents, _) = userData
        val eventIdToUserEvent = userEvents.associateBy { it.id }
        return allSessions.map{
            UserSession(it, eventIdToUserEvent[it.id] ?: createDefaultUserEvent(it))
        }
    }

    override fun clearSingleEventSubscriptions() {
        //The UserEvent data source can stop observing usre data
        userEventDataSource.clearSingleEventSubscriptions()
    }

    override fun getConferenceDays(): List<ConferenceDay> = sessionRepository.getConferenceDays()
}

interface SessionAndUserEventRepository{

    //TODO: Repository should not have source dependency on UseCase result
    fun getObservableUserEvents(
        userId: String?
    ): LiveData<Result<ObservableUserEvents>>

    //TODO: Repository should not have source dependency on UseCase result
    fun getObservableUserEvent(
        userId: String?,
        eventId: SessionId
    ): LiveData<Result<LoadUserSessionUseCaseResult>>

    fun getUserEvents(userId: String?): List<UserEvent>

    fun changeReservation(
        userId: String,
        sessionId: SessionId,
        action: ReservationRequestAction
    ): LiveData<Result<ReservationRequestAction>>

    fun swapReservation(
        userId: String,
        fromId: SessionId,
        toId: SessionId
    ): LiveData<Result<SwapRequestAction>>

    fun starEvent(userId: String, userEvent: UserEvent): LiveData<Result<StarUpdatedStatus>>

    fun recordFeedbackSent(
        userId: String,
        userEvent: UserEvent
    ): LiveData<Result<Unit>>

    fun clearSingleEventSubscriptions()

    fun getConferenceDays(): List<ConferenceDay>

    fun getUserSession(userId: String, sessionId: SessionId): UserSession
}

data class ObservableUserEvents(
    val userSessions: List<UserSession>,

    /** A message to show to the user with important changes like reservation confirmations*/
    val userMessage: UserEventMessage? = null,

    /** The session the user message is about, if any.*/
    val userMessageSession: Session? = null
)