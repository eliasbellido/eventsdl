package com.beyondthecode.shared.data.config

import androidx.lifecycle.LiveData
import com.beyondthecode.model.ConferenceWifiInfo

interface AppConfigDataSource{
    fun getStringLiveData(key: String): LiveData<String>
    fun syncStringAsync(changedCallback: StringsChangedCallback?)
    fun getWifiInfo(): ConferenceWifiInfo
    fun isMapFeatureEnabled(): Boolean
    fun isExploreArFeatureEnabled(): Boolean
    fun isCodelabsFeatureEnabled(): Boolean
    fun isSearchScheduleFeatureEnabled(): Boolean
    fun isSearchUsingRoomFeatureEnabled(): Boolean
    fun isAssistantAppFeatureEnabled(): Boolean
}

interface StringsChangedCallback{

    /**
     * Called when any of Strings parameters are changed from the previous values.
     *
     * @param changedKeys has the list of key names whose values are changed from the previous values.
     * */
    fun onChanged(changedKeys: List<String>)

}