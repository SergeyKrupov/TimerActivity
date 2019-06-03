package com.example.timeractivity

import android.os.Bundle
import android.os.Handler
import androidx.recyclerview.widget.LinearLayoutManager
import com.arellomobile.mvp.MvpAppCompatActivity
import com.arellomobile.mvp.presenter.InjectPresenter
import com.arellomobile.mvp.presenter.ProvidePresenter
import com.example.timeractivity.lapsadapter.LapItem
import com.example.timeractivity.lapsadapter.LapsAdapter
import com.example.timeractivity.uimodels.RunningTimer
import com.example.timeractivity.uimodels.TimerState
import dagger.android.AndroidInjection
import kotlinx.android.synthetic.main.activity_main.*
import javax.inject.Inject
import javax.inject.Provider

class TimerActivity : MvpAppCompatActivity(), TimerView {

    // Dagger
    @Inject
    lateinit var daggerPresenter: Provider<TimerPresenter>

    @Inject
    lateinit var adapter: LapsAdapter

    // Moxy
    @InjectPresenter
    lateinit var presenter: TimerPresenter

    @ProvidePresenter
    fun providePresenter(): TimerPresenter = daggerPresenter.get()

    // Lifecycle
    override fun onCreate(savedInstanceState: Bundle?) {
        AndroidInjection.inject(this)
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        lapsRecyclerView.adapter = adapter
        lapsRecyclerView.layoutManager = LinearLayoutManager(this)

        leftButton.setOnClickListener {
            presenter.leftButtonClicked()
        }
        rightButton.setOnClickListener {
            presenter.rightButtonClicked()
        }
    }

    override fun onStop() {
        super.onStop()
        timerTickHandler.removeCallbacks(timerRunnable)
    }

    override fun onRestart() {
        super.onRestart()
        timerTickHandler.postDelayed(timerRunnable, 0)
    }

    // TimerView
    override fun setLeftButtonTitle(title: String) {
        leftButton.text = title
    }

    override fun setRightButtonTitle(title: String) {
        rightButton.text = title
    }

    override fun setTimerState(timerState: TimerState) {
        timerTickHandler.removeCallbacks(timerRunnable)

        timeTextView.text = formatDuration(timerState.duration)

        if (timerState is RunningTimer) {
            timerRunnable.timerState = timerState
            timerTickHandler.postDelayed(timerRunnable, 0)
        }
    }

    override fun setLaps(laps: Collection<LapItem>) {
        adapter.setLaps(laps)
    }

    // Timer
    private val timerTickHandler = Handler()

    private val timerRunnable = object: Runnable {

        var timerState: RunningTimer? = null

        override fun run() {
            val state = timerState ?: return
            timeTextView.text = formatDuration(state.duration + System.currentTimeMillis() - state.startAt)
            timerTickHandler.postDelayed(this, 100)
        }
    }

    private fun formatDuration(duration: Long): String {
        return (duration / 1000.0).toString()
    }
}
