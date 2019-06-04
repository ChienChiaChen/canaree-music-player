package dev.olog.msc.presentation.sleeptimer

import android.app.AlarmManager
import android.content.Context
import android.os.Bundle
import android.os.SystemClock
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.annotation.Keep
import dev.olog.msc.core.interactor.SleepTimerUseCase
import dev.olog.msc.presentation.base.extensions.act
import dev.olog.msc.presentation.navigator.Services
import dev.olog.msc.presentation.sleeptimer.di.inject
import dev.olog.msc.shared.PendingIntents
import dev.olog.msc.shared.core.coroutines.CustomScope
import dev.olog.msc.shared.core.flow.flowInterval
import dev.olog.msc.shared.extensions.toast
import dev.olog.msc.shared.ui.TimeUtils
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.takeWhile
import kotlinx.coroutines.launch
import java.util.concurrent.TimeUnit
import javax.inject.Inject

@Keep
class SleepTimerPickerDialog : ScrollHmsPickerDialog(),
    ScrollHmsPickerDialog.HmsPickHandler,
    CoroutineScope by CustomScope() {

    private var countDownJob: Job? = null

    private lateinit var fakeView: View
    private lateinit var okButton: Button

    @Inject
    lateinit var sleepTimerUseCase: SleepTimerUseCase

    override fun onAttach(context: Context) {
        inject()
        super.onAttach(context)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = super.onCreateView(inflater, container, savedInstanceState)!!

        okButton = view.findViewById(R.id.button_ok)

        val (sleepFrom, sleepTime) = sleepTimerUseCase.getLast()

        setTimeInMilliseconds(sleepTime - (System.currentTimeMillis() - sleepFrom), false)

        fakeView = view.findViewById(R.id.fakeView)
        toggleVisibility(fakeView, sleepTime > 0)

        toggleButtons(sleepTime > 0)

        if (sleepTime > 0) {
            countDownJob?.cancel()
            countDownJob = launch {
                try {
                    flowInterval(1, TimeUnit.SECONDS)
                        .map { sleepTime - (System.currentTimeMillis() - sleepFrom) }
                        .takeWhile { it >= 0L }
                        .collect {
                            setTimeInMilliseconds(it, true)
                        }
                } catch (ex: Exception) {
                    ex.printStackTrace()
                    resetPersistedValues()
                    toggleButtons(false)
                }
            }
        }

        pickListener = this

        return view
    }

    override fun onResume() {
        super.onResume()
        okButton.setOnClickListener {
            if (it.isSelected) {
                // as reset button
                setTimeInMilliseconds(0, true)
                countDownJob?.cancel()
                toggleButtons(false)
                resetPersistedValues()
                resetAlarmManager()
            } else {
                // as ok button
                if (TimeUtils.timeAsMillis(hmsPicker.hours, hmsPicker.minutes, hmsPicker.seconds) > 0) {
                    pickListener?.onHmsPick(-1, hmsPicker.hours, hmsPicker.minutes, hmsPicker.seconds)
                    act.toast(R.string.sleep_timer_set)
                    dismiss()
                } else {
                    act.toast(R.string.sleep_timer_can_not_be_set)
                }
            }
        }
    }

    override fun onPause() {
        super.onPause()
        okButton.setOnClickListener(null)
    }

    override fun onDestroy() {
        super.onDestroy()
        countDownJob?.cancel()
    }

    private fun toggleButtons(isCountDown: Boolean) {
        val okText = if (isCountDown) {
            R.string.scroll_hms_picker_stop
        } else android.R.string.ok

        okButton.setText(okText)
        okButton.isSelected = isCountDown
        toggleVisibility(fakeView, isCountDown)
    }

    private fun toggleVisibility(view: View, showCondition: Boolean) {
        val visibility = if (showCondition) View.VISIBLE else View.GONE
        view.visibility = visibility
    }

    private fun setTimeInMilliseconds(millis: Long, smooth: Boolean) {
        val totalSeconds = (millis / 1000).toInt()

        val hours = totalSeconds / 3600
        val remaining = totalSeconds % 3600
        val minutes = remaining / 60
        val seconds = remaining % 60
        hmsPicker.setTime(hours, minutes, seconds, smooth)
    }

    override fun onHmsPick(reference: Int, hours: Int, minutes: Int, seconds: Int) {
        val sleep = TimeUtils.timeAsMillis(hours, minutes, seconds)
        val currentTime = System.currentTimeMillis()

        persistValues(currentTime, sleep)
        setAlarmManager(hours, minutes, seconds)
    }

    private fun resetPersistedValues() {
        persistValues(-1, -1)
    }

    private fun persistValues(sleepFrom: Long, sleepTime: Long) {
        sleepTimerUseCase.set(sleepFrom, sleepTime)
    }

    private fun resetAlarmManager() {
        val intent = PendingIntents.stopMusicServiceIntent(context!!, Services.music())
        val alarmManager = requireContext().getSystemService(Context.ALARM_SERVICE) as AlarmManager
        alarmManager.cancel(intent)
    }

    private fun setAlarmManager(hours: Int, minutes: Int, seconds: Int) {
        val nextSleep = SystemClock.elapsedRealtime() +
                TimeUnit.HOURS.toMillis(hours.toLong()) +
                TimeUnit.MINUTES.toMillis(minutes.toLong()) +
                TimeUnit.SECONDS.toMillis(seconds.toLong())

        val intent = PendingIntents.stopMusicServiceIntent(context!!, Services.music())
        val alarmManager = requireContext().getSystemService(Context.ALARM_SERVICE) as AlarmManager
        alarmManager.set(AlarmManager.ELAPSED_REALTIME_WAKEUP, nextSleep, intent)
    }

}