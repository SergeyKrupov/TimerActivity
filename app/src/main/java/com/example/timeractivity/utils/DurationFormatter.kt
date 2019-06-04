package com.example.timeractivity.utils

object DurationFormatter {

    fun stringFromDuration(duration: Long): String {
        val minutes = duration / 1000 / 60
        val seconds = (duration / 1000) % 60
        val fraction = (duration % 1000) / 10
        return String.format("%02d:%02d,%02d", minutes, seconds, fraction)
    }
}
