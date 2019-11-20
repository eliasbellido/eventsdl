package com.beyondthecode.shared.di

import androidx.lifecycle.ViewModelProvider
import dagger.Binds
import dagger.Module

/**
 * Module used to define the connection between the framework's [ViewModelProvider.Factory] and
 * our own implementation [EventsdlViewModelFactory]
 * */
@Module
@Suppress
abstract class ViewModelModule{

    @Binds
    internal abstract fun bindViewModelFactory(factory : EventsdlViewModelFactory):
            ViewModelProvider.Factory
}