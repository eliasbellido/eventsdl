package com.beyondthecode.shared.data.feedback

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.beyondthecode.model.SessionId
import com.google.firebase.functions.FirebaseFunctions
import com.beyondthecode.shared.result.Result
import javax.inject.Inject

interface FeedbackEndpoint {
    fun sendFeedback(sessionId: SessionId, responses: Map<String, Int>): LiveData<Result<Unit>>
}

class DefaultFeedbackEndpoint @Inject constructor(
    private val functions: FirebaseFunctions
): FeedbackEndpoint{

    override fun sendFeedback(
        sessionId: SessionId,
        responses: Map<String, Int>
    ): LiveData<Result<Unit>> {

        val result = MutableLiveData<Result<Unit>>()
        functions
            .getHttpsCallable("sendFeedback")
            .call(hashMapOf(
                "sessionid" to sessionId,
                "responses" to responses,
                "client" to "ANDROID"
            ))
            .addOnCompleteListener { task ->
                result.postValue(if (task.isSuccessful){
                    Result.Success(Unit)
                }else{
                    Result.Error(RuntimeException(task.exception))
                })
            }
        return result
    }
}