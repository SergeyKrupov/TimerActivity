package com.example.services.statestorage

import com.example.timeractivity.models.State
import io.reactivex.Single

interface StateStorageService {
    fun saveState(state: State)
    fun loadState(): Single<State>
}
