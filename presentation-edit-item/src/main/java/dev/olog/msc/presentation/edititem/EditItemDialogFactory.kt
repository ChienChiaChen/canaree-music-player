package dev.olog.msc.presentation.edititem

import android.content.Context
import dev.olog.msc.core.MediaId
import dev.olog.msc.core.dagger.qualifier.ApplicationContext
import dev.olog.msc.core.entity.podcast.Podcast
import dev.olog.msc.core.entity.track.Song
import dev.olog.msc.core.interactor.GetSongListChunkByParamUseCase
import dev.olog.msc.core.interactor.item.GetPodcastUseCase
import dev.olog.msc.core.interactor.item.GetSongUseCase
import dev.olog.msc.shared.extensions.toast
import io.reactivex.disposables.Disposable
import kotlinx.coroutines.runBlocking
import org.jaudiotagger.audio.AudioFileIO
import org.jaudiotagger.audio.exceptions.CannotReadException
import org.jaudiotagger.audio.exceptions.ReadOnlyFileException
import java.io.File
import java.io.IOException
import javax.inject.Inject

class EditItemDialogFactory @Inject constructor(
        @ApplicationContext private val context: Context,
        private val getSongUseCase: GetSongUseCase,
        private val getPodcastUseCase: GetPodcastUseCase,
        private val getSongListByParamUseCase: GetSongListChunkByParamUseCase

) {

    private var toDialogDisposable : Disposable? = null

    fun dispose(){
//        toDialogDisposable.unsubscribe()
    }

    fun toEditTrack(mediaId: MediaId, action: () -> Unit) = runBlocking{
//        toDialogDisposable.unsubscribe() TODO
//        toDialogDisposable = if (mediaId.isAnyPodcast){
//            getPodcastUseCase.execute(mediaId).asObservable()
//                    .observeOn(Schedulers.computation())
//                    .firstOrError()
//                    .map { checkPodcast(it) }
//        } else {
//            getSongUseCase.execute(mediaId).asObservable()
//                    .observeOn(Schedulers.computation())
//                    .firstOrError()
//                    .map { checkSong(it) }
//        }.observeOn(AndroidSchedulers.mainThread())
//                .subscribe({ action() }, { showError(it) })
    }

    fun toEditAlbum(mediaId: MediaId, action: () -> Unit){
//        toDialogDisposable.unsubscribe()
//        toDialogDisposable = getSongListByParamUseCase.execute(mediaId)
//                .observeOn(Schedulers.computation())
//                .firstOrError()
//                .flattenAsObservable { it }
//                .map { checkSong(it) }
//                .toList()
//                .observeOn(AndroidSchedulers.mainThread())
//                .subscribe({ action() }, { showError(it) })
    }

    fun toEditArtist(mediaId: MediaId, action: () -> Unit){
//        toDialogDisposable.unsubscribe()
//        toDialogDisposable = getSongListByParamUseCase.execute(mediaId)
//                .observeOn(Schedulers.computation())
//                .firstOrError()
//                .flattenAsObservable { it }
//                .map { checkSong(it) }
//                .toList()
//                .observeOn(AndroidSchedulers.mainThread())
//                .subscribe({ action() }, { showError(it) })
    }

    private fun checkSong(song: Song){
        val file = File(song.path)
        val audioFile = AudioFileIO.read(file)
        audioFile.tagOrCreateAndSetDefault
    }

    private fun checkPodcast(podcast: Podcast){
        val file = File(podcast.path)
        val audioFile = AudioFileIO.read(file)
        audioFile.tagOrCreateAndSetDefault
    }

    private fun showError(error: Throwable){
        when (error) {
            is CannotReadException -> context.toast(R.string.edit_song_error_can_not_read)
            is IOException -> context.toast(R.string.edit_song_error_io)
            is ReadOnlyFileException -> context.toast(R.string.edit_song_error_read_only)
            else -> {
                error.printStackTrace()
                context.toast(R.string.edit_song_error)
            }
        }
    }

}