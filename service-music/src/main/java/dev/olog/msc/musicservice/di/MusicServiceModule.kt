package dev.olog.msc.musicservice.di

import android.app.NotificationManager
import android.app.Service
import android.content.ComponentName
import android.content.Context
import android.media.AudioManager
import android.support.v4.media.session.MediaControllerCompat
import android.support.v4.media.session.MediaSessionCompat
import androidx.lifecycle.Lifecycle
import androidx.media.session.MediaButtonReceiver
import dagger.Module
import dagger.Provides
import dev.olog.msc.core.dagger.qualifier.ServiceContext
import dev.olog.msc.core.dagger.qualifier.ServiceLifecycle
import dev.olog.msc.core.dagger.scope.PerService
import dev.olog.msc.musicservice.MusicService
import dev.olog.msc.musicservice.QueueManager
import dev.olog.msc.musicservice.interfaces.Player
import dev.olog.msc.musicservice.interfaces.PlayerLifecycle
import dev.olog.msc.musicservice.interfaces.Queue
import dev.olog.msc.musicservice.interfaces.ServiceLifecycleController
import dev.olog.msc.musicservice.model.PlayerMediaEntity
import dev.olog.msc.musicservice.player.CustomExoPlayer
import dev.olog.msc.musicservice.player.PlayerImpl
import dev.olog.msc.musicservice.player.PlayerVolume
import dev.olog.msc.musicservice.player.crossfade.CrossFadePlayer
import dev.olog.msc.musicservice.volume.IPlayerVolume

@Module(includes = arrayOf(MusicServiceModule.Binds::class))
class MusicServiceModule(
        private val service: MusicService
) {

    @Provides
    @ServiceContext
    internal fun provideContext(): Context = service

    @Provides
    internal fun provideService(): Service = service

    @Provides
    @PerService
    internal fun provideServiceLifecycle(): ServiceLifecycleController = service


    @Provides
    @ServiceLifecycle
    internal fun provideLifecycle(): Lifecycle = service.lifecycle

    @Provides
    @PerService
    internal fun provideMediaSession(): MediaSessionCompat {
        return MediaSessionCompat(service, MusicService.TAG,
                ComponentName(service, MediaButtonReceiver::class.java),
                null)
    }

    @Provides
    @PerService
    internal fun provideAudioManager(): AudioManager {
        return service.getSystemService(Context.AUDIO_SERVICE) as AudioManager
    }

    @Provides
    @PerService
    internal fun provideNotificationManager(): NotificationManager {
        return service.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
    }

    @Provides
    internal fun provideToken(mediaSession: MediaSessionCompat): MediaSessionCompat.Token {
        return mediaSession.sessionToken
    }

    @Provides
    internal fun provideMediaController(mediaSession: MediaSessionCompat): MediaControllerCompat {
        return mediaSession.controller
    }

    @Provides
    @PerService
    internal fun providePlayer(
//            simplePlayer: Lazy<SimplePlayer>,
            crossfadePlayer: CrossFadePlayer): CustomExoPlayer<PlayerMediaEntity> {
        return crossfadePlayer
    }

    @Module
    abstract class Binds {

        @dagger.Binds
        @PerService
        abstract fun provideQueue(queue: QueueManager): Queue

        @dagger.Binds
        @PerService
        abstract fun providePlayer(player: PlayerImpl): Player

        @dagger.Binds
        @PerService
        abstract fun providePlayerLifecycle(player: Player): PlayerLifecycle

        @dagger.Binds
        @PerService
        abstract fun providePlayerVolume(volume: PlayerVolume): IPlayerVolume

    }

}