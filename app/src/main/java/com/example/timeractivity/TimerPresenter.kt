package com.example.timeractivity

import com.arellomobile.mvp.InjectViewState
import com.arellomobile.mvp.MvpPresenter
import com.example.services.statestorage.StateStorageService
import com.example.timeractivity.lapsadapter.CurrentLap
import com.example.timeractivity.lapsadapter.FinishedLap
import com.example.timeractivity.lapsadapter.LapItem
import com.example.timeractivity.models.*
import com.example.timeractivity.uimodels.RunningTimer
import com.example.timeractivity.uimodels.StoppedTimer
import com.jakewharton.rxrelay2.BehaviorRelay
import com.jakewharton.rxrelay2.PublishRelay
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.functions.BiFunction
import io.reactivex.schedulers.Schedulers
import javax.inject.Inject


@InjectViewState
class TimerPresenter @Inject constructor(): MvpPresenter<TimerView>() {

    @Inject
    lateinit var stateStorageService: StateStorageService

    // Public
    fun leftButtonClicked() {
        val action = leftActionRelay.value ?: return
        actionRelay.accept(action)

    }

    fun rightButtonClicked() {
        val action = rightActionRelay.value ?: return
        actionRelay.accept(action)
    }

    override fun onFirstViewAttach() {
        super.onFirstViewAttach()

        state.map {
                return@map when (it) {
                    is Initial -> Action.RESET
                    is Running -> Action.LAP
                    is Paused -> Action.RESET
                }
            }
            .observeOn(AndroidSchedulers.mainThread())
            .doAfterNext { viewState.setLeftButtonTitle(titleFromAction(it)) }
            .subscribe(leftActionRelay)
            .let (disposable::add)

        state.map {
                return@map when (it) {
                    is Initial -> Action.START
                    is Running -> Action.STOP
                    is Paused -> Action.START
                }
            }
            .observeOn(AndroidSchedulers.mainThread())
            .doAfterNext { viewState.setRightButtonTitle(titleFromAction(it)) }
            .subscribe(rightActionRelay)
            .let(disposable::add)

        state.map {
                return@map when (it) {
                    is Initial -> StoppedTimer(0)
                    is Running -> RunningTimer(it.currentLap.duration, it.startAt)
                    is Paused -> StoppedTimer(it.currentLap.duration)
                }
            }
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe {
                viewState.setTimerState(it)
            }
            .let(disposable::add)

        state.map<Collection<LapItem>> {
                return@map when (it) {
                    is Initial -> listOf()
                    is Running -> arrayListOf<LapItem>(CurrentLap(it.currentLap.duration, it.startAt)).apply {
                        addAll(it.finishedLaps.map { lap -> FinishedLap(lap.duration) })
                    }
                    is Paused -> arrayListOf<LapItem>(FinishedLap(it.currentLap.duration)).apply {
                        addAll(it.finishedLaps.map { lap -> FinishedLap(lap.duration) })
                    }
                }
            }
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe {
                viewState.setLaps(it)
            }
            .let(disposable::add)

        actionRelay
            .withLatestFrom<State, State>(state, BiFunction { action, state -> reduce(state, action) })
            .subscribe(state)
            .let(disposable::add)

        stateStorageService
            .loadState()
            .subscribe(state)
            .let(disposable::add)

        state
            .distinctUntilChanged()
            .skip(1)
            .subscribeOn(Schedulers.io())
            .subscribe(stateStorageService::saveState)
            .let(disposable::add)

    }

    override fun onDestroy() {
        super.onDestroy()
        disposable.clear()
    }

    // Private
    private val disposable = CompositeDisposable()
    private val state = BehaviorRelay.create<State>()
    private val leftActionRelay = BehaviorRelay.create<Action>()
    private val rightActionRelay = BehaviorRelay.create<Action>()
    private val actionRelay = PublishRelay.create<Action>()

    private fun titleFromAction(action: Action): String {
        return when (action) {
            Action.START -> "Start"
            Action.STOP -> "Stop"
            Action.LAP -> "Lap"
            Action.RESET -> "Reset"
        }
    }

    // States
    private fun reduce(state: State, action: Action): State {
        return when (state) {
            is Initial -> handle(state, action)
            is Running -> handle(state, action)
            is Paused -> handle(state, action)
        }
    }

    private fun handle(state: Initial, action: Action): State {
        return when (action) {
            Action.START -> Running(System.currentTimeMillis(), Lap(0), emptyList())
            Action.RESET -> Initial
            else -> state
        }
    }

    private fun handle(state: Running, action: Action): State {
        val now = System.currentTimeMillis()
        val lap = Lap(state.currentLap.duration + now - state.startAt)
        return when (action) {
            Action.STOP -> Paused(lap, state.finishedLaps)
            Action.LAP -> Running(now, Lap(0), mutableListOf(lap).apply { addAll(state.finishedLaps) })
            else -> state
        }
    }

    private fun handle(state: Paused, action: Action): State {
        return when (action) {
            Action.RESET -> Initial
            Action.START -> Running(System.currentTimeMillis(), state.currentLap, state.finishedLaps)
            else -> state
        }
    }
}
