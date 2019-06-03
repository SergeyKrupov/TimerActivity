package com.example.timeractivity.models

sealed class State

object Initial : State()

data class Running(
    val startAt: Long,
    val currentLap: Lap,
    val finishedLaps: List<Lap>
): State()

data class Paused(
    val currentLap: Lap,
    val finishedLaps: List<Lap>
): State()
