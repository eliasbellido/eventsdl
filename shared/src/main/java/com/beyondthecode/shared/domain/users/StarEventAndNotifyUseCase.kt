package com.beyondthecode.shared.domain.users

import com.beyondthecode.model.userdata.UserSession
import com.beyondthecode.shared.data.userevent.SessionAndUserEventRepository
import com.beyondthecode.shared.domain.MediatorUseCase
import com.beyondthecode.shared.domain.sessions.StarReserveNotificationAlarmUpdater
import com.beyondthecode.shared.result.Result
import java.lang.Exception
import javax.inject.Inject

open class StarEventAndNotifyUseCase @Inject constructor(
    private val repository: SessionAndUserEventRepository,
    private val alarmUpdater: StarReserveNotificationAlarmUpdater
) : MediatorUseCase<StarEventParameter, StarUpdatedStatus>(){

    override fun execute(parameters: StarEventParameter) {
        val updateResult = try {
            repository.starEvent(parameters.userid, parameters.userSession.userEvent)
        }catch (e: Exception){
            result.postValue(Result.Error(e))
            return
        }
        //Avoid duplicating sources and trigger an update on the LiveData from the base class
        result.removeSource(updateResult)
        result.addSource(updateResult){
            alarmUpdater.updateSession(
                parameters.userSession,
                parameters.userSession.userEvent.isPreSessionNotificationRequired()
            )
            result.postValue(updateResult.value)
        }
    }
}

data class StarEventParameter(
    val userid: String,
    val userSession: UserSession
)

enum class StarUpdatedStatus{
    STARRED,
    UNSTARRED;
}