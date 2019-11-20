package com.beyondthecode.model

typealias SpeakerId = String

/**
 * Describes a speaker at the conference
 * */
data class Speaker(

    //unique string indetifying this speaker
    val id: SpeakerId,

    //name of this speaker
    val name: String,

    //profile photo of this speaker
    val imageUrl: String,

    //company this speaker works for
    val company: String,

    //text describing this speaker in detail
    val biography: String,

    //full URL of the speaker's website
    val websiteUrl: String? = null,

    //full URL of the speaker's twitter profile
    val twitterUrl: String? = null,

    //full URL of the speaker's github profile
    val githubUrl: String?= null,

    //full URL of the speaker's linkedin profile
    val linkedinUrl: String? = null
){
    val hasCompany inline get() = company.isNotEmpty()

}