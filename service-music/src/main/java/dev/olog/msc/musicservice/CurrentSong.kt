package dev.olog.msc.musicservice

import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleOwner
import dev.olog.msc.core.MediaId
import dev.olog.msc.core.dagger.qualifier.ServiceLifecycle
import dev.olog.msc.core.entity.LastMetadata
import dev.olog.msc.core.entity.favorite.FavoriteEnum
import dev.olog.msc.core.entity.favorite.FavoriteStateEntity
import dev.olog.msc.core.entity.favorite.FavoriteType
import dev.olog.msc.core.gateway.prefs.MusicPreferencesGateway
import dev.olog.msc.core.interactor.InsertHistorySongUseCase
import dev.olog.msc.core.interactor.favorite.IsFavoriteSongUseCase
import dev.olog.msc.core.interactor.favorite.UpdateFavoriteStateUseCase
import dev.olog.msc.core.interactor.played.InsertLastPlayedAlbumUseCase
import dev.olog.msc.core.interactor.played.InsertLastPlayedArtistUseCase
import dev.olog.msc.core.interactor.played.InsertMostPlayedUseCase
import dev.olog.msc.musicservice.interfaces.PlayerLifecycle
import dev.olog.msc.musicservice.model.MediaEntity
import dev.olog.msc.shared.core.coroutines.CustomScope
import dev.olog.msc.shared.utils.assertBackgroundThread
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.cancel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch
import javax.inject.Inject

internal class CurrentSong @Inject constructor(
    @ServiceLifecycle lifecycle: Lifecycle,
    private val insertMostPlayedUseCase: InsertMostPlayedUseCase,
    private val insertHistorySongUseCase: InsertHistorySongUseCase,
    private val musicPreferencesUseCase: MusicPreferencesGateway,
    private val isFavoriteSongUseCase: IsFavoriteSongUseCase,
    private val updateFavoriteStateUseCase: UpdateFavoriteStateUseCase,
    private val insertLastPlayedAlbumUseCase: InsertLastPlayedAlbumUseCase,
    private val insertLastPlayedArtistUseCase: InsertLastPlayedArtistUseCase,
    playerLifecycle: PlayerLifecycle

) : DefaultLifecycleObserver, CoroutineScope by CustomScope() {

    private val mediaEntityChannel = Channel<MediaEntity>()
    private val favoriteChannel = Channel<MediaEntity>()

    private val playerListener = object : PlayerLifecycle.Listener {
        override fun onPrepare(entity: MediaEntity) {
            launch {
                updateFavorite(entity)
                saveLastMetadata(entity)
            }
        }

        override fun onMetadataChanged(entity: MediaEntity) {
            launch {
                mediaEntityChannel.send(entity)
                favoriteChannel.send(entity)
                updateFavorite(entity)
                saveLastMetadata(entity)
            }
        }
    }

    init {
        lifecycle.addObserver(this)

        playerLifecycle.addListener(playerListener)

        launch {
            for (mediaEntity in mediaEntityChannel) {
                tryAddToMostPlayed(mediaEntity)
                tryAddToHistory(mediaEntity)
                tryAddLastPlayedArtist(mediaEntity)
                tryAddLastPlayedAlbum(mediaEntity)
            }
        }
        launch {
            for (mediaEntity in favoriteChannel) {
                updateFavorite(mediaEntity)
            }
        }
    }

    override fun onDestroy(owner: LifecycleOwner) {
        cancel()
    }

    private suspend fun tryAddToMostPlayed(entity: MediaEntity) = coroutineScope {
        assertBackgroundThread()
        val mediaId = MediaId.playableItem(entity.mediaId, entity.id)
        insertMostPlayedUseCase.execute(mediaId)
    }

    private suspend fun tryAddToHistory(entity: MediaEntity) = coroutineScope {
        assertBackgroundThread()
        insertHistorySongUseCase.execute(InsertHistorySongUseCase.Input(entity.id, entity.isPodcast))
    }

    private suspend fun tryAddLastPlayedAlbum(entity: MediaEntity) = coroutineScope {
        assertBackgroundThread()
        if (entity.mediaId.isAlbum || entity.mediaId.isPodcastAlbum) {
            insertLastPlayedAlbumUseCase.execute(entity.mediaId)
        }
    }

    private suspend fun tryAddLastPlayedArtist(entity: MediaEntity) = coroutineScope {
        assertBackgroundThread()
        if (entity.mediaId.isArtist || entity.mediaId.isPodcastArtist) {
            insertLastPlayedArtistUseCase.execute(entity.mediaId)
        }
    }

    private suspend fun updateFavorite(mediaEntity: MediaEntity) {
        assertBackgroundThread()
        val type = if (mediaEntity.isPodcast) FavoriteType.PODCAST else FavoriteType.TRACK
        val isFavorite = isFavoriteSongUseCase.execute(IsFavoriteSongUseCase.Input(mediaEntity.id, type))
        val newFavoriteState = if (isFavorite) FavoriteEnum.FAVORITE else FavoriteEnum.NOT_FAVORITE
        updateFavoriteStateUseCase.execute(FavoriteStateEntity(mediaEntity.id, newFavoriteState, type))
    }

    private fun saveLastMetadata(entity: MediaEntity) {
        assertBackgroundThread()
        musicPreferencesUseCase.setLastMetadata(LastMetadata(entity.title, entity.artist, entity.id))
    }

}