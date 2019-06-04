package dev.olog.msc.musicservice.player

import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleOwner
import dev.olog.msc.core.dagger.qualifier.ServiceLifecycle
import dev.olog.msc.core.dagger.scope.PerService
import dev.olog.msc.core.gateway.prefs.MusicPreferencesGateway
import dev.olog.msc.musicservice.volume.IPlayerVolume
import dev.olog.msc.musicservice.volume.IVolume
import dev.olog.msc.shared.core.coroutines.CustomScope
import dev.olog.msc.shared.core.flow.flowInterval
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.flatMapConcat
import kotlinx.coroutines.flow.map
import java.util.*
import java.util.concurrent.TimeUnit
import javax.inject.Inject

private const val VOLUME_DUCK = .2f
private const val VOLUME_LOWERED_DUCK = 0.1f

private const val VOLUME_NORMAL = 1f
private const val VOLUME_LOWERED_NORMAL = 0.4f

@PerService
internal class PlayerVolume @Inject constructor(
        @ServiceLifecycle lifecycle: Lifecycle,
        musicPreferencesUseCase: MusicPreferencesGateway

) : IPlayerVolume, DefaultLifecycleObserver, CoroutineScope by CustomScope(
    Dispatchers.Main
) {

    override var listener: IPlayerVolume.Listener? = null

    private var volume: IVolume = Volume()
    private var isDucking = false

    init {
        lifecycle.addObserver(this)

        launch {
            // observe to preferences
            musicPreferencesUseCase.isMidnightMode()
                .collect { lowerAtNight ->
                    if (!lowerAtNight) {
                        volume = provideVolumeManager(false)
                    } else {
                        volume = provideVolumeManager(isNight())
                    }

                    listener?.onVolumeChanged(getVolume())
                }
        }
        launch(Dispatchers.Default) {
            // observe at interval of 15 mins to detect if is day or night when
            // settigs is on
            musicPreferencesUseCase.isMidnightMode()
                .filter { it }
                .flatMapConcat { flowInterval(15, TimeUnit.MINUTES) }
                .map { isNight() }
                .collect { isNight ->
                    withContext(Dispatchers.Main) {
                        volume = provideVolumeManager(isNight)
                        listener?.onVolumeChanged(getVolume())
                    }
                }
        }
    }

    private fun isNight(): Boolean {
        val hour = Calendar.getInstance().get(Calendar.HOUR_OF_DAY)
        return hour <= 6 || hour >= 21
    }

    override fun getVolume(): Float {
        return if (isDucking) volume.duck else volume.normal
    }

    private fun provideVolumeManager(isNight: Boolean): IVolume {
        return if (isNight) {
            NightVolume()
        } else {
            Volume()
        }
    }

    override fun onDestroy(owner: LifecycleOwner) {
        cancel()
        listener = null
    }

    override fun normal(): Float {
        isDucking = false
        return volume.normal
    }

    override fun ducked(): Float {
        isDucking = true
        return volume.duck
    }
}

private class Volume : IVolume {
    override val normal: Float = VOLUME_NORMAL
    override val duck: Float = VOLUME_DUCK
}

private class NightVolume : IVolume {
    override val normal: Float = VOLUME_LOWERED_NORMAL
    override val duck: Float = VOLUME_LOWERED_DUCK
}
