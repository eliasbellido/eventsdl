package com.beyondthecode.model

data class Codelab(
    /** Unique ID identifying this Codelab */
    val id: String,

    /** Codelab title*/
    val title: String,
    val description: String,
    /** Approximate time in minutes a user would spend doing this codelab*/
    val durationMinutes: Int,
    val iconUrl: String?,
    val codelabUrl: String,
    /** Sort priority. Higher sort priority should come before lower ones. */
    val sortPriority: Int,
    /** [Tag]s applicable to this codelab*/
    val tags: List<Tag>
){
    fun hasUrl() = !codelabUrl.isBlank()
}