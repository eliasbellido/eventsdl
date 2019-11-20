package com.beyondthecode.shared.data.signin.datasources

import androidx.lifecycle.LiveData
import com.beyondthecode.shared.data.signin.AuthenticatedUserInfoBasic
import com.beyondthecode.shared.result.Result

/**
 * Listens to an Authentication state data source that emits updates on the current user.
 * */
interface AuthStateUserDataSource {

    /**
     * Listens to changes in the authentication-related user info.
     * */
    fun startListening()

    /**
     * Returns an observable of the [AuthenticatedUserInfoBasic].
     * */
    fun getBasicUserInfo(): LiveData<Result<AuthenticatedUserInfoBasic?>>

    /**
     * Call this method to lear listeners to avoid leaks
     * */
    fun clearListener()
}