package com.beyondthecode.eventsdl.ui

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.beyondthecode.shared.domain.prefs.OnboardingCompletedUseCase
import com.beyondthecode.shared.result.Event
import com.beyondthecode.shared.result.Result
import com.beyondthecode.shared.util.map
import javax.inject.Inject

/**
 * Logic for determining which screen to send users to on app launch
 * */
class LaunchViewModel @Inject constructor(
    onboardingCompletedUseCase: OnboardingCompletedUseCase
): ViewModel(){

    private val onboardingCompletedResult = MutableLiveData<Result<Boolean>>()
    val launchDestination: LiveData<Event<LaunchDestination>>

    init {
        //Check if onboarding has already benn completed and then navigate the user accordingly
        onboardingCompletedUseCase(Unit, onboardingCompletedResult)
        launchDestination = onboardingCompletedResult.map {
            //If this check fails, prefer to launch main activity than show onboarding too often
            if((it as? Result.Success)?.data == false){
                Event(LaunchDestination.ONBOARDING)
            }else{
                Event(LaunchDestination.MAIN_ACTIVITY)
            }
        }
    }
}

enum class LaunchDestination{
    ONBOARDING,
    MAIN_ACTIVITY
}