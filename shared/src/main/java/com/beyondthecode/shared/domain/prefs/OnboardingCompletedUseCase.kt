package com.beyondthecode.shared.domain.prefs

import com.beyondthecode.shared.data.prefs.PreferenceStorage
import com.beyondthecode.shared.domain.UseCase
import javax.inject.Inject

/**
 * Returns whether onboarding has been completed.
 * */
open class OnboardingCompletedUseCase @Inject constructor(
    private val preferenceStorage: PreferenceStorage
): UseCase<Unit, Boolean>(){
    override fun execute(parameters: Unit): Boolean = preferenceStorage.onboardingCompleted
}