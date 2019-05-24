package dev.olog.msc.presentation.base

import android.preference.PreferenceManager
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.edit
import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.LifecycleOwner
import dev.olog.msc.shared.core.lazyFast
import kotlinx.coroutines.*
import javax.inject.Inject

private var counterAlreadyIncreased = false

private const val PREFS_APP_STARTED_COUNT = "prefs.app.started.count"
private const val PREFS_APP_RATE_NEVER_SHOW_AGAIN = "prefs.app.rate.never.show"

class RateAppDialog @Inject constructor(
        private val activity: AppCompatActivity

): DefaultLifecycleObserver {

    private var job : Job? = null

    private val prefs by lazyFast { PreferenceManager.getDefaultSharedPreferences(activity.applicationContext) }

    init {
        activity.lifecycle.addObserver(this)
        check()
    }

    private fun check(){
        job = GlobalScope.launch {
            val canShow = updateCounter()
            delay(2000) // small delay
            if (canShow){
                withContext(Dispatchers.Main){
                    showDialog()
                }
            }
        }
    }

    override fun onDestroy(owner: LifecycleOwner) {
        job?.cancel()
    }

    private fun showDialog(){
        AlertDialog.Builder(activity)
            .setTitle(R.string.rate_app_title)
            .setMessage(R.string.rate_app_message)
            .setPositiveButton(R.string.rate_app_positive_button) { _, _ ->
                setNeverShowAgain()
                openPlayStore(activity)
            }
            .setNegativeButton(R.string.rate_app_negative_button) { _, _ -> setNeverShowAgain() }
            .setNeutralButton(R.string.rate_app_neutral_button) { _, _ ->  }
            .setCancelable(false)
            .show()
    }

    /**
     * @return true when is requested to show rate dialog
     */
    private fun updateCounter(): Boolean {
        return if (!counterAlreadyIncreased){
            counterAlreadyIncreased = true

            val oldValue = prefs.getInt(PREFS_APP_STARTED_COUNT, 0)
            val newValue = oldValue + 1
            prefs.edit { putInt(PREFS_APP_STARTED_COUNT, newValue) }

            newValue.rem(20) == 0 && !isNeverShowAgain()
        } else {
            false
        }
    }

    private fun isNeverShowAgain(): Boolean{
        return prefs.getBoolean(PREFS_APP_RATE_NEVER_SHOW_AGAIN, false)
    }

    private fun setNeverShowAgain(){
        prefs.edit { putBoolean(PREFS_APP_RATE_NEVER_SHOW_AGAIN, true) }
    }

}