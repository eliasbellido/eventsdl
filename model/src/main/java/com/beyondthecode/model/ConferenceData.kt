package com.beyondthecode.model

data class ConferenceData(
    val sessions: List<Session>,
    val speakers: List<Speaker>,
    val rooms: List<Room>,
    val codelabs: List<Codelab>,
    val tags: List<Tag>,
    val version: Int
)