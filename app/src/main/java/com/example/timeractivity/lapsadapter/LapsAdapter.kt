package com.example.timeractivity.lapsadapter

import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import java.lang.AssertionError
import javax.inject.Inject

const val CURRENT_LAP_VIEW_TYPE = 1000
const val FINISHED_LAP_VIEW_TYPE = 2000

class LapsAdapter @Inject constructor(): RecyclerView.Adapter<LapViewHolder>() {

    // Public
    fun setLaps(laps: Collection<LapItem>) {
        items.clear()
        items.addAll(laps)
        notifyDataSetChanged()
    }

    // Adapter
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): LapViewHolder {
        return when (viewType) {
            CURRENT_LAP_VIEW_TYPE -> CurrentLapViewHolder(parent)
            FINISHED_LAP_VIEW_TYPE -> FinishedLapViewHolder(parent)
            else -> throw AssertionError("Not implemented")
        }
    }

    override fun getItemCount(): Int {
        return items.size
    }

    override fun onBindViewHolder(holder: LapViewHolder, position: Int) {
        val item = items[position]
        if (item is CurrentLap && holder is CurrentLapViewHolder) {
            holder.setup(item, items.size - position)
        }
        if (item is FinishedLap && holder is FinishedLapViewHolder) {
            holder.setup(item, items.size - position)
        }
    }

    override fun getItemViewType(position: Int): Int {
        return when(items[position]) {
            is CurrentLap -> CURRENT_LAP_VIEW_TYPE
            is FinishedLap -> FINISHED_LAP_VIEW_TYPE
        }
    }

    override fun onViewAttachedToWindow(holder: LapViewHolder) {
        super.onViewAttachedToWindow(holder)
        holder.onAttachToWindow()
    }

    override fun onViewDetachedFromWindow(holder: LapViewHolder) {
        super.onViewDetachedFromWindow(holder)
        holder.onDetachFromWindow()
    }

    // Private
    private var items = arrayListOf<LapItem>()
}
