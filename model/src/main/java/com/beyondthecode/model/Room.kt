package com.beyondthecode.model

/**
 * Describes a venue associated with the conference.
 * */
data class Room(

    //unique string identifying this room.
    val id: String,

    //the name of the room.
    val name: String

){
    val abbreviatedName get() = name.split("|")[0]
}