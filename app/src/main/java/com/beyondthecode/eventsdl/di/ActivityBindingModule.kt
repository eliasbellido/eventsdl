package com.beyondthecode.eventsdl.di

import com.beyondthecode.eventsdl.ui.LaunchModule
import com.beyondthecode.eventsdl.ui.LauncherActivity
import com.beyondthecode.eventsdl.ui.onboarding.OnboardingActivity
import com.beyondthecode.eventsdl.ui.onboarding.OnboardingModule
import com.beyondthecode.eventsdl.ui.signin.SignInDialogModule
import com.beyondthecode.shared.di.ActivityScoped
import dagger.Module
import dagger.android.ContributesAndroidInjector

/**
 * We want Dagger.Android to create a Subcomponent which has a parent Component of whichever module
 * ActivityBindingModule is on, in out case that will be [AppComponent]. You never
 * need to tell [AppComponent] that it0s going to have all these subcomponents
 * nor do you need to tell these subcomponents that [AppComponent] exists.
 * We're also telling Dagger.Android that this is generated Subcomponent needs to include the
 * specified modules and be aware of a scope annotation [@ActivityScoped].
 * When Dagger.Android annotation processor runs it will create 2 subcomponents for us.
 * */
@Module
@Suppress("UNUSED")
abstract class ActivityBindingModule{

    @ActivityScoped
    @ContributesAndroidInjector(modules = [LaunchModule::class])
    internal abstract fun launcherActivity(): LauncherActivity

    @ActivityScoped
    @ContributesAndroidInjector(
        modules = [
            OnboardingModule::class,
            SignInDialogModule::class

        ]
    )
    internal abstract fun onboardingActivity(): OnboardingActivity
}