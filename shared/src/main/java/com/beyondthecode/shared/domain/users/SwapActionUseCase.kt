package com.beyondthecode.shared.domain.users

import com.beyondthecode.model.SessionId
import com.beyondthecode.shared.data.userevent.SessionAndUserEventRepository
import com.beyondthecode.shared.domain.MediatorUseCase
import com.beyondthecode.shared.domain.internal.DefaultScheduler
import com.beyondthecode.shared.result.Result
import timber.log.Timber
import javax.inject.Inject

/**
 * Sends a request to replace reservations
 * */
open class SwapActionUseCase @Inject constructor(
    private val repository: SessionAndUserEventRepository
): MediatorUseCase<SwapRequestParameters, SwapRequestAction>(){

    override fun execute(parameters: SwapRequestParameters) {
        DefaultScheduler.execute {
            try{
                val (userId, sessionId, _, toId) = parameters
                val updateResult = repository.swapReservation(userId, sessionId, toId)

                result.removeSource(updateResult)
                result.addSource(updateResult){
                    result.postValue(updateResult.value)
                }
            }catch (e: Exception){
                Timber.d("Exception in Swapping reservations")
                result.postValue(Result.Error(e))
            }
        }
    }
}

/**
 * Parameters required to process the swap reservation request
 * */
data class SwapRequestParameters(
    val userId: String,
    val fromId: SessionId,
    val fromTitle: String,
    val toId: SessionId,
    val totTitle: String
)

class SwapRequestAction