package com.beyondthecode.shared.data.feed

import com.beyondthecode.model.Moment
import com.beyondthecode.shared.data.document2019
import com.beyondthecode.shared.util.ColorUtils
import com.google.android.gms.tasks.Tasks
import com.google.firebase.Timestamp
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import org.threeten.bp.Instant
import org.threeten.bp.ZoneId

import java.util.concurrent.TimeUnit
import javax.inject.Inject

interface MomentDataSource{
    fun getMoments(): List<Moment>
}

/**
 * Moments data source backed by items in a FireStore collection.
 * */
class FirestoreMomentDataSource @Inject constructor(
    val firestore: FirebaseFirestore
): MomentDataSource{
    override fun getMoments(): List<Moment> {
        val task = firestore
            .document2019()
            .collection(MOMENT_COLLECTION)
            .get()

        val snapshot = Tasks.await(task, 20, TimeUnit.SECONDS)

        return snapshot.documents.map{ parseMomentItem(it) }.sortedBy { it.startTime }
    }

    private fun parseMomentItem(snapshot: DocumentSnapshot): Moment{
        return Moment(
            id = snapshot.id,
            title = snapshot[TITLE] as? String ?: "",
            streamUrl = snapshot[STREAM_URL] as? String ?: "",
            startTime = Instant.ofEpochSecond(
                (snapshot[START_TIME] as Timestamp).seconds
            ).atZone(ZoneId.systemDefault()),
            endTime = Instant.ofEpochSecond(
                (snapshot[END_TIME] as Timestamp).seconds
            ).atZone(ZoneId.systemDefault()),
            textColor = ColorUtils.parseHexcolor(snapshot[TEXT_COLOR] as? String ?: ""),
            ctaType = snapshot[CTA_TYPE] as? String ?: "",
            imageUrl = snapshot[IMAGE_URL] as? String ?: "",
            imageUrlDarkTheme = snapshot[IMAGE_URL_DARK] as? String ?: "",
            attendeeRequired = snapshot[ATTENDEE_REQUIRED] as? Boolean ?: false,
            timeVisible = snapshot[TIME_VISIBLE] as? Boolean ?: false,
            featureId = snapshot[FEATURE_ID] as? String,
            featureName = snapshot[FEATURE_NAME] as? String
        )
    }

    companion object {
        private const val MOMENT_COLLECTION = "moments"
        private const val TITLE = "title"
        private const val START_TIME = "startTime"
        private const val END_TIME = "endtime"
        private const val ATTENDEE_REQUIRED = "attendeeRequired"
        private const val STREAM_URL = "streamUrl"
        private const val TEXT_COLOR = "textColor"
        private const val IMAGE_URL = "imageUrl"
        private const val IMAGE_URL_DARK = "imageUrlDarkTheme"
        private const val CTA_TYPE = "ctaType"
        private const val TIME_VISIBLE = "timeVisible"
        private const val FEATURE_ID = "featureId"
        private const val FEATURE_NAME = "featureName"
    }
}