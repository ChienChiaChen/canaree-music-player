package dev.olog.msc.presentation.edititem.album

import dev.olog.msc.core.entity.podcast.PodcastAlbum
import dev.olog.msc.core.entity.track.Album
import dev.olog.msc.core.entity.track.Song
import dev.olog.msc.core.interactor.GetSongListChunkByParamUseCase
import dev.olog.msc.core.interactor.item.GetAlbumUseCase
import dev.olog.msc.core.interactor.item.GetPodcastAlbumUseCase
import io.reactivex.Single
import kotlinx.coroutines.runBlocking
import java.io.File
import javax.inject.Inject

class EditAlbumFragmentPresenter @Inject constructor(
    private val getAlbumUseCase: GetAlbumUseCase,
    private val getPodcastAlbumUseCase: GetPodcastAlbumUseCase,
    private val getSongListByParamUseCase: GetSongListChunkByParamUseCase

) {

    lateinit var songList: List<Song>
    private lateinit var originalAlbum: DisplayableAlbum

    fun observeAlbum(): Single<DisplayableAlbum> {
//        if (mediaId.isPodcastAlbum){
//            return observePodcastAlbumInternal()
//        }
        return observeAlbumInternal()
    }

    private fun observeAlbumInternal(): Single<DisplayableAlbum> = runBlocking {
        TODO()
//        getAlbumUseCase.execute(mediaId).asObservable()
//                .flatMap { original ->
//                    getSongListByParamUseCase.execute(mediaId)
//                            .map { original.toDisplayableAlbum(it[0].path)  }
//                }
//                .firstOrError()
//                .doOnSuccess { originalAlbum = it }
    }

    private fun observePodcastAlbumInternal(): Single<DisplayableAlbum> = runBlocking {
        TODO()
//        getPodcastAlbumUseCase.execute(mediaId).asObservable()
//                .flatMap { original ->
//                    getSongListByParamUseCase.execute(mediaId)
//                            .map { original.toDisplayableAlbum(it[0].path)}
//                }
//                .firstOrError()
//                .doOnSuccess { originalAlbum = it }
    }

    fun getSongList(): Single<List<Song>> {
        TODO()
//        return getSongListByParamUseCase.execute(mediaId)
//                .firstOrError()
//                .doOnSuccess { songList = it }
    }

    fun getAlbum(): DisplayableAlbum = originalAlbum

    private fun Album.toDisplayableAlbum(path: String): DisplayableAlbum {
        val file = File(path)
        val audioFile = AudioFileIO.read(file)
        val tag = audioFile.tagOrCreateAndSetDefault

        return DisplayableAlbum(
            this.id,
            this.title,
            tag.get(FieldKey.ARTIST),
            tag.get(FieldKey.ALBUM_ARTIST),
            tag.get(FieldKey.GENRE),
            tag.get(FieldKey.YEAR)
        )
    }

    private fun PodcastAlbum.toDisplayableAlbum(path: String): DisplayableAlbum {
        val file = File(path)
        val audioFile = AudioFileIO.read(file)
        val tag = audioFile.tagOrCreateAndSetDefault

        return DisplayableAlbum(
            this.id,
            this.title,
            tag.get(FieldKey.ARTIST),
            tag.get(FieldKey.ALBUM_ARTIST),
            tag.get(FieldKey.GENRE),
            tag.get(FieldKey.YEAR)
        )
    }

}