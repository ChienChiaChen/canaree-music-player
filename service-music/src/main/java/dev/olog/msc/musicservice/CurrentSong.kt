package dev.olog.msc.musicservice

import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleOwner
import dev.olog.msc.core.MediaId
import dev.olog.msc.core.dagger.qualifier.ServiceLifecycle
import dev.olog.msc.core.dagger.scope.PerService
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
import dev.olog.msc.shared.extensions.unsubscribe
import io.reactivex.Maybe
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.disposables.Disposable
import io.reactivex.processors.BehaviorProcessor
import io.reactivex.rxkotlin.addTo
import io.reactivex.schedulers.Schedulers
import javax.inject.Inject

@PerService
internal class CurrentSong @Inject constructor(
        @ServiceLifecycle lifecycle: Lifecycle,
        insertMostPlayedUseCase: InsertMostPlayedUseCase,
        insertHistorySongUseCase: InsertHistorySongUseCase,
        private val musicPreferencesUseCase: MusicPreferencesGateway,
        private val isFavoriteSongUseCase: IsFavoriteSongUseCase,
        private val updateFavoriteStateUseCase: UpdateFavoriteStateUseCase,
        private val insertLastPlayedAlbumUseCase: InsertLastPlayedAlbumUseCase,
        private val insertLastPlayedArtistUseCase: InsertLastPlayedArtistUseCase,
        playerLifecycle: PlayerLifecycle

) : DefaultLifecycleObserver {

    private val publisher = BehaviorProcessor.create<MediaEntity>()

    private val subscriptions = CompositeDisposable()
    private var isFavoriteDisposable : Disposable? = null

    private val insertToMostPlayedFlowable = publisher
            .observeOn(Schedulers.io())
            .flatMapMaybe { createMostPlayedId(it) }
            .flatMapCompletable { insertMostPlayedUseCase.execute(it).onErrorComplete() }

    private val insertHistorySongFlowable = publisher
            .observeOn(Schedulers.io())
            .flatMapCompletable { insertHistorySongUseCase.execute(
                    InsertHistorySongUseCase.Input(it.id, it.isPodcast)
            ).onErrorComplete() }

    private val insertLastPlayedAlbumFlowable = publisher
            .observeOn(Schedulers.io())
            .filter { it.mediaId.isAlbum || it.mediaId.isPodcastAlbum }
            .flatMapCompletable { insertLastPlayedAlbumUseCase.execute(it.mediaId).onErrorComplete() }

    private val insertLastPlayedArtistFlowable = publisher
            .observeOn(Schedulers.io())
            .filter { it.mediaId.isArtist || it.mediaId.isPodcastArtist}
            .flatMapCompletable { insertLastPlayedArtistUseCase.execute(it.mediaId) }

    private val playerListener = object : PlayerLifecycle.Listener {
        override fun onPrepare(entity: MediaEntity) {
            updateFavorite(entity)
            saveLastMetadata(entity)
        }

        override fun onMetadataChanged(entity: MediaEntity) {
            publisher.onNext(entity)
            updateFavorite(entity)
            saveLastMetadata(entity)
        }
    }

    init {
        lifecycle.addObserver(this)

        playerLifecycle.addListener(playerListener)

        insertToMostPlayedFlowable.subscribe({}, Throwable::printStackTrace)
                .addTo(subscriptions)
        insertHistorySongFlowable.subscribe({}, Throwable::printStackTrace)
                .addTo(subscriptions)
        insertLastPlayedAlbumFlowable.subscribe({}, Throwable::printStackTrace)
                .addTo(subscriptions)
        insertLastPlayedArtistFlowable.subscribe({}, Throwable::printStackTrace)
                .addTo(subscriptions)

    }

    override fun onDestroy(owner: LifecycleOwner) {
        subscriptions.clear()
        isFavoriteDisposable.unsubscribe()
    }

    private fun updateFavorite(mediaEntity: MediaEntity){
        isFavoriteDisposable.unsubscribe()
        val type = if (mediaEntity.isPodcast) FavoriteType.PODCAST else FavoriteType.TRACK
        isFavoriteDisposable = isFavoriteSongUseCase
                .execute(IsFavoriteSongUseCase.Input(mediaEntity.id, type))
                .map { if (it) FavoriteEnum.FAVORITE else FavoriteEnum.NOT_FAVORITE }
                .flatMapCompletable { updateFavoriteStateUseCase.execute(FavoriteStateEntity(mediaEntity.id, it, type)) }
                .subscribe({}, Throwable::printStackTrace)
    }

    private fun saveLastMetadata(entity: MediaEntity){
        musicPreferencesUseCase.setLastMetadata(LastMetadata(
                entity.title, entity.artist, entity.image, entity.id
        ))
    }

    private fun createMostPlayedId(entity: MediaEntity): Maybe<MediaId> {
        try {
            return Maybe.just(MediaId.playableItem(entity.mediaId, entity.id))
        } catch (ex: Exception){
            return Maybe.empty()
        }
    }

}