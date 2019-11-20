package com.beyondthecode.shared.data.session

import com.beyondthecode.model.ConferenceDay
import com.beyondthecode.model.Session
import com.beyondthecode.model.SessionId
import com.beyondthecode.shared.data.ConferenceDataRepository
import javax.inject.Inject

/**
 * Single point of access to session data for the presentation layer
 *
 * The session data is loaded from the bootstrap file.
 * */
interface SessionRepository{
    fun getSessions(): List<Session>
    fun getSession(eventId: SessionId): Session
    fun getConferenceDays(): List<ConferenceDay>
}

class DefaultSessionRepository @Inject constructor(
    private val conferenceDataRepository: ConferenceDataRepository
) : SessionRepository{

    override fun getSessions(): List<Session> {
        return conferenceDataRepository.getOfflineConferenceData().sessions
    }

    override fun getSession(eventId: SessionId): Session {
        return conferenceDataRepository.getOfflineConferenceData().sessions.firstOrNull{
            session -> session.id == eventId
        }?: throw SessionNotFoundException()
    }

    override fun getConferenceDays(): List<ConferenceDay> {
        return conferenceDataRepository.getConferenceDays()
    }
}

class SessionNotFoundException : Throwable()