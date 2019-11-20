package com.beyondthecode.model

import org.threeten.bp.ZonedDateTime

/**
 * Defines a block of time associated with the conference. For example, a span of time denotes
 * the time when codelabs are offered, or when lunch is provided, etc.
 * */
data class Block(

    /**The title of the block. Ex: "Sandbox"*/
    val title: String,

    /**The type of agenda item. Ex: "concert", or "meal"*/
    val type: String,

    /**The color of the block*/
    val color: Int,

    /**The stroke color of the block (default to [color])*/
    val strokeColor: Int = color,

    /**If [color] is dark i.e. overlaid text should be light (defaults to false)*/
    val isDark: Boolean = false,

    val startTime: ZonedDateTime,

    val endTime: ZonedDateTime
)