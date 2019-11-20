package com.beyondthecode.shared.data.signin.datasources

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.beyondthecode.shared.data.signin.AuthenticatedUserInfoBasic
import com.beyondthecode.shared.data.signin.AuthenticatedUserRegistration
import com.beyondthecode.shared.data.signin.FirebaseUserInfo
import com.beyondthecode.shared.domain.internal.DefaultScheduler
import com.beyondthecode.shared.domain.sessions.NotificationAlarmUpdater
import com.beyondthecode.shared.fcm.FcmTokenUpdater
import com.google.firebase.auth.FirebaseAuth
import com.beyondthecode.shared.result.Result
import com.google.android.gms.tasks.Tasks
import com.google.firebase.auth.GetTokenResult
import timber.log.Timber
import javax.inject.Inject

/**
 * An [AuthStateUserDataSource] that listens to changes in [FirebaseAuth].
 *
 * When a [FirebaseUSer] is available, it
 * * Posts it to the user observable
 * * Feteches the ID token
 * * Uses the ID token to trigger the registration point
 * * Stores the FCM ID Token in Firestore
 * * Posts the user ID to the observable
 *
 * This data source doesn't find if a user is registered or not (is an attendee). Once the
 * registration point is called, the server will generate a field in the user document, which
 * is observed by [RegisteredUserDataSource] in its implementation
 * [FirestoreRegisteredUserDataSource]
 * */
class FirebaseAuthStateUserDataSource @Inject constructor(
    val firebase: FirebaseAuth,
    private val tokenUpdater: FcmTokenUpdater,
    notificationAlarmUpdater: NotificationAlarmUpdater
): AuthStateUserDataSource{
    private val currentFirebaseUserObservable = MutableLiveData<Result<AuthenticatedUserInfoBasic?>>()

    private var isAlreadyListening = false

    private var lastUid: String? = null

    //Listener that saves the [FirebaseUser], fetches the ID token
    //and updates the user ID observable.
    private val authStateListener: ((FirebaseAuth) -> Unit) = { auth ->
        DefaultScheduler.execute {
            Timber.d("Received a FirebaseAuth update.")
            //Post the current user for observers
            currentFirebaseUserObservable.postValue(
                Result.Success(
                    FirebaseUserInfo(auth.currentUser)
                )
            )

            auth.currentUser?.let { currentUser ->

                //Get the ID token (force refresh)
                val tokenTask = currentUser.getIdToken(true)
                try{
                    //Do this synchronously
                    val await: GetTokenResult = Tasks.await(tokenTask)
                    await.token?.let{
                        //Call registration point to generate a result in Firestore
                        Timber.d("User authenticated, hitting registration endpoint")
                        AuthenticatedUserRegistration.callRegistrationEndpoint(it)
                    }
                }catch (e: Exception){
                    Timber.e(e)
                    return@let
                }
                //Save the FCM ID token in firestore
                tokenUpdater.updateTokenForUser(currentUser.uid)
            }
        }
        if(auth.currentUser == null){
            //Logout, cancel all alarms
            notificationAlarmUpdater.cancelAll()
        }
        auth.currentUser?.let{
            if(lastUid != auth.uid){ //Prevent duplicate
                notificationAlarmUpdater.updateAll(it.uid)
            }
        }
        // Save the last UID to prevent setting too many alarms.
        lastUid = auth.uid
    }

    override fun startListening() {
        if(!isAlreadyListening){
            firebase.addAuthStateListener(authStateListener)
            isAlreadyListening = true
        }
    }

    override fun getBasicUserInfo(): LiveData<Result<AuthenticatedUserInfoBasic?>> {
        return currentFirebaseUserObservable
    }


    override fun clearListener() {
        firebase.removeAuthStateListener(authStateListener)
    }
}