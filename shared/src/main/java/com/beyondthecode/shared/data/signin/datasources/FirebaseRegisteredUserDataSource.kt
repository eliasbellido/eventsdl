package com.beyondthecode.shared.data.signin.datasources

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.beyondthecode.shared.data.document2019
import com.beyondthecode.shared.domain.internal.DefaultScheduler
import com.beyondthecode.shared.result.Result
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.FirebaseFirestoreException
import com.google.firebase.firestore.ListenerRegistration
import timber.log.Timber
import javax.inject.Inject

/**
 * A [RegisteredUserDataSource] that listens to changes in firestore to indicate whether the
 * current user is registered in the event or not as an attendee
 * */
class FirebaseRegisteredUserDataSource  @Inject constructor(
    val firestore: FirebaseFirestore
): RegisteredUserDataSource{

    companion object{
        private const val USERS_COLLECTION = "users"
        private const val REGISTERED_KEY = "registered"
    }

    private var registeredChangedListenerSubscription: ListenerRegistration? = null

    //Result can contain a null value (not processed) or a null result (not available).
    private val result = MutableLiveData<Result<Boolean?>?>()

    //Keeping the last observed user ID, to avoid unnecessary calls
    private var lastUserId: String? = null

    override fun listenToUserChanges(userId: String) {
        val newUserId = if(lastUserId != userId){
            userId
        }else{
            //No need to refresh
            return
        }

        //Remove the previous subscription, if it exists:
        registeredChangedListenerSubscription?.remove()

        Timber.d("LoadUserSessionuseCase FirestoreRegistereduserDataSource = null")
        result.postValue(null) //Reset result

        //Watch the document:
        val registeredChangedListener =
            { snapshot: DocumentSnapshot?, _: FirebaseFirestoreException? ->
                DefaultScheduler.execute {
                    if(snapshot == null || !snapshot.exists()){
                        //When the account signs in for the first time, the document doesn't exist
                        Timber.d("Dodcument for snapshot $newUserId doesn't exist")
                        result.postValue(Result.Success(false))
                        return@execute
                    }

                    val isRegistered: Boolean? = snapshot.get(REGISTERED_KEY) as? Boolean
                    // Only emit a value if it's a new value or a value change.
                    if(result.value == null ||
                        (result.value as? Result.Success)?.data != isRegistered ){

                        Timber.d("Received registered flag: $isRegistered")
                        result.postValue(Result.Success(isRegistered))
                    }
                }
            }

        registeredChangedListenerSubscription = firestore
            .document2019()
            .collection(USERS_COLLECTION)
            .document(newUserId)
            .addSnapshotListener(registeredChangedListener)

        lastUserId = newUserId
    }

    override fun observeResult(): LiveData<Result<Boolean?>?> {
        return result
    }

    override fun setAnonymousValue() {
        registeredChangedListenerSubscription?.remove()
        lastUserId = null
        result.postValue(Result.Success(false))
    }
}