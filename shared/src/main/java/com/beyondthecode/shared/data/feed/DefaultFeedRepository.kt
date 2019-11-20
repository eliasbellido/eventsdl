package com.beyondthecode.shared.data.feed

import com.beyondthecode.model.Announcement
import com.beyondthecode.model.Moment
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Single point of access to feed data for the presentation layer
 * */
interface FeedRepository{
    fun getAnnouncements(): List<Announcement>
    fun getMoments(): List<Moment>
}

@Singleton
open class DefaultFeedRepository @Inject constructor(
    private val announcementDataSource: AnnouncementDataSource,
    private val momentDataSource: MomentDataSource
): FeedRepository{
    override fun getAnnouncements(): List<Announcement> = announcementDataSource.getAnnouncements()

    override fun getMoments(): List<Moment> = momentDataSource.getMoments()

}