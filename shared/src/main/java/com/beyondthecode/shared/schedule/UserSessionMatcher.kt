package com.beyondthecode.shared.schedule

import com.beyondthecode.model.Tag
import com.beyondthecode.model.userdata.UserSession
import com.beyondthecode.shared.data.prefs.PreferenceStorage
import com.google.gson.GsonBuilder
import timber.log.Timber

/** Capable of discerning [UserSession]s that match some criterion*/
class UserSessionMatcher {

    companion object{
        val FILTER_CATEGORIES = arrayOf(Tag.CATEGORY_TOPIC, Tag.CATEGORY_TYPE)

        fun isSuppportedTag(tag: Tag): Boolean{
            return tag.tagName != Tag.TYPE_CODELABS && tag.category in FILTER_CATEGORIES
        }
    }

    private var showPinnedEventsOnly = false

    private val selectedTags = HashSet<TagIdAndCategory>()

    private val gson = GsonBuilder().create()

    @Synchronized
    fun setShowPinnedEventsOnly(pinnedOnly: Boolean): Boolean{
        if(showPinnedEventsOnly != pinnedOnly){
            showPinnedEventsOnly = pinnedOnly
            return true
        }
        return false
    }

    @Synchronized
    fun getShowPinnedEventsOnly() = showPinnedEventsOnly

    @Synchronized
    fun add(tag: Tag) = selectedTags.add(TagIdAndCategory.fromTag(tag))

    @Synchronized
    fun remove(tag: Tag) = selectedTags.remove(TagIdAndCategory.fromTag(tag))

    /** Returns true if the set of filtered tags changed*/
    @Synchronized
    fun addAll(vararg tags: Tag): Boolean{
        var changed = false
        tags.forEach {
            changed = changed or add(it)
        }
        return changed
    }

    /** returns true if the filters are changed by this call*/
    @Synchronized
    fun clearAll(): Boolean{
        val changed = hasAnyFilters()
        showPinnedEventsOnly = false
        selectedTags.clear()
        return changed
    }

    /** Returns true if the filters are changed by this call*/
    @Synchronized
    fun hasAnyFilters(): Boolean{
        return showPinnedEventsOnly || selectedTags.isNotEmpty()
    }

    @Synchronized
    operator fun contains(tag: Tag) = selectedTags.contains(TagIdAndCategory.fromTag(tag))

    /**
     * Remove any tags that aren't in [newTags]. Call this whenever a new list of Tagss is loaded
     * in order to reconcile the currently selected filters.
     * */
    @Synchronized
    fun removeOrphanedTags(newTags: List<Tag>){
        if(newTags.isEmpty()){
            selectedTags.clear()
        }else{
            val valid = newTags.map{ TagIdAndCategory(it.id, it.category) }
            selectedTags.removeAll(selectedTags.subtract(valid))
        }
    }

    /** Return true if the [UserSession] matches the current filters.*/
    @Synchronized
    fun matched(userSession: UserSession): Boolean{
        if(showPinnedEventsOnly && !userSession.userEvent.isPinned()){
            return false
        }
        var match = true
        val sessionTags = userSession.session.tags.map{ TagIdAndCategory(it.id, it.category) }

        selectedTags.groupBy { it.category }.forEach{ (_, tagsInCategory) ->
            if(sessionTags.intersect(tagsInCategory).isEmpty()) {
                match = false
                return@forEach
            }
        }
        return match
    }

    fun save(preferenceStorage: PreferenceStorage){
        val state = SavedFilterPreferences(showPinnedEventsOnly, selectedTags.toList())
        preferenceStorage.selectedFilters = gson.toJson(state)
    }

    fun load(preferenceStorage: PreferenceStorage){
        val prefValue = preferenceStorage.selectedFilters
        if(prefValue != null){
            val state = try {
                gson.fromJson(prefValue, SavedFilterPreferences::class.java)
            }catch (t: Throwable){
                Timber.e(t, "Error reading filter preferneces")
                return
            }
            showPinnedEventsOnly = state.showPinnedEventsOnly
            selectedTags.addAll(state.tagsAndCategories)
        }
    }


}

//Copy of Tag with only the id and category, for smaller serializing/deserializing.
data class TagIdAndCategory(
    val id: String,
    val category: String
){
    /**Only IDs are used for equality*/
    override fun equals(other: Any?): Boolean =
        this === other || (other is TagIdAndCategory && other.id == id)

    /**Only IDs are used for equality*/
    override fun hashCode(): Int = id.hashCode()

    companion object{
        fun fromTag(tag: Tag): TagIdAndCategory{
            return TagIdAndCategory(tag.id, tag.category)
        }
    }
}

// POJO saved to/loaded from preferences
data class SavedFilterPreferences(
    val showPinnedEventsOnly: Boolean,
    val tagsAndCategories: List<TagIdAndCategory>
)