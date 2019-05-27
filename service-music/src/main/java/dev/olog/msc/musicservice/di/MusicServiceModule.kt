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
import dagger.Binds
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

@Module
abstract class MusicServiceModule{

    @Binds
    @ServiceContext
    internal abstract fun provideContext(service: MusicService): Context

    @Binds
    internal abstract fun provideService(service: MusicService): Service

    @Binds
    @PerService
    internal abstract fun provideServiceLifecycle(service: MusicService): ServiceLifecycleController


    @Binds
    @PerService
    internal abstract fun providePlayerImpl(crossfadePlayer: CrossFadePlayer): CustomExoPlayer<PlayerMediaEntity>

    @Binds
    @PerService
    internal abstract fun provideQueue(queue: QueueManager): Queue

    @Binds
    @PerService
    internal abstract fun providePlayer(player: PlayerImpl): Player

    @Binds
    @PerService
    internal abstract fun providePlayerLifecycle(player: Player): PlayerLifecycle

    @Binds
    @PerService
    internal abstract fun providePlayerVolume(volume: PlayerVolume): IPlayerVolume

    @Module
    companion object {
        @Provides
        @ServiceLifecycle
        @JvmStatic
        internal fun provideLifecycle(service: MusicService): Lifecycle = service.lifecycle

        @Provides
        @PerService
        @JvmStatic
        internal fun provideMediaSession(service: MusicService): MediaSessionCompat {
            return MediaSessionCompat(
                service, MusicService.TAG,
                ComponentName(service, MediaButtonReceiver::class.java),
                null
            )
        }

        @Provides
        @JvmStatic
        internal fun provideAudioManager(service: MusicService): AudioManager {
            return service.getSystemService(Context.AUDIO_SERVICE) as AudioManager
        }

        @Provides
        @JvmStatic
        internal fun provideNotificationManager(service: MusicService): NotificationManager {
            return service.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        }

        @Provides
        @JvmStatic
        internal fun provideToken(mediaSession: MediaSessionCompat): MediaSessionCompat.Token {
            return mediaSession.sessionToken
        }

        @Provides
        @JvmStatic
        internal fun provideMediaController(mediaSession: MediaSessionCompat): MediaControllerCompat {
            return mediaSession.controller
        }
    }

}