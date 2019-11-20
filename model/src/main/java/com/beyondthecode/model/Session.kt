package com.beyondthecode.model

import org.threeten.bp.ZonedDateTime
import com.beyondthecode.model.SessionType.Companion.reservableTypes


typealias SessionId = String

/**
 * Describges a conference session. Sessions have specific start and end times,
 * and they represent a variety of conference events: talks, sandbox demos, offcice
 * hours, etc. A session is usually associated with one or more [Tag]s
 * */
data class Session(
    //unique string identifying this session.
    val id: SessionId,

    //start time of the session
    val startTime: ZonedDateTime,

    //end time of the session
    val endTime: ZonedDateTime,

    //session title
    val title: String,

    //body of text explaining this session in detail
    val description: String,

    //the session room.
    val room: Room?,

    //full URL for the session online
    val sessionUrl: String,

    //indicates if the session has a live stream
    val isLiveStream: Boolean,

    //full URL to youtube
    val youtubeUrl: String,

    //URL to the Dory page.
    val doryLink: String,

    /**
     * The [Tag]s associated with the session. Ordered, with the most important tags appearing     *
     * */
    val tags: List<Tag>,

    /**
     * Subset of [Tag]s that are for visual consumption
     * */
    val displayTags: List<Tag>,

    //the session speakers
    val speakers: Set<Speaker>,

    //the session's photo URL
    val photoUrl: String,

    //IDs of the sessions related to this session
    val relatedSessions: Set<SessionId>
){

    /**
     * Returns whether the session is currently being live streamed or not
     * */
    fun isLive(): Boolean{
        val now = ZonedDateTime.now()
        // TODO: Determine when a session is live based on the time AND the liveStream being available.
        return startTime <= now && endTime >= now
    }

    val hasPhoto inline get() = photoUrl.isNotEmpty()

    /**
     * Returns whether the session has a video or not. A session could be live streaming or have a
     * recorded session. Both live stream and recorded videos are stored in [Session.youtubeUrl]
     * */
    val hasVideo inline get() = youtubeUrl.isNotEmpty()

    val hasPhotooOrVideo inline get() = hasPhoto || hasVideo

    // the year the session was held
    val year = startTime.year

    /**
     * The duration of the session
     */
    //TODO: Get duration from the youtube video. not every talk fills the full session time.
    val duration = endTime.toInstant().toEpochMilli() - startTime.toInstant().toEpochMilli()

    // The type of the event e.g. Session, Codelab, etc.
    val type: SessionType by lazy(LazyThreadSafetyMode.PUBLICATION){
        SessionType.fromTags(tags)
    }

    fun levelTag(): Tag?{
        return tags.firstOrNull{ it.category == Tag.CATEGORY_LEVEL}
    }

    /**
     * Whether this event is reservable, based upon [type].
     * */
    val isReservable: Boolean by lazy(LazyThreadSafetyMode.PUBLICATION){
        type in reservableTypes
    }

    fun isOverlapping(session: Session): Boolean{
        return this.startTime < session.endTime && this.endTime > session.startTime
    }

    //Detailed description of this event. Includes description, speakers, and live-streaming URL.
    fun getCalendarDescription(
        paragraphDelimiter: String,
        speakerDelimiter: String
    ): String = buildString {
        append(description)
        append(paragraphDelimiter)
        append(speakers.joinToString (speakerDelimiter) {
            speaker -> speaker.name
        })

        if(!isLiveStream && !youtubeUrl.isEmpty()){
            append(paragraphDelimiter)
            append(youtubeUrl)
        }

    }
}


/**
 * Represents the type of the event e.g. Session, codelab, etc.
 * */
enum class SessionType(val displayName: String){

    KEYNOTE("Keynote"),
    SESSION("Session"),
    APP_REVIEW("App Reviews"),
    GAME_REVIEW("Game Reviews"),
    OFFICE_HOURS("Office Hours"),
    CODELAB("Codelab"),
    MEETUP("Meetup"),
    AFTER_DARK("After Dark"),
    UNKNOWN("Unknown");//';' required to close enum class body

    companion object {
        /**
         * Examine the given [tags] to determine the [SessionType]. Defaults to [SESSION] if no
         * category tag is found.
         * */
        fun fromTags(tags:List<Tag>): SessionType {
            val typeTag = tags.firstOrNull{ it.category == Tag.CATEGORY_TYPE }

            return when (typeTag?.tagName){
                Tag.TYPE_KEYNOTE -> KEYNOTE
                Tag.TYPE_SESSIONS -> SESSION
                Tag.TYPE_APP_REVIEWS -> APP_REVIEW
                Tag.TYPE_GAME_REVIEWS -> GAME_REVIEW
                Tag.TYPE_OFFICEHOURS -> OFFICE_HOURS
                Tag.TYPE_CODELABS -> CODELAB
                Tag.TYPE_MEETUPS -> MEETUP
                Tag.TYPE_AFTERDARK -> AFTER_DARK
                else -> UNKNOWN
            }
        }

        internal val reservableTypes = listOf(SESSION, OFFICE_HOURS, APP_REVIEW, GAME_REVIEW)

    }

}