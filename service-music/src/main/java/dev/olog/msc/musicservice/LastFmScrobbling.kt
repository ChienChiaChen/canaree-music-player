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
import dev.olog.msc.core.gateway.LastFmGateway.Companion.LAST_FM_API_KEY
import dev.olog.msc.core.gateway.LastFmGateway.Companion.LAST_FM_API_SECRET
import dev.olog.msc.core.interactor.scrobble.ObserveLastFmUserCredentials
import dev.olog.msc.musicservice.interfaces.PlayerLifecycle
import dev.olog.msc.musicservice.model.MediaEntity
import dev.olog.msc.shared.extensions.unsubscribe
import io.reactivex.Maybe
import io.reactivex.Single
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.rxkotlin.addTo
import io.reactivex.schedulers.Schedulers
import java.util.logging.Level
import javax.inject.Inject

class LastFmScrobbling @Inject constructor(
        @ServiceLifecycle lifecycle: Lifecycle,
        observeLastFmUserCredentials: ObserveLastFmUserCredentials,
        playerLifecycle: PlayerLifecycle

) : DefaultLifecycleObserver, PlayerLifecycle.Listener {

    private var session: Session? = null
    private var userCredentials : UserCredentials? = null

    private val credendialsDisposable = observeLastFmUserCredentials.execute()
            .observeOn(Schedulers.io())
            .filter { it.username.isNotBlank() }
            .subscribe(this::tryAutenticate, Throwable::printStackTrace)

    private fun tryAutenticate(credentials: UserCredentials){
        try {
            session = Authenticator.getMobileSession(credentials.username, credentials.password, LAST_FM_API_KEY, LAST_FM_API_SECRET)
            userCredentials = credentials
        } catch (ex: Exception){
            ex.printStackTrace()
        }
    }

    private var scrobbleSubscriptions = CompositeDisposable()

    init {
        lifecycle.addObserver(this)
        playerLifecycle.addListener(this)

        Caller.getInstance().userAgent = "dev.olog.msc"
        Caller.getInstance().logger.level = Level.OFF
    }

    override fun onDestroy(owner: LifecycleOwner) {
        credendialsDisposable.unsubscribe()
    }

    override fun onMetadataChanged(entity: MediaEntity) {
        scrobble(entity)
    }

    private fun scrobble(entity: MediaEntity){
        Single.just(true)
                .observeOn(Schedulers.io())
                .filter { session != null && userCredentials != null }
                .flatMap { Maybe.fromCallable {
                    val scrobbleData = entity.toScrollData()
                    Track.scrobble(scrobbleData, session)
                    Track.updateNowPlaying(scrobbleData, session)
                } }
                .subscribe({ }, Throwable::printStackTrace)
                .addTo(scrobbleSubscriptions)
    }

    private fun MediaEntity.toScrollData(): ScrobbleData {
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