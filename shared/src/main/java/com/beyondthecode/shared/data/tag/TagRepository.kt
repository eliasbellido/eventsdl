package com.beyondthecode.shared.data.tag

import com.beyondthecode.model.Tag
import com.beyondthecode.shared.data.ConferenceDataRepository
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Single point of access to tag data for the presentation layer.
 * */
@Singleton
open class TagRepository @Inject constructor(
    private val conferenceDataRepository: ConferenceDataRepository
){
    fun getTags(): List<Tag> = conferenceDataRepository.getOfflineConferenceData().tags
}