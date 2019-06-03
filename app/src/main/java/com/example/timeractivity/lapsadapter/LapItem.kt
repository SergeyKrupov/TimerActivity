package com.example.timeractivity.lapsadapter

sealed class LapItem(
    val duration: Long
)

class FinishedLap(
    duration: Long
): LapItem(duration)

class CurrentLap(
    duration: Long,
    val startAt: Long
): LapItem(duration)
