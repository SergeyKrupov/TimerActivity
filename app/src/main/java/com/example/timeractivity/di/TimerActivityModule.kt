package com.example.timeractivity.di

import com.example.timeractivity.TimerActivity
import dagger.Module
import dagger.android.ContributesAndroidInjector

@Module
abstract class TimerActivityModule {

    @ContributesAndroidInjector
    abstract fun contributeTimerActivityInjector(): TimerActivity
}
