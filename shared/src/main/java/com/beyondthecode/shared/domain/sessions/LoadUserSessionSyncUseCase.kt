package com.beyondthecode.shared.domain.sessions

import com.beyondthecode.model.SessionId
import com.beyondthecode.model.userdata.UserSession
import com.beyondthecode.shared.data.userevent.DefaultSessionAndUserEventRepository
import com.beyondthecode.shared.domain.UseCase
import javax.inject.Inject

/**
 * A [UseCase] that returns the [UserSession]s for a user
 * */
class LoadUserSessionSyncUseCase @Inject constructor(
    private val userEventRepository: DefaultSessionAndUserEventRepository
): UseCase<Pair<String, SessionId>, UserSession>(){

    override fun execute(parameters: Pair<String, SessionId>): UserSession {
        val(userId, eventId) = parameters

        return userEventRepository.getUserSession(userId, eventId)
    }
}