package com.beyondthecode.shared.data.prefs

import android.content.Context
import android.content.SharedPreferences
import androidx.annotation.WorkerThread
import androidx.core.content.edit
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.beyondthecode.model.Theme
import javax.inject.Inject
import javax.inject.Singleton
import kotlin.properties.ReadWriteProperty
import kotlin.reflect.KProperty

/**
 * Storage for app and user preferences.
 * */
interface PreferenceStorage {
    var onboardingCompleted: Boolean
    var scheduleUiHintsShown: Boolean
    var notificationsPreferenceShown: Boolean
    var preferToReceiveNotifications: Boolean
    var myLocationOptedIn: Boolean
    var snackbarIsStopped: Boolean
    var observableSnackbarIsStopped: LiveData<Boolean>
    var sendUsageStatistics: Boolean
    var preferConferenceTimeZone: Boolean
    var selectedFilters: String?
    var selectedTheme: String?
    var observableSelectedTheme: LiveData<String>
    var codelabsInfoShown: Boolean
}

/**
 * [PreferenceStorage] impl backend by [android.content.SharedPreferences].
 * */
@Singleton
class SharedPreferenceStorage @Inject constructor(context: Context) : PreferenceStorage {

    private val prefs: Lazy<SharedPreferences> = lazy {
        // Lazy to prevent IO access to main thread.
        context.applicationContext.getSharedPreferences(
            PREFS_NAME, Context.MODE_PRIVATE
        ).apply {
            registerOnSharedPreferenceChangeListener(changeListener)
        }
    }

    private val observableShowSnackbarResult = MutableLiveData<Boolean>()
    private val observableSelectedThemeResult = MutableLiveData<String>()

    private val changeListener =
        SharedPreferences.OnSharedPreferenceChangeListener { sharedPreferences, key ->
            when (key) {
                PREF_SNACKBAR_IS_STOPPED -> observableShowSnackbarResult.value = snackbarIsStopped
                PREF_DARK_MODE_ENABLED -> observableSelectedThemeResult.value = selectedTheme
            }

        }

    override var onboardingCompleted by BooleanPreference(prefs, PREF_ONBOARDING, false)

    override var scheduleUiHintsShown by BooleanPreference(prefs, PREF_SCHED_UI_HINTS_SHOWN, false)

    override var notificationsPreferenceShown by BooleanPreference(
        prefs,
        PREF_NOTIFICATIONS_SHOWN,
        false
    )

    override var preferToReceiveNotifications by BooleanPreference(
        prefs,
        PREF_RECEIVE_NOTIFICATIONS,
        false
    )

    override var myLocationOptedIn by BooleanPreference(prefs, PREF_MY_LOCATION_OPTED_IN, false)

    override var snackbarIsStopped by BooleanPreference(prefs, PREF_SNACKBAR_IS_STOPPED, false)

    override var observableSnackbarIsStopped: LiveData<Boolean>
        get() {
            observableShowSnackbarResult.value = snackbarIsStopped
            return observableShowSnackbarResult
        }set (_) = throw IllegalAccessException("This property can't be changed")

    override var sendUsageStatistics by BooleanPreference(prefs, PREF_SEND_USAGE_STATISTICS, true)

    override var preferConferenceTimeZone by BooleanPreference(prefs, PREF_CONFERENCE_TIME_ZONE, true)

    override var selectedFilters by StringPreference(prefs, PREF_SELECTED_FILTERS, null)

    override var selectedTheme by StringPreference(prefs, PREF_DARK_MODE_ENABLED, Theme.SYSTEM.storageKey)

    override var observableSelectedTheme: LiveData<String>
        get() {
            observableSelectedThemeResult.value = selectedTheme
            return observableSelectedThemeResult
        }
        set(_) = throw IllegalAccessException("This property can't be changed")

    override var codelabsInfoShown by BooleanPreference(prefs, PREF_CODELABS_INFO_SHOWN, false)


    companion object{
        const val PREFS_NAME = "eventsdl"
        const val PREF_ONBOARDING = "pref_onboarding"
        const val PREF_SCHED_UI_HINTS_SHOWN = "pref_sched_ui_hints_shown"
        const val PREF_NOTIFICATIONS_SHOWN = "pref_notifications_shown"
        const val PREF_RECEIVE_NOTIFICATIONS = "pref_receive_notifications"
        const val PREF_MY_LOCATION_OPTED_IN = "pref_my_location_opted_in"
        const val PREF_SNACKBAR_IS_STOPPED = "pref_snackbar_is_stopped"
        const val PREF_SEND_USAGE_STATISTICS = "pref_send_usage_statistics"
        const val PREF_CONFERENCE_TIME_ZONE = "pref_conference_time_zone"
        const val PREF_SELECTED_FILTERS = "pref_selected_filters"
        const val PREF_DARK_MODE_ENABLED = "pref_dark_mode"
        const val PREF_CODELABS_INFO_SHOWN = "pref_codelabs_info_shown"
    }

    fun registerOnPreferenceChangeListener(listener: SharedPreferences.OnSharedPreferenceChangeListener){
        prefs.value.registerOnSharedPreferenceChangeListener(listener)
    }
}

class BooleanPreference(
    private val preferences: Lazy<SharedPreferences>,
    private val name: String,
    private val defaultValue: Boolean
) : ReadWriteProperty<Any, Boolean>{

    @WorkerThread
    override fun getValue(thisRef: Any, property: KProperty<*>): Boolean {
        return preferences.value.getBoolean(name, defaultValue)
    }

    override fun setValue(thisRef: Any, property: KProperty<*>, value: Boolean) {
        preferences.value.edit{ putBoolean(name, value) }
    }
}

class StringPreference(
    private val preferences: Lazy<SharedPreferences>,
    private val name: String,
    private val defaultValue: String?
): ReadWriteProperty<Any, String?> {

    @WorkerThread
    override fun getValue(thisRef: Any, property: KProperty<*>): String? {
        return preferences.value.getString(name, defaultValue)
    }

    override fun setValue(thisRef: Any, property: KProperty<*>, value: String?) {
        preferences.value.edit { putString(name, value) }
    }
}