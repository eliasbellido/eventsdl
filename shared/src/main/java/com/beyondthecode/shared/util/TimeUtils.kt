package com.beyondthecode.shared.util

import androidx.annotation.StringRes
import com.beyondthecode.model.ConferenceDay
import com.beyondthecode.model.Session
import com.beyondthecode.shared.BuildConfig
import com.beyondthecode.shared.R
import org.threeten.bp.ZoneId
import org.threeten.bp.ZonedDateTime
import org.threeten.bp.format.DateTimeFormatter

object TimeUtils {

    val CONFERENCE_TIMEZONE: ZoneId = ZoneId.of(BuildConfig.CONFERENCE_TIMEZONE)

    val ConferenceDays = listOf(
        ConferenceDay(
            ZonedDateTime.parse(BuildConfig.CONFERENCE_DAY1_START),
            ZonedDateTime.parse(BuildConfig.CONFERENCE_DAY1_END)
        ),
        ConferenceDay(
            ZonedDateTime.parse(BuildConfig.CONFERENCE_DAY2_START),
            ZonedDateTime.parse(BuildConfig.CONFERENCE_DAY2_END)
        ),
        ConferenceDay(
            ZonedDateTime.parse(BuildConfig.CONFERENCE_DAY3_START),
            ZonedDateTime.parse(BuildConfig.CONFERENCE_DAY3_END)
        )
    )

    enum class SessionRelativeTimeState { BEFORE, DURING, AFTER, UNKNOWN }

    /** Determine whether the current time is before, during */
    fun getSessionState(
        session: Session?,
        currentTime: ZonedDateTime = ZonedDateTime.now()
    ): SessionRelativeTimeState{
        return when{
            session == null -> SessionRelativeTimeState.UNKNOWN
            currentTime < session.startTime -> SessionRelativeTimeState.BEFORE
            currentTime > session.endTime -> SessionRelativeTimeState.AFTER
            else -> SessionRelativeTimeState.DURING
        }
    }

    /** Return a string resource to use for the label of this day, e.g. 'Tuesday, May 7'. */
    @StringRes
    fun getLabelResForDay(day: ConferenceDay, inConferenceTimeZone: Boolean = true): Int{
        return when (day){
            ConferenceDays[0] -> if (inConferenceTimeZone) R.string.day1_day_date else R.string.day1
            ConferenceDays[1] -> if (inConferenceTimeZone) R.string.day2_day_date else R.string.day2
            ConferenceDays[2] -> if (inConferenceTimeZone) R.string.day3_day_date else R.string.day3
            else -> throw IllegalArgumentException("Unknown ConferenceDay")
        }
    }

    /** Return a short string resource to use for the label of this day, e.g. 'May 7'.*/
    @StringRes
    fun getShortLabelResForDay(day: ConferenceDay, inConferenceTimeZone: Boolean = true): Int{
        return when (day){
            ConferenceDays[0] -> if (inConferenceTimeZone) R.string.day1_date else R.string.day1
            ConferenceDays[1] -> if (inConferenceTimeZone) R.string.day2_date else R.string.day2
            ConferenceDays[2] -> if (inConferenceTimeZone) R.string.day3_date else R.string.day3
            else -> throw IllegalArgumentException("Unknown ConferenceDay")
        }
    }

    /** Return a string resource to use for the nearest day to the given time */
    @StringRes
    fun getLabelResForTime(time: ZonedDateTime, inConferenceTimeZone: Boolean = true): Int{
        return when{
            time.isBefore(ConferenceDays[0].start) -> if (inConferenceTimeZone) R.string.day0_day_date else R.string.day0
            time.isBefore(ConferenceDays[1].start) -> if (inConferenceTimeZone) R.string.day1_day_date else R.string.day1
            time.isBefore(ConferenceDays[2].start) -> if (inConferenceTimeZone) R.string.day2_day_date else R.string.day2
            else -> if (inConferenceTimeZone) R.string.day3_day_date else R.string.day3
        }
    }

    fun zonedTime(time: ZonedDateTime, zoneId: ZoneId = ZoneId.systemDefault()): ZonedDateTime{
        return ZonedDateTime.ofInstant(time.toInstant(), zoneId)
    }

    fun isConferenceTimeZone(zoneId: ZoneId = ZoneId.systemDefault()): Boolean{
        return zoneId == CONFERENCE_TIMEZONE
    }

    fun abbreviatedTimeString(startTime: ZonedDateTime): String{
        return DateTimeFormatter.ofPattern("EEE, MM d").format(startTime)
    }

    /** Format a time to show month and day, e.g. 'May 7'*/
    fun dateString(startTime: ZonedDateTime): String{
        return DateTimeFormatter.ofPattern("MMM d").format(startTime)
    }

    /** Format a time to show month and day, e.g. 'May 7, 10:00 AM'*/
    fun dateTimeString(startTime: ZonedDateTime): String{
        return DateTimeFormatter.ofPattern("MMM d, h:mm a").format(startTime)
    }

    fun timeString(startTime: ZonedDateTime, endTime: ZonedDateTime): String{
        val sb = StringBuilder()
        sb.append(DateTimeFormatter.ofPattern("EEE, MM d, h:mm ").format(startTime))

        val startTimeMeridiem: String = DateTimeFormatter.ofPattern("a").format(startTime)
        val endTimeMeridiem: String = DateTimeFormatter.ofPattern("a").format(endTime)

        if(startTimeMeridiem != endTimeMeridiem){
            sb.append(startTimeMeridiem).append(" ")
        }

        sb.append(DateTimeFormatter.ofPattern("- h:mm a").format(endTime))
        return sb.toString()
    }

    fun abbreviatedDayForAr(startTime: ZonedDateTime): String{
        return DateTimeFormatter.ofPattern("MM/dd").format(startTime)
    }

    fun abbreviatedTimeForAr(startTime: ZonedDateTime): String{
        return DateTimeFormatter.ofPattern("HH:mm").format(startTime)
    }

    fun conferenceHasStarted(): Boolean{
        return ZonedDateTime.now().isAfter(ConferenceDays.first().start)
    }

    fun conferenceHasEnded(): Boolean{
        return ZonedDateTime.now().isAfter(ConferenceDays.last().end)
    }

    //TODO: replace with a UseCase
    fun getKeynoteStartTime(): ZonedDateTime{
        return ConferenceDays.first().start.plusHours(3L)
    }

    fun getConferenceEndTime() = ConferenceDays.last().end

}