package com.beyondthecode.shared.util

import com.beyondthecode.model.Speaker

object SpeakerUtils{
    fun alphabeticallyOrderedSpeakerList(speakerSet: Set<Speaker>) =
        ArrayList<Speaker>(speakerSet).sortedBy { it.name }
}