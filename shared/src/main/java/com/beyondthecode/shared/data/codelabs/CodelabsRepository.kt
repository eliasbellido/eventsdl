package com.beyondthecode.shared.data.codelabs

import com.beyondthecode.model.Codelab
import com.beyondthecode.shared.data.ConferenceDataRepository
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
open class CodelabsRepository @Inject constructor(
    private val conferenceDataRepository: ConferenceDataRepository
){
    fun getCodelabs(): List<Codelab> = conferenceDataRepository.getOfflineConferenceData().codelabs
}