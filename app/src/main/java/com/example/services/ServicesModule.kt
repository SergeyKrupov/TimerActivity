package com.example.services

import com.example.di.AppScope
import com.example.services.statestorage.StateStorageService
import com.example.services.statestorage.StateStorageServiceImpl
import dagger.Binds
import dagger.Module

@Module
abstract class ServicesModule {

    @Binds
    @AppScope
    abstract fun bindStateStorageService(serivce: StateStorageServiceImpl): StateStorageService
}
