package dev.olog.msc.presentation.popup

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.widget.PopupMenu
import androidx.fragment.app.FragmentActivity
import dev.olog.msc.core.MediaId
import dev.olog.msc.core.entity.PlaylistType
import dev.olog.msc.core.entity.track.Song
import dev.olog.msc.presentation.navigator.Navigator
import dev.olog.msc.presentation.popup.domain.AddToPlaylistUseCase
import dev.olog.msc.presentation.popup.domain.GetPlaylistsBlockingUseCase
import dev.olog.msc.shared.FileProvider
import dev.olog.msc.shared.extensions.lazyFast
import dev.olog.msc.shared.extensions.toast
import dev.olog.presentation.base.extensions.asHtml
import io.reactivex.android.schedulers.AndroidSchedulers

abstract class AbsPopupListener(
        getPlaylistBlockingUseCase: GetPlaylistsBlockingUseCase,
        private val addToPlaylistUseCase: AddToPlaylistUseCase,
        private val podcastPlaylist: Boolean

) : PopupMenu.OnMenuItemClickListener {

    protected lateinit var activity: FragmentActivity

    fun setActivity(activity: FragmentActivity): AbsPopupListener {
        this.activity = activity
        return this
    }

    val playlists by lazyFast {
        getPlaylistBlockingUseCase.execute(if (podcastPlaylist) PlaylistType.PODCAST
        else PlaylistType.TRACK)
    }

    @SuppressLint("RxLeakedSubscription")
    protected fun onPlaylistSubItemClick(context: Context, itemId: Int, mediaId: MediaId, listSize: Int, title: String){
        playlists.firstOrNull { it.id == itemId.toLong() }?.run {
            addToPlaylistUseCase.execute(this to mediaId)
                    .observeOn(AndroidSchedulers.mainThread())
                    .doOnComplete { createSuccessMessage(context, itemId.toLong(), mediaId, listSize, title) }
                    .doOnError { createErrorMessage(context) }
                    .subscribe({}, Throwable::printStackTrace)
        }
    }

    private fun createSuccessMessage(context: Context, playlistId: Long, mediaId: MediaId, listSize: Int, title: String){
        val playlist = playlists.first { it.id == playlistId }.title
        val message = if (mediaId.isLeaf){
            context.getString(R.string.added_song_x_to_playlist_y, title, playlist)
        } else {
            context.resources.getQuantityString(R.plurals.xx_songs_added_to_playlist_y, listSize, listSize, playlist)
        }
        context.toast(message)
    }

    private fun createErrorMessage(context: Context){
        context.toast("Something went wrong")
    }

    protected fun share(activity: FragmentActivity, song: Song){
        val intent = Intent()
        intent.action = Intent.ACTION_SEND
        intent.putExtra(Intent.EXTRA_STREAM, FileProvider.getUriForPath(activity, song.path))
        intent.type = "audio/*"
        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
        try {
            if (intent.resolveActivity(activity.packageManager) != null){
                val string = activity.getString(R.string.share_song_x, song.title)
                activity.startActivity(Intent.createChooser(intent, string.asHtml()))
            } else {
                activity.toast(R.string.song_not_shareable)
            }
        } catch (ex: Exception){
            activity.toast(R.string.song_not_shareable)
        }
    }

    protected fun viewInfo(navigator: Navigator, mediaId: MediaId){
        navigator.toEditInfoFragment(activity, mediaId)
    }

    protected  fun viewAlbum(navigator: Navigator, mediaId: MediaId){
        navigator.toDetailFragment(activity, mediaId)
    }

    protected  fun viewArtist(navigator: Navigator, mediaId: MediaId){
        navigator.toDetailFragment(activity, mediaId)
    }

    protected fun setRingtone(navigator: Navigator, mediaId: MediaId, song: Song){
        navigator.toSetRingtoneDialog(activity, mediaId, song.title, song.artist)
    }


}