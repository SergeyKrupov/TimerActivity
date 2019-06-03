package com.example.timeractivity.lapsadapter

import android.os.Handler
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.timeractivity.R
import kotlinx.android.synthetic.main.lap_cell.view.*

sealed class LapViewHolder(
    parent: ViewGroup
): RecyclerView.ViewHolder(
    LayoutInflater.from(parent.context).inflate(R.layout.lap_cell, parent, false)
) {
    fun formatLapName(position: Int) = String.format("Lap %s", position)
    fun formatDuration(duration: Long) = String.format("%.2f", duration / 1000.0)

    open fun onAttachToWindow() {}

    open fun onDetachFromWindow() {}
}

class CurrentLapViewHolder(
    parent: ViewGroup
): LapViewHolder(parent) {

    fun setup(item: CurrentLap, position: Int) {
        itemView.lapNameTextView.text = formatLapName(position)
        itemView.lapValueTextView.text = formatDuration(item.duration)
        timerRunnable.lap = item
    }

    override fun onAttachToWindow() {
        timerTickHandler.postDelayed(timerRunnable, 0)
    }

    override fun onDetachFromWindow() {
        timerTickHandler.removeCallbacks(timerRunnable)
    }

    // Private
    private val timerTickHandler = Handler()

    private val timerRunnable = object: Runnable {

        var lap: CurrentLap? = null

        override fun run() {
            val lap = this.lap ?: return
            itemView.lapValueTextView.text = formatDuration(lap.duration + System.currentTimeMillis() - lap.startAt)
            timerTickHandler.postDelayed(this, 100)
        }
    }
}

class FinishedLapViewHolder(
    parent: ViewGroup
): LapViewHolder(parent) {

    fun setup(item: FinishedLap, position: Int) {
        itemView.lapNameTextView.text = formatLapName(position)
        itemView.lapValueTextView.text = formatDuration(item.duration)
    }
}
