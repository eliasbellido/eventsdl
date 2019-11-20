package com.beyondthecode.model

/**
 * Describes a tag, which contains meta-info about a conference session. A tag has 2 components,
 * a category, and a name, and together these give a tag its semantic meaning. example, a session
 * may contain the following tag: {category: "TRACK", name: "ANDROID"} and {category: "TYPE",
 * "OFFICEHOURS"}. The first tag defines the session track as Android, and the 2nd tag defines
 * the session type as an office hour
 * */
data class Tag(

    //unique string indetifying this tag
    val id: String,

    //tag category. For example, "track", "level", "type", "theme", etc.
    val category: String,

    /**
     * Tag name. For example, "topic_iot", "type_afterhours", "topic_ar&vr", etc. Used to resolve
     * references to this tag from other entities during data deserialization and normalization.
     * For UI, use [displayName] instead.
     * */
    val tagName: String,

    /**
     * This tag's order within its [category]
     * */
    val orderInCategory: Int,

    //Display name within a category. For example, "Android", "Ads", "Design".
    val displayName: String,

    //the color associated wit this tag as a color integer.
    val color: Int,

    //the font color associated with this tag as a color integer.
    val fontColor: Int? = null
){
    companion object {

        //category value for topic tags
        const val CATEGORY_TOPIC = "topic"
        //cateogry value for type tags
        const val CATEGORY_TYPE = "type"
        //category value for theme tags
        const val CATEGORY_THEME = "theme"
        //cateogory value for level tags
        const val CATEGORY_LEVEL = "level"

        //Exhaustive list of IDs for tags with category = TYPE
        const val TYPE_KEYNOTE = "type_keynotes"
        const val TYPE_SESSIONS = "type_sessions"
        const val TYPE_APP_REVIEWS = "type_appreviews"
        const val TYPE_AFTERDARK = "type_afterdark"
        const val TYPE_CODELABS = "type_codelabs"
        const val TYPE_GAME_REVIEWS = "type_gamereviews"
        const val TYPE_MEETUPS = "type_meetups"
        const val TYPE_OFFICEHOURS = "type_officehours"
    }

    //only IDs are used for equality
    override fun equals(other: Any?): Boolean = this === other || (other is Tag && other.id == id)

    override fun hashCode(): Int = id.hashCode()

    fun isUiContentEqual(other: Tag) = color == other.color && displayName == other.displayName

    fun isLightFontColor() = fontColor?.toLong() == 0xFFFFFFFF
}