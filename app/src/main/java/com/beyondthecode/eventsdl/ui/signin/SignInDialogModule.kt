package com.beyondthecode.eventsdl.ui.signin

import androidx.lifecycle.ViewModel
import com.beyondthecode.shared.di.ChildFragmentScoped
import com.beyondthecode.shared.di.ViewModelKey
import dagger.Binds
import dagger.Module
import dagger.android.ContributesAndroidInjector
import dagger.multibindings.IntoMap

/**
 * Module that defines the child fragments related to sign in/out.
 * */
@Module
internal abstract class SignInDialogModule{

    /**
     * Generates an [dagger.android.AndroidInjector] for the [SignInDialogFragment].
     * */
    @ChildFragmentScoped
    @ContributesAndroidInjector
    internal abstract fun contributeSignInDialogFragment() : SignInDialogFragment


    /**
     * The ViewModels are created by Dagger in a map. Via the @ViewModelKey, we define that we
     * want to get a [SignInViewModel] class.
     * */
    @Binds
    @IntoMap
    @ViewModelKey(SignInViewModel::class)
    abstract fun bindSignInViewModel(viewModel: SignInViewModel) : ViewModel
}