package com.beyondthecode.eventsdl

import android.os.StrictMode
import android.os.StrictMode.ThreadPolicy.Builder
import com.beyondthecode.eventsdl.di.DaggerAppComponent
import dagger.android.AndroidInjector
import dagger.android.DaggerApplication
import timber.log.Timber

/**
 * Initialization of libraries
 * */
class MainApplication : DaggerApplication(){

    override fun onCreate() {


        //Enable strict mode before Dagger creates graph
        if(BuildConfig.DEBUG){
            enableStrictMode()
        }
        super.onCreate()

        if(BuildConfig.DEBUG){
            Timber.plant(Timber.DebugTree())
        }
    }

    override fun applicationInjector(): AndroidInjector<out DaggerApplication> {
        return DaggerAppComponent.factory().create(this)
    }

    private fun enableStrictMode(){
        StrictMode.setThreadPolicy(
            Builder()
                .detectDiskReads()
                .detectDiskWrites()
                .detectNetwork()
                .penaltyLog()
                .build()
        )
    }
}