package com.beyondthecode.eventsdl.util.signin

import android.app.Activity
import android.content.Context
import android.content.Intent
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.beyondthecode.shared.domain.internal.DefaultScheduler
import com.firebase.ui.auth.AuthUI
import com.firebase.ui.auth.IdpResponse
import com.google.android.gms.auth.api.signin.GoogleSignInOptions

/**
 * Element in the presentation layer that interact with the Auth provider (Firebase in this case).
 *
 * This class is used from the activities or fragments.
 * */
interface SignInHandler{
    fun makeSignInIntent(): LiveData<Intent?>

    fun signIn(resultCode: Int, data: Intent?, onComplete: (SignInResult) -> Unit)

    fun signOut(context: Context, onComplete: () -> Unit = {})
}

/**
 * Implementation of [SignInHandler] that interacts with Firebase Auth.
 */
class FirebaseAuthSignInHandler: SignInHandler{

    /**
     * Request a sign in intent.
     *
     * To observe the result you must pass this to startActivityForResult.
     * */
    override fun makeSignInIntent(): LiveData<Intent?>{

        val result = MutableLiveData<Intent?>()

        //Run on background because AuthUI does I/O operations
        DefaultScheduler.execute {
            //this is mutable because FirebaseUI requires it be mutable
            var providers = mutableListOf(
                AuthUI.IdpConfig.GoogleBuilder().setSignInOptions(
                    GoogleSignInOptions.Builder()
                        .requestId()
                        .requestEmail()
                        .build()
                ).build()
            )

            result.postValue(
                AuthUI.getInstance()
                    .createSignInIntentBuilder()
                    .setAvailableProviders(providers)
                    .build()
            )
        }
        return result
    }

    /**
     * Parse the response from a sign in request, helper to call from onActivityResult.
     *
     * ´´´
     * signIn(resultCode, data) { result ->
     *      return when(result) {
     *          is SignInSuccess -> // all good
     *          is SignInFailed -> result?.error // access FirebaseUiException - can be null
     *                                           // (e.g. canceled)
     *      }
     * }
     * ´´´
     *
     * @param resultCode activity result code
     * @param data activity result intent
     * @param onComplete pass parsed result of either [SignInSuccess] or [SignInFailed]
     * */
    override fun signIn(
        resultCode: Int,
        data: Intent?,
        onComplete: (SignInResult) -> Unit) {

        when (resultCode) {
            Activity.RESULT_OK -> onComplete(SignInSuccess)
            else -> onComplete(SignInFailed(IdpResponse.fromResultIntent(data)?.error))
        }
    }

    /**
     * Attempt to sign the current user out.
     *
     * @param context any context
     * @param onComplete used to notify of signOut completion.
     * */
    override fun signOut(context: Context, onComplete: () -> Unit) {
        AuthUI.getInstance()
            .signOut(context)
            .addOnCompleteListener { onComplete() }
    }
}
