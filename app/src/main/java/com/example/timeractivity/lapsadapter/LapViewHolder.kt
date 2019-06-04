package com.example.timeractivity.lapsadapter

import android.os.Handler
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.timeractivity.R
import com.example.timeractivity.utils.DurationFormatter
import kotlinx.android.synthetic.main.lap_cell.view.*

sealed class LapViewHolder(
    parent: ViewGroup
): RecyclerView.ViewHolder(
    LayoutInflater.from(parent.context).inflate(R.layout.lap_cell, parent, false)
) {
    fun formatLapName(position: Int) = String.format("Lap %s", position)

    open fun onAttachToWindow() {}

    open fun onDetachFromWindow() {}
}

class CurrentLapViewHolder(
    parent: ViewGroup
): LapViewHolder(parent) {

    fun setup(item: CurrentLap, position: Int) {
        itemView.lapNameTextView.text = formatLapName(position)
        itemView.lapValueTextView.text = DurationFormatter.stringFromDuration(item.duration)
        timerRunnable.lap = item
    }

    override fun onAttachToWindow() {
        itemView.postDelayed(timerRunnable, 0)
    }

    override fun onDetachFromWindow() {
        itemView.removeCallbacks(timerRunnable)
    }

    // Private
    private val timerRunnable = object: Runnable {

        var lap: CurrentLap? = null

        override fun run() {
            val lap = this.lap ?: return
            itemView.lapValueTextView.text = DurationFormatter.stringFromDuration(lap.duration + System.currentTimeMillis() - lap.startAt)
            itemView.postDelayed(this, 100)
        }
    }
}

class FinishedLapViewHolder(
    parent: ViewGroup
): LapViewHolder(parent) {

    fun setup(item: FinishedLap, position: Int) {
        itemView.lapNameTextView.text = formatLapName(position)
        itemView.lapValueTextView.text = DurationFormatter.stringFromDuration(item.duration)
    }
}
