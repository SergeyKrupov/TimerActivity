package com.example.timeractivity.uimodels

sealed class TimerState(
    val duration: Long
)

class StoppedTimer(
    duration: Long
): TimerState(duration)

class RunningTimer(
    duration: Long,
    val startAt: Long
): TimerState(duration)
