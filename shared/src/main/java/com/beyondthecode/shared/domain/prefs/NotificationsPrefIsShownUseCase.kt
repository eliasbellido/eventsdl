package com.beyondthecode.shared.domain.prefs

import com.beyondthecode.shared.data.prefs.PreferenceStorage
import com.beyondthecode.shared.domain.UseCase
import javax.inject.Inject

/**
 * Returns whether the notifications preference has been shown to the user.
 * */
open class NotificationsPrefIsShownUseCase @Inject constructor(
    private val preferenceStorage: PreferenceStorage
) : UseCase<Unit, Boolean>(){

    override fun execute(parameters: Unit): Boolean = preferenceStorage.notificationsPreferenceShown

}