package com.beyondthecode.shared.domain.sessions

import androidx.lifecycle.LiveData
import com.beyondthecode.model.SessionId
import com.beyondthecode.model.userdata.UserSession
import com.beyondthecode.shared.data.userevent.DefaultSessionAndUserEventRepository
import com.beyondthecode.shared.data.userevent.UserEventMessage
import com.beyondthecode.shared.domain.MediatorUseCase
import com.beyondthecode.shared.domain.internal.DefaultScheduler
import com.beyondthecode.shared.result.Result
import javax.inject.Inject

open class LoadUserSessionUseCase @Inject constructor(
    private val userEventRepository: DefaultSessionAndUserEventRepository
): MediatorUseCase<Pair<String?, SessionId>, LoadUserSessionUseCaseResult>(){

    private var userSession: LiveData<Result<LoadUserSessionUseCaseResult>>? = null

    override fun execute(parameters: Pair<String?, SessionId>) {
        val (userId, eventId) = parameters

        //Remove old data sources
        clearSources()

        //Fetch an observable of the data
        val newUserSession = userEventRepository.getObservableUserEvent(userId, eventId)

        //Post new values to the result object
        result.addSource(newUserSession){
            DefaultScheduler.execute {
                when(it){
                    is Result.Success -> {
                        val useCaseResult = LoadUserSessionUseCaseResult(
                            userSession = it.data.userSession,
                            userMessage = it.data.userMessage
                        )
                        result.postValue(Result.Success(useCaseResult))
                    }
                    is Result.Error -> {
                        result.postValue(it)
                    }
                }
            }
        }
        //Save a reference to the observable for later cleaning of sources
        userSession = newUserSession
    }

    fun onCleared(){
        clearSources()
    }

    private fun clearSources(){
        userSession?.let {
            result.removeSource(it)
        }
        result.value = null
    }

}

data class LoadUserSessionUseCaseResult(
    val userSession: UserSession,

    /** A message to show to the user with importants changes like reservation confirmations*/
    val userMessage: UserEventMessage? = null
)