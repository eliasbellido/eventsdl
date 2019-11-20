package com.beyondthecode.shared.data

import com.beyondthecode.model.ConferenceData

interface ConferenceDataSource{
    fun getRemoteConferenceData(): ConferenceData?
    fun getOfflineConferenceData(): ConferenceData?
}

enum class UpdateSource{
    NONE,
    NETWORK,
    CACHE,
    BOOTSTRAP;
}