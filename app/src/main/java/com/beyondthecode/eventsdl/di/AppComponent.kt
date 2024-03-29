package com.beyondthecode.eventsdl.di

import com.beyondthecode.eventsdl.MainApplication
import com.beyondthecode.eventsdl.ui.signin.SignInViewModelDelegateModule
import com.beyondthecode.shared.di.ViewModelModule
import dagger.BindsInstance
import dagger.Component
import dagger.android.AndroidInjector
import dagger.android.support.AndroidSupportInjectionModule
import javax.inject.Singleton

/**
 * Main component of the app, created and persisted in the Application class.
 *
 * Whenever a new module is created, it should be added to the list of modules.
 * [AndroidSupportInjectionModule] is the module from Dagger.Android that helps with the
 * generation and location of subcomponents.
 * */
@Singleton
@Component(
    modules = [
        AndroidSupportInjectionModule::class,
        AppModule::class,
        ActivityBindingModule::class,
        ViewModelModule::class,
        SignInViewModelDelegateModule::class
    ]
)
interface AppComponent : AndroidInjector<MainApplication>{
    @Component.Factory
    interface Factory {
        fun create(@BindsInstance application : MainApplication) : AppComponent
    }
}