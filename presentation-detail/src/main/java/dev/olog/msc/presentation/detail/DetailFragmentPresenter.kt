package dev.olog.msc.presentation.detail

import dev.olog.msc.core.MediaId
import dev.olog.msc.core.entity.PlaylistType
import dev.olog.msc.core.gateway.prefs.TutorialPreferenceGateway
import dev.olog.msc.core.gateway.track.PlaylistGateway
import dev.olog.msc.presentation.base.model.DisplayableItem
import dev.olog.msc.presentation.detail.domain.MoveItemInPlaylistUseCase
import dev.olog.msc.presentation.detail.domain.RemoveFromPlaylistUseCase
import io.reactivex.Completable
import javax.inject.Inject

class DetailFragmentPresenter @Inject constructor(
    private val mediaId: MediaId,
    private val removeFromPlaylistUseCase: RemoveFromPlaylistUseCase,
    private val moveItemInPlaylistUseCase: MoveItemInPlaylistUseCase,
    private val tutorialPreferenceUseCase: TutorialPreferenceGateway

) {

    fun removeFromPlaylist(item: DisplayableItem): Completable {
        mediaId.assertPlaylist()
        val playlistId = mediaId.resolveId
        val playlistType = if (item.mediaId.isPodcast) PlaylistType.PODCAST else PlaylistType.TRACK
        if (playlistId == PlaylistGateway.FAVORITE_LIST_ID){
            // favorites use songId instead of idInPlaylist
            return removeFromPlaylistUseCase.execute(
                RemoveFromPlaylistUseCase.Input(
                    playlistId, item.mediaId.leaf!!, playlistType
            ))
        }
        return removeFromPlaylistUseCase.execute(
            RemoveFromPlaylistUseCase.Input(
                playlistId, item.trackNumber.toLong(), playlistType
        ))
    }

    fun moveInPlaylist(from: Int, to: Int){
        mediaId.assertPlaylist()
        val playlistId = mediaId.resolveId
        moveItemInPlaylistUseCase.execute(
            MoveItemInPlaylistUseCase.Input(playlistId, from, to,
                if (mediaId.isPodcastPlaylist) PlaylistType.PODCAST else PlaylistType.TRACK
        ))
    }

    fun showSortByTutorialIfNeverShown(): Completable {
        return tutorialPreferenceUseCase.sortByTutorial()
    }

}