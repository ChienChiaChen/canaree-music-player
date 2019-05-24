package dev.olog.msc.presentation.popup

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import androidx.appcompat.widget.PopupMenu
import androidx.fragment.app.FragmentActivity
import dev.olog.msc.core.MediaId
import dev.olog.msc.core.entity.PlaylistType
import dev.olog.msc.core.entity.track.Song
import dev.olog.msc.core.interactor.GetPlaylistsBlockingUseCase
import dev.olog.msc.presentation.base.extensions.asHtml
import dev.olog.msc.presentation.navigator.Navigator
import dev.olog.msc.presentation.popup.domain.AddToPlaylistUseCase
import dev.olog.msc.shared.FileProvider
import dev.olog.msc.shared.core.lazyFast
import dev.olog.msc.shared.extensions.toast
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

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
        getPlaylistBlockingUseCase.execute(
            if (podcastPlaylist) PlaylistType.PODCAST
            else PlaylistType.TRACK
        )
    }

    @SuppressLint("RxLeakedSubscription")
    protected fun onPlaylistSubItemClick(
        context: Context,
        itemId: Int,
        mediaId: MediaId,
        listSize: Int,
        title: String
    ) {
        playlists.firstOrNull { it.id == itemId.toLong() }?.let { playlist ->
            GlobalScope.launch {
                try {
                    addToPlaylistUseCase.execute(playlist to mediaId)
                    withContext(Dispatchers.Main) {
                        createSuccessMessage(context, itemId.toLong(), mediaId, listSize, title)
                    }
                } catch (ex: Exception) {
                    withContext(Dispatchers.Main) {
                        createErrorMessage(context)
                    }
                }
            }
        }
    }

    private fun createSuccessMessage(
        context: Context,
        playlistId: Long,
        mediaId: MediaId,
        listSize: Int,
        title: String
    ) {
        val playlist = playlists.first { it.id == playlistId }.title
        val message = if (mediaId.isLeaf) {
            context.getString(R.string.added_song_x_to_playlist_y, title, playlist)
        } else {
            context.resources.getQuantityString(R.plurals.xx_songs_added_to_playlist_y, listSize, listSize, playlist)
        }
        context.toast(message)
    }

    private fun createErrorMessage(context: Context) {
        context.toast("Something went wrong")
    }

    protected fun share(activity: FragmentActivity, song: Song) {
        val intent = Intent()
        intent.action = Intent.ACTION_SEND
        intent.putExtra(Intent.EXTRA_STREAM, FileProvider.getUriForPath(activity, song.path))
        intent.type = "audio/*"
        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
        try {
            if (intent.resolveActivity(activity.packageManager) != null) {
                val string = activity.getString(R.string.share_song_x, song.title)
                activity.startActivity(Intent.createChooser(intent, string.asHtml()))
            } else {
                activity.toast(R.string.song_not_shareable)
            }
        } catch (ex: Exception) {
            activity.toast(R.string.song_not_shareable)
        }
    }

    protected fun viewInfo(navigator: Navigator, mediaId: MediaId) {
        navigator.toEditInfoFragment(activity, mediaId)
    }

    protected fun viewAlbum(navigator: Navigator, mediaId: MediaId) {
        navigator.toDetailFragment(activity, mediaId)
    }

    protected fun viewArtist(navigator: Navigator, mediaId: MediaId) {
        navigator.toDetailFragment(activity, mediaId)
    }

    protected fun setRingtone(navigator: Navigator, mediaId: MediaId, song: Song) {
        navigator.toSetRingtoneDialog(activity, mediaId, song.title, song.artist)
    }


}