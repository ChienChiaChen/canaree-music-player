package dev.olog.msc.presentation.popup

import android.view.View
import android.widget.PopupMenu
import androidx.fragment.app.FragmentActivity
import dev.olog.msc.core.MediaId
import dev.olog.msc.core.MediaIdCategory
import dev.olog.msc.core.interactor.item.*
import dev.olog.msc.presentation.popup.album.AlbumPopup
import dev.olog.msc.presentation.popup.artist.ArtistPopup
import dev.olog.msc.presentation.popup.folder.FolderPopup
import dev.olog.msc.presentation.popup.genre.GenrePopup
import dev.olog.msc.presentation.popup.playlist.PlaylistPopup
import dev.olog.msc.presentation.popup.podcast.PodcastPopup
import dev.olog.msc.presentation.popup.podcastalbum.PodcastAlbumPopup
import dev.olog.msc.presentation.popup.podcastartist.PodcastArtistPopup
import dev.olog.msc.presentation.popup.podcastplaylist.PodcastPlaylistPopup
import dev.olog.msc.presentation.popup.song.SongPopup
import io.reactivex.Single
import io.reactivex.android.schedulers.AndroidSchedulers
import javax.inject.Inject

class PopupMenuFactory @Inject constructor(
        private val getFolderUseCase: GetFolderUseCase,
        private val getPlaylistUseCase: GetPlaylistUseCase,
        private val getSongUseCase: GetSongUseCase,
        private val getAlbumUseCase: GetAlbumUseCase,
        private val getArtistUseCase: GetArtistUseCase,
        private val getGenreUseCase: GetGenreUseCase,
        private val getPodcastUseCase: GetPodcastUseCase,
        private val getPodcastPlaylistUseCase: GetPodcastPlaylistUseCase,
        private val getPodcastAlbumUseCase: GetPodcastAlbumUseCase,
        private val getPodcastArtistUseCase: GetPodcastArtistUseCase,
        private val listenerFactory: MenuListenerFactory

) {

    fun create(view: View, mediaId: MediaId): Single<PopupMenu> {
        val category = mediaId.category
        return when (category) {
            MediaIdCategory.FOLDERS -> getFolderPopup(view, mediaId)
            MediaIdCategory.PLAYLISTS -> getPlaylistPopup(view, mediaId)
            MediaIdCategory.SONGS -> getSongPopup(view, mediaId)
            MediaIdCategory.ALBUMS -> getAlbumPopup(view, mediaId)
            MediaIdCategory.ARTISTS -> getArtistPopup(view, mediaId)
            MediaIdCategory.GENRES -> getGenrePopup(view, mediaId)
            MediaIdCategory.PODCASTS -> getPodcastPopup(view, mediaId)
            MediaIdCategory.PODCASTS_PLAYLIST -> getPodcastPlaylistPopup(view, mediaId)
            MediaIdCategory.PODCASTS_ALBUMS -> getPodcastAlbumPopup(view, mediaId)
            MediaIdCategory.PODCASTS_ARTISTS -> getPodcastArtistPopup(view, mediaId)
            else -> throw IllegalArgumentException("invalid category $category")
        }
    }

    private fun getFolderPopup(view: View, mediaId: MediaId): Single<PopupMenu> {
        val activity = view.context as FragmentActivity
        return getFolderUseCase.execute(mediaId).firstOrError()
                .flatMap { folder ->
                    if (mediaId.isLeaf) {
                        getSongUseCase.execute(mediaId).firstOrError()
                                .observeOn(AndroidSchedulers.mainThread())
                                .map { FolderPopup(view, folder, it, listenerFactory.folder(activity, folder, it)) }
                    } else {
                        Single.just(FolderPopup(view, folder, null, listenerFactory.folder(activity, folder, null)))
                                .subscribeOn(AndroidSchedulers.mainThread())
                    }
                }
    }

    private fun getPlaylistPopup(view: View, mediaId: MediaId): Single<PopupMenu> {
        val activity = view.context as FragmentActivity
        return getPlaylistUseCase.execute(mediaId).firstOrError()
                .flatMap { playlist ->
                    if (mediaId.isLeaf) {
                        getSongUseCase.execute(mediaId).firstOrError()
                                .observeOn(AndroidSchedulers.mainThread())
                                .map { PlaylistPopup(view, playlist, it, listenerFactory.playlist(activity, playlist, it)) }
                    } else {
                        Single.just(PlaylistPopup(view, playlist, null, listenerFactory.playlist(activity, playlist, null)))
                                .subscribeOn(AndroidSchedulers.mainThread())
                    }
                }
    }

    private fun getSongPopup(view: View, mediaId: MediaId): Single<PopupMenu> {
        val activity = view.context as FragmentActivity
        return getSongUseCase.execute(mediaId).firstOrError()
                .observeOn(AndroidSchedulers.mainThread())
                .map { SongPopup(view, it, listenerFactory.song(activity, it)) }

    }

    private fun getAlbumPopup(view: View, mediaId: MediaId): Single<PopupMenu> {
        val activity = view.context as FragmentActivity
        return getAlbumUseCase.execute(mediaId).firstOrError()
                .flatMap { album ->
                    if (mediaId.isLeaf) {
                        getSongUseCase.execute(mediaId).firstOrError()
                                .observeOn(AndroidSchedulers.mainThread())
                                .map { AlbumPopup(view, album, it, listenerFactory.album(activity, album, it)) }
                    } else {
                        Single.just(AlbumPopup(view, album, null, listenerFactory.album(activity, album, null)))
                                .subscribeOn(AndroidSchedulers.mainThread())
                    }
                }
    }

    private fun getArtistPopup(view: View, mediaId: MediaId): Single<PopupMenu> {
        val activity = view.context as FragmentActivity
        return getArtistUseCase.execute(mediaId).firstOrError()
                .flatMap { artist ->
                    if (mediaId.isLeaf) {
                        getSongUseCase.execute(mediaId).firstOrError()
                                .observeOn(AndroidSchedulers.mainThread())
                                .map { ArtistPopup(view, artist, it, listenerFactory.artist(activity, artist, it)) }
                    } else {
                        Single.just(ArtistPopup(view, artist, null, listenerFactory.artist(activity, artist, null)))
                                .subscribeOn(AndroidSchedulers.mainThread())
                    }
                }
    }

    private fun getGenrePopup(view: View, mediaId: MediaId): Single<PopupMenu> {
        val activity = view.context as FragmentActivity
        return getGenreUseCase.execute(mediaId).firstOrError()
                .flatMap { genre ->
                    if (mediaId.isLeaf) {
                        getSongUseCase.execute(mediaId).firstOrError()
                                .observeOn(AndroidSchedulers.mainThread())
                                .map { GenrePopup(view, genre, it, listenerFactory.genre(activity, genre, it)) }
                    } else {
                        Single.just(GenrePopup(view, genre, null, listenerFactory.genre(activity, genre, null)))
                                .subscribeOn(AndroidSchedulers.mainThread())
                    }
                }
    }

    private fun getPodcastPopup(view: View, mediaId: MediaId): Single<PopupMenu> {
        val activity = view.context as FragmentActivity
        return getPodcastUseCase.execute(mediaId)
                .firstOrError()
                .observeOn(AndroidSchedulers.mainThread())
                .map { PodcastPopup(view, it, listenerFactory.podcast(activity, it)) }
    }

    private fun getPodcastPlaylistPopup(view: View, mediaId: MediaId): Single<PopupMenu> {
        val activity = view.context as FragmentActivity
        return getPodcastPlaylistUseCase.execute(mediaId).firstOrError()
                .flatMap { playlist ->
                    if (mediaId.isLeaf) {
                        getPodcastUseCase.execute(mediaId).firstOrError()
                                .observeOn(AndroidSchedulers.mainThread())
                                .map { PodcastPlaylistPopup(view, playlist, it, listenerFactory.podcastPlaylist(activity, playlist, it)) }
                    } else {
                        Single.just(PodcastPlaylistPopup(view, playlist, null, listenerFactory.podcastPlaylist(activity, playlist, null)))
                                .subscribeOn(AndroidSchedulers.mainThread())
                    }
                }
    }

    private fun getPodcastAlbumPopup(view: View, mediaId: MediaId): Single<PopupMenu> {
        val activity = view.context as FragmentActivity
        return getPodcastAlbumUseCase.execute(mediaId).firstOrError()
                .flatMap { album ->
                    if (mediaId.isLeaf) {
                        getPodcastUseCase.execute(mediaId).firstOrError()
                                .observeOn(AndroidSchedulers.mainThread())
                                .map { PodcastAlbumPopup(view, album, it, listenerFactory.podcastAlbum(activity, album, it)) }
                    } else {
                        Single.just(PodcastAlbumPopup(view, album, null, listenerFactory.podcastAlbum(activity, album, null)))
                                .subscribeOn(AndroidSchedulers.mainThread())
                    }
                }
    }

    private fun getPodcastArtistPopup(view: View, mediaId: MediaId): Single<PopupMenu> {
        val activity = view.context as FragmentActivity
        return getPodcastArtistUseCase.execute(mediaId).firstOrError()
                .flatMap { artist ->
                    if (mediaId.isLeaf) {
                        getPodcastUseCase.execute(mediaId).firstOrError()
                                .observeOn(AndroidSchedulers.mainThread())
                                .map { PodcastArtistPopup(view, artist, it, listenerFactory.podcastArtist(activity, artist, it)) }
                    } else {
                        Single.just(PodcastArtistPopup(view, artist, null, listenerFactory.podcastArtist(activity,artist, null)))
                                .subscribeOn(AndroidSchedulers.mainThread())
                    }
                }
    }

}