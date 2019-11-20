package com.beyondthecode.eventsdl.di

import android.content.Context
import com.beyondthecode.eventsdl.MainApplication
import com.beyondthecode.shared.data.prefs.PreferenceStorage
import com.beyondthecode.shared.data.prefs.SharedPreferenceStorage
import dagger.Module
import dagger.Provides
import javax.inject.Singleton

/**
 * Defines all the classes that need to be provided in the scope of the app.
 *
 * Define here all objects that are shared throughout the app, like SharePreferences, navigators or
 * others. if some of those objects are singletons, they should be annotated with ´@Singleton´.
 * */
@Module
class AppModule {

    @Provides
    fun provideContext(app: MainApplication): Context {
        return app.applicationContext
    }

    @Singleton
    @Provides
    fun providesPreferenceStorage(context: Context) : PreferenceStorage =
        SharedPreferenceStorage(context)
}