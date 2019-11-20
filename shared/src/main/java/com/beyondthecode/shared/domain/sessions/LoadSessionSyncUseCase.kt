package com.beyondthecode.shared.domain.sessions

import com.beyondthecode.model.Session
import com.beyondthecode.model.SessionId
import com.beyondthecode.shared.data.session.SessionRepository
import com.beyondthecode.shared.domain.UseCase
import javax.inject.Inject

open class LoadSessionSyncUseCase @Inject constructor(
    private val repository: SessionRepository
): UseCase<SessionId, Session>(){

    override fun execute(parameters: SessionId): Session {
        return repository.getSession(parameters)
    }
}