package dev.olog.msc.presentation.base.theme.player.theme

import android.content.Context
import android.content.SharedPreferences
import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleOwner
import com.f2prateek.rx.preferences2.RxSharedPreferences
import dev.olog.msc.core.dagger.qualifier.ApplicationContext
import dev.olog.msc.core.dagger.qualifier.ProcessLifecycle
import dev.olog.msc.presentation.base.R
import io.reactivex.BackpressureStrategy
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.drop
import kotlinx.coroutines.reactive.flow.asFlow
import javax.inject.Inject

class PlayerTheme @Inject constructor(
    @ApplicationContext private val context: Context,
    @ProcessLifecycle lifecycle: Lifecycle,
    private val prefs: SharedPreferences,
    private val rxPrefs: RxSharedPreferences
) : DefaultLifecycleObserver, IPlayerTheme {

    private var job: Job? = null
    private var THEME = PlayerThemeEnum.DEFAULT

    init {
        lifecycle.addObserver(this)

    }

    override fun onStart(owner: LifecycleOwner) {
        setInitialValue()
        job?.cancel()
        job = GlobalScope.launch {
            rxPrefs.getString(
                context.getString(R.string.prefs_appearance_key),
                context.getString(R.string.prefs_appearance_entry_value_default)
            )
                .asObservable()
                .toFlowable(BackpressureStrategy.LATEST)
                .asFlow()
                .drop(1) // skip initial emission
                .collect {
                    withContext(Dispatchers.Main) {
                        onThemeChanged(it)
                    }
                }
        }
    }

    override fun onStop(owner: LifecycleOwner) {
        job?.cancel()
    }

    private fun setInitialValue() {
        val initialTheme = prefs.getString(
            context.getString(R.string.prefs_appearance_key),
            context.getString(R.string.prefs_appearance_entry_value_default)
        )!!
        onThemeChanged(initialTheme)
    }

    private fun onThemeChanged(theme: String) {
        THEME = when (theme) {
            context.getString(R.string.prefs_appearance_entry_value_default) -> PlayerThemeEnum.DEFAULT
            context.getString(R.string.prefs_appearance_entry_value_flat) -> PlayerThemeEnum.FLAT
            context.getString(R.string.prefs_appearance_entry_value_spotify) -> PlayerThemeEnum.SPOTIFY
            context.getString(R.string.prefs_appearance_entry_value_fullscreen) -> PlayerThemeEnum.FULLSCREEN
            context.getString(R.string.prefs_appearance_entry_value_big_image) -> PlayerThemeEnum.BIG_IMAGE
            context.getString(R.string.prefs_appearance_entry_value_clean) -> PlayerThemeEnum.CLEAN
            context.getString(R.string.prefs_appearance_entry_value_mini) -> PlayerThemeEnum.MINI
            else -> throw IllegalStateException("invalid theme=$theme")
        }
    }

    override fun isDefault(): Boolean {
        return THEME == PlayerThemeEnum.DEFAULT
    }

    override fun isFlat(): Boolean {
        return THEME == PlayerThemeEnum.FLAT
    }

    override fun isSpotify(): Boolean {
        return THEME == PlayerThemeEnum.SPOTIFY
    }

    override fun isFullscreen(): Boolean {
        return THEME == PlayerThemeEnum.FULLSCREEN
    }

    override fun isBigImage(): Boolean {
        return THEME == PlayerThemeEnum.BIG_IMAGE
    }

    override fun isClean(): Boolean {
        return THEME == PlayerThemeEnum.CLEAN
    }

    override fun isMini(): Boolean {
        return THEME == PlayerThemeEnum.MINI
    }
}