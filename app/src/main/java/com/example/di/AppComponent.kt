package com.example.di

import android.content.Context
import com.example.TimerApplication
import com.example.services.ServicesModule
import com.example.timeractivity.di.TimerActivityModule
import dagger.BindsInstance
import dagger.Component
import dagger.android.AndroidInjectionModule
import javax.inject.Scope

@AppScope
@Component(modules = [TimerActivityModule::class, AndroidInjectionModule::class, ServicesModule::class])
interface AppComponent {
    fun inject(app: TimerApplication)

    @Component.Builder
    interface Builder {
        @BindsInstance
        fun appContext(context: Context): Builder
        fun build(): AppComponent
    }
}

@Scope
@Retention(AnnotationRetention.RUNTIME)
annotation class AppScope
