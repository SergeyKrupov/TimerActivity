package com.example.timeractivity

import com.arellomobile.mvp.MvpView
import com.arellomobile.mvp.viewstate.strategy.AddToEndSingleStrategy
import com.arellomobile.mvp.viewstate.strategy.StateStrategyType
import com.example.timeractivity.lapsadapter.LapItem
import com.example.timeractivity.uimodels.TimerState

interface TimerView: MvpView {

    @StateStrategyType(AddToEndSingleStrategy::class)
    fun setLeftButtonTitle(title: String)

    @StateStrategyType(AddToEndSingleStrategy::class)
    fun setRightButtonTitle(title: String)

    @StateStrategyType(AddToEndSingleStrategy::class)
    fun setTimerState(timerState: TimerState)

    @StateStrategyType(AddToEndSingleStrategy::class)
    fun setLaps(laps: Collection<LapItem>)
}
