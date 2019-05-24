package dev.olog.msc.musicservice

import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleOwner
import de.umass.lastfm.Authenticator
import de.umass.lastfm.Caller
import de.umass.lastfm.Session
import de.umass.lastfm.Track
import de.umass.lastfm.scrobble.ScrobbleData
import dev.olog.msc.core.dagger.qualifier.ServiceLifecycle
import dev.olog.msc.core.entity.UserCredentials
import dev.olog.msc.core.interactor.scrobble.ObserveLastFmUserCredentials
import dev.olog.msc.musicservice.interfaces.PlayerLifecycle
import dev.olog.msc.musicservice.model.MediaEntity
import dev.olog.msc.shared.core.coroutines.DefaultScope
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.filter
import java.util.logging.Level
import javax.inject.Inject

internal class LastFmScrobbling @Inject constructor(
    @ServiceLifecycle lifecycle: Lifecycle,
    observeLastFmUserCredentials: ObserveLastFmUserCredentials,
    playerLifecycle: PlayerLifecycle

) : DefaultLifecycleObserver, PlayerLifecycle.Listener, CoroutineScope by DefaultScope() {

    private var session: Session? = null
    private var userCredentials: UserCredentials? = null

    private var scrollbeJob: Job? = null

    init {
        launch {
            observeLastFmUserCredentials.execute()
                .filter { it.username.isNotBlank() }
                .collect { tryAutenticate(it) }
        }
    }

    private fun tryAutenticate(credentials: UserCredentials) {
        try {
            session = Authenticator.getMobileSession(
                credentials.username,
                credentials.password,
                BuildConfig.LAST_FM_KEY,
                BuildConfig.LAST_FM_SECRET
            )
            userCredentials = credentials
        } catch (ex: Exception) {
            ex.printStackTrace()
        }
    }

    init {
        lifecycle.addObserver(this)
        playerLifecycle.addListener(this)

        Caller.getInstance().userAgent = "dev.olog.msc"
        Caller.getInstance().logger.level = Level.OFF
    }

    override fun onDestroy(owner: LifecycleOwner) {
        cancel()
        scrollbeJob?.cancel()
    }

    override fun onMetadataChanged(entity: MediaEntity) {
        scrollbeJob?.cancel()
        scrollbeJob = launch {
            delay(10 * 1000)
            scrobble(entity)
        }
    }

    private suspend fun scrobble(entity: MediaEntity) = coroutineScope {
        if (session == null || userCredentials == null) {
            return@coroutineScope
        }
        val scrobbleData = entity.toScroblleData()
        Track.scrobble(scrobbleData, session)
        Track.updateNowPlaying(scrobbleData, session)
    }

    private fun MediaEntity.toScroblleData(): ScrobbleData {
        return ScrobbleData(
            this.artist,
            this.title,
            (System.currentTimeMillis() / 1000).toInt(),
            this.duration.toInt(),
            this.album,
            null,
            null,
            this.trackNumber,
            null,
            true
        )
    }

}