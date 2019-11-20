package com.beyondthecode.shared.domain.prefs

import com.beyondthecode.shared.data.prefs.PreferenceStorage
import com.beyondthecode.shared.domain.UseCase
import javax.inject.Inject

/**
 * Records whether onboarding has been completed.
 * */
open class OnboardingCompleteActionUseCase @Inject constructor(
    private val preferenceStorage: PreferenceStorage
): UseCase<Boolean, Unit>(){
    override fun execute(parameters: Boolean) {
        preferenceStorage.onboardingCompleted = parameters
    }
}