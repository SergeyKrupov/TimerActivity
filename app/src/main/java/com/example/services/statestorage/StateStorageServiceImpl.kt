package com.example.services.statestorage

import android.content.Context
import android.content.SharedPreferences
import androidx.core.content.edit
import com.example.timeractivity.models.*
import com.orhanobut.hawk.Hawk
import io.reactivex.Single
import io.reactivex.schedulers.Schedulers
import javax.inject.Inject

const val STATE_TYPE = "type"
const val STATE_TYPE_INITIAL = "initial"
const val STATE_TYPE_RUNNING = "running"
const val STATE_TYPE_PAUSED = "paused"

const val CURRENT_LAP = "current.lap"
const val FINISHED_LAPS = "finished.laps"
const val START_AT = "start.at"

class StateStorageServiceImpl @Inject constructor(
    context: Context
): StateStorageService {

    init {
        Hawk.init(context).build()
    }

    override fun saveState(state: State) {
        Hawk.deleteAll()
        when (state) {
            is Initial ->
                Hawk.put(STATE_TYPE, STATE_TYPE_INITIAL)

            is Running -> {
                Hawk.put(STATE_TYPE, STATE_TYPE_RUNNING)
                Hawk.put(CURRENT_LAP, state.currentLap.duration)
                Hawk.put(FINISHED_LAPS, state.finishedLaps.map { it.duration })
                Hawk.put(START_AT, state.startAt)
            }

            is Paused -> {
                Hawk.put(STATE_TYPE, STATE_TYPE_PAUSED)
                Hawk.put(CURRENT_LAP, state.currentLap.duration)
                Hawk.put(FINISHED_LAPS, state.finishedLaps.map { it.duration })
            }
        }
    }

    override fun loadState(): Single<State> {
        return Single.create<State> {

            val state: State = when (Hawk.get(STATE_TYPE, STATE_TYPE_INITIAL)) {
                STATE_TYPE_PAUSED ->
                    Paused(
                        Lap(Hawk.get(CURRENT_LAP, 0)),
                        Hawk.get(FINISHED_LAPS, listOf<Long>()).map { Lap(it) }
                    )

                STATE_TYPE_RUNNING ->
                    Running(
                        Hawk.get(START_AT, 0),
                        Lap(Hawk.get(CURRENT_LAP, 0)),
                        Hawk.get(FINISHED_LAPS, listOf<Long>()).map { Lap(it) }
                    )

                else ->
                    Initial
            }

            it.onSuccess(state)
        }
        .subscribeOn(Schedulers.io())
    }
}
