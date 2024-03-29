package com.beyondthecode.shared.util

import android.os.Parcel
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.LayoutRes
import androidx.core.os.ParcelCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentTransaction
import androidx.lifecycle.*
import com.beyondthecode.shared.BuildConfig
import org.threeten.bp.ZonedDateTime
import timber.log.Timber

/** Convenience for callbacks/listeners whose return value indicates an event was consumed*/
inline fun consume(f: () -> Unit): Boolean{
    f()
    return true
}

/**
 * Allows calls like
 *
 * `viewGroup.inflate(R.layout.foo)`
 * */
fun ViewGroup.inflate(@LayoutRes layout: Int, attachToRoot: Boolean = false): View{
    return LayoutInflater.from(context).inflate(layout, this, attachToRoot)
}

/**
 * Allows calls like
 *
 * `supportFragmentManager.inTransaction { add(...) }`
 * */
inline fun FragmentManager.inTransaction(func: FragmentTransaction.() -> FragmentTransaction){
    beginTransaction().func().commit()
}

// region ViewModels

/**
 * For Activities, allows declaration like
 * val my ViewModel = viewModelProvider(myViewModelFactory)
 * */
inline fun <reified VM : ViewModel> FragmentActivity.viewModelProvider(
    provider: ViewModelProvider.Factory
) =
    ViewModelProviders.of(this, provider).get(VM::class.java)

/**
 * For Fragments, allows declarations like
 * val myViewModel = viewModelProvider(myViewModelFactory)
 * */
inline fun <reified VM: ViewModel> Fragment.viewModelProvider(
    provider: ViewModelProvider.Factory
) =
    ViewModelProviders.of(this, provider).get(VM::class.java)

/**
 * Like [Fragment.viewModelProvider] for Fragments that want a [ViewModel] scoped to the Activity.
 * */
inline fun <reified VM: ViewModel> Fragment.activityViewModelProvider(
    provider: ViewModelProvider.Factory
) =
    ViewModelProviders.of(requireActivity(), provider).get(VM::class.java)

/**
 * Like [Fragment.viewModelProvider] for Fragments thatwant a [ViewModel] scoped to the parent
 * Fragment.
 * */
inline fun <reified VM: ViewModel> Fragment.parentViewmodelProvider(
    provider: ViewModelProvider.Factory
) =
    ViewModelProviders.of(parentFragment!!, provider).get(VM::class.java)

//endregion
//region Parcelables, Bundles

/** Write a boolean to a Parcel. */
fun Parcel.writeBooleanUsingCompat(value: Boolean) = ParcelCompat.writeBoolean(this, value)

/** Read a boolean from a Parcel.*/
fun Parcel.readBooleanUsingCompat() = ParcelCompat.readBoolean(this)

// endregion
// region Livedata

/**Uses `Transformations.map` on a LiveData*/
fun <X, Y> LiveData<X>.map(body: (X) -> Y): LiveData<Y>{
    return Transformations.map(this, body)
}

fun <T> MutableLiveData<T>.setValueIfNew(newValue: T){
    if(this.value != newValue) value = newValue
}

// end region

//region ZonedDateTime
fun ZonedDateTime.toEpochMilli() = this.toInstant().toEpochMilli()
//end region

/***
 * Helper to force a when statement to assert all options are matched in a when statement.
 *
 * By default, Kotlin doesn't care if all branches are handled in a when statement. However, if you
 * use the when statement as an expression (with a value) it will force all cases to be handled.
 *
 * This helper is to make a lightweight way to say you meant to match all of them.
 *
 * Usage:
 *
 * ```
 * when(sealedObject){
 *      is OneType -> //
 *      is AnotherType -> //
 * }.checkAllMatched
 * */
val <T> T.checkAllMatched: T
    get() = this

// region UI utils

//end region

/***
 * Helper to throw exceptions only in Debug builds, logging a warning otherwise
 */
fun exceptionInDebug(t: Throwable){
    if(BuildConfig.DEBUG){
        throw t
    }else{
        Timber.e(t)
    }
}