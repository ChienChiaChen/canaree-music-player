package dev.olog.msc.presentation.edit

import dev.olog.msc.core.MediaId
import dev.olog.msc.core.gateway.LastFmGateway
import dev.olog.msc.domain.interactor.update.UpdateMultipleTracksUseCase
import io.reactivex.Completable
import org.jaudiotagger.tag.FieldKey
import javax.inject.Inject

class EditItemPresenter @Inject constructor(
        private val deleteTrackUseCase: LastFmGateway,
        private val updateMultipleTracksUseCase: UpdateMultipleTracksUseCase

){

    fun deleteTrack(id: Long): Completable {
        TODO()
//        return deleteTrackUseCase.deleteTrack(id)
    }

    fun deleteAlbum(mediaId: MediaId): Completable {
        TODO()
//        return deleteAlbumUseCase.execute(mediaId)
    }

    fun deleteArtist(mediaId: MediaId): Completable {
        TODO()
//        return deleteArtistUseCase.execute(mediaId)
    }

    fun updateSingle(info: UpdateSongInfo): Completable {
        TODO()
//        val albumArtist = if (info.albumArtist.isBlank()) info.artist else info.albumArtist
//
//        return updateTrackUseCase.execute(UpdateTrackUseCase.Data(
//                info.originalSong.id,
//                info.originalSong.path,
//                info.image,
//                mapOf(
//                        FieldKey.TITLE to info.title,
//                        FieldKey.ARTIST to info.artist,
//                        FieldKey.ALBUM_ARTIST to albumArtist,
//                        FieldKey.ALBUM to info.album,
//                        FieldKey.GENRE to info.genre,
//                        FieldKey.YEAR to info.year,
//                        FieldKey.DISC_NO to info.disc,
//                        FieldKey.TRACK to info.track
//                )
//        ))
    }

    fun updateAlbum(info: UpdateAlbumInfo): Completable {
        val albumArtist = if (info.albumArtist.isBlank()) info.artist else info.albumArtist
        return updateMultipleTracksUseCase.execute(UpdateMultipleTracksUseCase.Data(
                info.mediaId,
                info.image,
                mapOf(
                        FieldKey.ALBUM to info.title,
                        FieldKey.ARTIST to info.artist,
                        FieldKey.ALBUM_ARTIST to albumArtist,
                        FieldKey.GENRE to info.genre,
                        FieldKey.YEAR to info.year
                )
        ))
    }


    fun updateArtist(info: UpdateArtistInfo): Completable {
        val albumArtist = if (info.albumArtist.isBlank()) info.name else info.albumArtist
        return updateMultipleTracksUseCase.execute(UpdateMultipleTracksUseCase.Data(
                info.mediaId,
                info.image,
                mapOf(
                        FieldKey.ARTIST to info.name,
                        FieldKey.ALBUM_ARTIST to albumArtist
                )
        ))
    }

}