package com.beyondthecode.eventsdl.ui.signin

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.beyondthecode.shared.result.Event
import timber.log.Timber
import javax.inject.Inject

/**
 * ViewModel for *both* the sign in & sign out dialogs.
 * */
class SignInViewModel @Inject constructor(
    signInViewModelDelegate: SignInViewModelDelegate
): ViewModel(), SignInViewModelDelegate by signInViewModelDelegate {

    private val _dismissDialogAction = MutableLiveData<Event<Unit>>()
    val dismissDialogAction: LiveData<Event<Unit>>
        get() =  _dismissDialogAction

    fun onSignIn(){
        Timber.d("Sign in requested")
        emitSignInRequest()
    }

    fun onSignOut(){
        Timber.d("Sign out requested")
        emitSignOutRequest()
    }

    fun onCancel(){
        _dismissDialogAction.value = Event(Unit)
    }
}