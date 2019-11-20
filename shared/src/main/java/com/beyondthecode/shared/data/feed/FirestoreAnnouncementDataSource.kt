package com.beyondthecode.shared.data.feed

import com.beyondthecode.model.Announcement
import com.beyondthecode.shared.data.document2019
import com.beyondthecode.shared.util.ColorUtils
import com.google.android.gms.tasks.Tasks
import com.google.firebase.Timestamp
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import org.threeten.bp.Instant
import org.threeten.bp.ZoneOffset
import java.util.concurrent.TimeUnit
import javax.inject.Inject

interface AnnouncementDataSource{
    fun getAnnouncements(): List<Announcement>
}

/**
 * Feed data source backed by items in a FireStore collection.
 * */
class FirestoreAnnouncementDataSource @Inject constructor(
    val firestore: FirebaseFirestore
): AnnouncementDataSource{
    override fun getAnnouncements(): List<Announcement> {
        val task = firestore
            .document2019()
            .collection(FEED_COLLECTION)
            .whereEqualTo(ACTIVE, true)
            .get()

        val snapshot = Tasks.await(task, 20, TimeUnit.SECONDS)

        return snapshot.documents.map { parseFeedItem(it) }
            .sortedWith(compareByDescending<Announcement> { it.priority}
            .thenByDescending { it.timestamp })


    }

    private fun parseFeedItem(snapshot: DocumentSnapshot): Announcement{
        return Announcement(
            id = snapshot.id,
            title = snapshot[TITLE] as? String ?: "",
            category = snapshot[CATEGORY] as? String ?: "",
            imageUrl = snapshot[IMAGE_URL] as? String,
            message = snapshot[MESSAGE] as? String ?: "",
            timestamp = Instant.ofEpochSecond(
                (snapshot[TIMESTAMP] as Timestamp).seconds
            ).atZone(ZoneOffset.UTC),
            color = ColorUtils.parseHexcolor(snapshot[COLOR] as? String ?: ""),
            priority = snapshot[PRIORITY] as? Boolean ?: false,
            emergency = snapshot[EMERGENCY] as? Boolean ?: false

        )
    }

    companion object{
        /**
         * Firestore constants.
         * */
        private const val FEED_COLLECTION = "feed"
        private const val ACTIVE = "active"
        private const val CATEGORY = "category"
        private const val COLOR = "color"
        private const val TIMESTAMP = "timeStamp"
        private const val IMAGE_URL = "imageUrl"
        private const val MESSAGE = "message"
        private const val PRIORITY = "priority"
        private const val TITLE = "title"
        private const val EMERGENCY = "emergency"
    }
}