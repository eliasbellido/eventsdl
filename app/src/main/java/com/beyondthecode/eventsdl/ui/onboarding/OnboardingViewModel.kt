package com.beyondthecode.eventsdl.ui.onboarding

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.beyondthecode.eventsdl.ui.signin.SignInViewModelDelegate
import com.beyondthecode.shared.domain.prefs.OnboardingCompleteActionUseCase
import com.beyondthecode.shared.result.Event
import javax.inject.Inject

/**
 * Records that onboarding has been completed and navigate user onward.
 * */
class OnboardingViewModel @Inject constructor(
    private val onboardingCompleteActionUseCase: OnboardingCompleteActionUseCase,
    signInViewModelDelegate: SignInViewModelDelegate
): ViewModel(), SignInViewModelDelegate by signInViewModelDelegate{

    private val _navigateToMainActivity = MutableLiveData<Event<Unit>>()
    val navigateToMainActivity: LiveData<Event<Unit>> = _navigateToMainActivity

    private val _navigateToSignInDialogAction = MutableLiveData<Event<Unit>>()
    val navigateToSignInDialogAction: LiveData<Event<Unit>> = _navigateToSignInDialogAction

    fun getStartedClick(){
        onboardingCompleteActionUseCase(true)
        _navigateToMainActivity.postValue(Event(Unit))
    }

    fun onSigninClicked(){
        _navigateToSignInDialogAction.value = Event(Unit)
    }
}