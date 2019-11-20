package com.beyondthecode.shared.di

import com.beyondthecode.shared.notifications.AlarmBroadcastReceiver
import com.beyondthecode.shared.notifications.CancelNotificationBroadcastReceiver
import dagger.Module
import dagger.android.ContributesAndroidInjector

@Module
abstract class BroadcastReceiverbindingModule {
    @BroadcastReceiverScoped
    @ContributesAndroidInjector
    internal abstract fun alarmBroadcastReceiver(): AlarmBroadcastReceiver

    @BroadcastReceiverScoped
    @ContributesAndroidInjector
    internal abstract fun cancelNotificationBroadcastReceiver(): CancelNotificationBroadcastReceiver

}