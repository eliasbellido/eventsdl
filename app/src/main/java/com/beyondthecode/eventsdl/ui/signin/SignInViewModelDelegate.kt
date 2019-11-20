package com.beyondthecode.eventsdl.ui.signin

import android.net.Uri
import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.MutableLiveData
import com.beyondthecode.shared.result.Result
import com.beyondthecode.shared.data.signin.AuthenticatedUserInfo
import com.beyondthecode.shared.domain.auth.ObserveUserAuthStateUseCase
import com.beyondthecode.shared.domain.prefs.NotificationsPrefIsShownUseCase
import com.beyondthecode.shared.result.Event
import com.beyondthecode.shared.util.map
import javax.inject.Inject

enum class SignInEvent{
    RequestSignIn,
    RequestSignOut
}

/**
 * Interface to implement sign-in functionality in a ViewModel.
 *
 * You can inject a implementation of this via Dagger2, then use the implementation as an interface
 * delegate to add sign in functionality without writing any code
 *
 * Example usage
 *
 * ```
 * class MyViewModel @Inject constructor(
 *     signInViewModelComponent: SignInViewModelDelegate
 * ) : ViewModel(), SignInViewModelDelegate by signInViewModelComponent {
 * ```
 * */
interface SignInViewModelDelegate{
    /**
     * Live updated value of the current firebase user
     * */
    val currentUserInfo: LiveData<AuthenticatedUserInfo?>

    /**
     * Live updated value of the current firebase users image url
     * */
    val currentUserImageUri: LiveData<Uri?>

    /**
     * Emits Events when a sign-in event should be attempted
     * */
    val performSignInEvent: MutableLiveData<Event<SignInEvent>>

    /**
     * Emits an non-null Event when the dialog to ask the user notifications preference should be
     * shown.
     * */
    val shouldShowNotificationsPrefAction: LiveData<Event<Boolean>>

    /**
     * Emit an event on performSignInEvent to request sign-in
     * */
    fun emitSignInRequest()

    /**
     * Emit an Event on performSignInEvent to request sign-out
     * */
    fun emitSignOutRequest()

    fun observeSignedInUser(): LiveData<Boolean>

    fun observeRegisteredUser(): LiveData<Boolean>

    fun isSignedIn(): Boolean

    fun isRegistered(): Boolean

    /**
     * Returns the current user ID or null if not available.
     * */
    fun getUserID(): String?
}

/**
 * Implementation of SignInViewModelDelegate that uses Firebase's auth mechanisms.
 * */
internal class FirebaseSignInViewModelDelegate @Inject constructor(
    observeUserAuthStateUseCase: ObserveUserAuthStateUseCase,
    private val notificationsPrefIsShownUseCase: NotificationsPrefIsShownUseCase
): SignInViewModelDelegate{

    override val performSignInEvent = MutableLiveData<Event<SignInEvent>>()
    override val currentUserInfo: LiveData<AuthenticatedUserInfo?>
    override val currentUserImageUri: LiveData<Uri?>
    override val shouldShowNotificationsPrefAction = MediatorLiveData<Event<Boolean>>()

    private val _isRegistered: LiveData<Boolean>
    private val _isSignedIn: LiveData<Boolean>

    private val notificationsPrefIsShown = MutableLiveData<Result<Boolean>>()

    init{
        currentUserInfo = observeUserAuthStateUseCase.observe().map { result ->
            (result as? Result.Success)?.data
        }

        currentUserImageUri = currentUserInfo.map{ user ->
            user?.getPhotoUrl()
        }

        _isSignedIn = currentUserInfo.map { isSignedIn() }

        _isRegistered = currentUserInfo.map { isRegistered() }

        observeUserAuthStateUseCase.execute(Any())

        shouldShowNotificationsPrefAction.addSource(notificationsPrefIsShown){
            showNotificationPref()
        }

        shouldShowNotificationsPrefAction.addSource(_isSignedIn){
            //Refresh the preferences
            notificationsPrefIsShown.value = null
            notificationsPrefIsShownUseCase(Unit, notificationsPrefIsShown)
        }
    }


}