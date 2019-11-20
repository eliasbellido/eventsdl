package com.beyondthecode.shared.analytics

import android.app.Activity

interface AnalyticsHelper {

    //record a screen view
    fun sendScreenView(screenName: String, activity: Activity)

    //record a ui event, e.g. user click a button
    fun logUiEvent(itemId: String, action: String)

    //set the user signed in property
    fun setUserSignedIn(isSignedin: Boolean)

    //set the user registered property
    fun setUserRegistered(isRegistered: Boolean)
}

object AnalyticsActions{

    //UI actions
    const val STARRED = "Bookmarked"
    const val CLICK = "Clicked"
    const val RESERVE = "Reserverd"
    const val RESERVE_FAILED = "Reserved (Failed)"
    const val RES_CANCEL_FAILED = "Reservation Cancellation (Failed)"
    const val RES_CANCEL = "Reservation Cancellation"

    const val MAP_MARKER_SELECT = "Selected Map Marker"
    const val MAP_MARKER_DETAILS = "Viewed Map Marker Details"
    const val WIFI_CONNECT = "Connected to wifi"
    const val YOUTUBE_LINK = "Youtube link click"
    const val SEARCH_QUERY_SUBMIT = "Submitted search query"
    const val SEARCH_RESULT_CLICK = "Clicked on search result"

    const val HOME_TO_MAP ="Home to Map transition"
    const val HOME_TO_SCHEDULE = "Home to Schedule transition"
    const val HOME_TO_SESSION_DETAIL = "Home to Session Detail transition"
    const val HOME_TO_SIGN_IN = "Home to Sign In transition"
    const val HOME_TO_LIVESTREAM = "Home to Live stream transition"

    //Settings Actions
    const val ENABLE = "Enabled"
    const val DISABLE = "Disabled"
}