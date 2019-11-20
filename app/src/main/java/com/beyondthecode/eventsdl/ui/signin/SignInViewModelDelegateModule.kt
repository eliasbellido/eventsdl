package com.beyondthecode.eventsdl.ui.signin

import dagger.Module
import dagger.Provides
import javax.inject.Singleton

@Module
class SignInViewModelDelegateModule {

    @Singleton
    @Provides
    fun provideSignInViewModelDelegate() : SignInViewModelDelegate{

    }

}