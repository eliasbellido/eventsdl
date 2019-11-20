package com.beyondthecode.eventsdl.ui

import androidx.lifecycle.ViewModel
import com.beyondthecode.shared.di.ViewModelKey
import dagger.Binds
import dagger.Module
import dagger.multibindings.IntoMap

/**
 * Module where classes needed for app launch are defined
 */
@Module
@Suppress("UNUSED")
internal abstract class LaunchModule {
    /**
     * The ViewModels are created by Dagger in a map. Via the @VIewModelKey, we
     * want to get a [LaunchViewModel] class.
     * */
    @Binds
    @IntoMap
    @ViewModelKey(LaunchViewModel::class)
    internal abstract fun bindLaunchViewModel(viewModel: LaunchViewModel): ViewModel
}