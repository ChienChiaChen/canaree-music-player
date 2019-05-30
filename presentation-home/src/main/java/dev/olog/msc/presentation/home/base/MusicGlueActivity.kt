package dev.olog.msc.presentation.home.base

import android.os.Bundle
import android.support.v4.media.MediaBrowserCompat
import android.support.v4.media.MediaDescriptionCompat
import android.support.v4.media.MediaMetadataCompat
import android.support.v4.media.RatingCompat
import android.support.v4.media.session.MediaControllerCompat
import android.support.v4.media.session.MediaSessionCompat
import android.support.v4.media.session.PlaybackStateCompat
import androidx.annotation.CallSuper
import androidx.core.os.bundleOf
import androidx.lifecycle.LiveData
import dev.olog.msc.core.MediaId
import dev.olog.msc.core.entity.sort.SortArranging
import dev.olog.msc.core.entity.sort.SortType
import dev.olog.msc.presentation.base.activity.BaseActivity
import dev.olog.msc.presentation.media.*
import dev.olog.msc.presentation.media.connection.OnConnectionChanged
import dev.olog.msc.shared.MusicConstants
import dev.olog.msc.shared.core.lazyFast
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.MainScope

abstract class MusicGlueActivity : BaseActivity(),
    MediaProvider,
    OnConnectionChanged,
    CoroutineScope by MainScope(){

    private val mediaExposer by lazyFast { MediaExposer(this, this) }

    @CallSuper
    override fun onStart() {
        super.onStart()
        mediaExposer.connect()
    }

    @CallSuper
    override fun onStop() {
        super.onStop()
        mediaExposer.disconnect()
        unregisterMediaController()
    }

    override fun onConnectedSuccess(mediaBrowser: MediaBrowserCompat, callback: MediaControllerCompat.Callback) {
        try {
            registerMediaController(mediaBrowser.sessionToken, callback)
            mediaExposer.initialize(MediaControllerCompat.getMediaController(this))
        } catch (ex: Exception) {
            ex.printStackTrace()
            onConnectedFailed(mediaBrowser, callback)
        }
    }

    override fun onConnectedFailed(mediaBrowser: MediaBrowserCompat, callback: MediaControllerCompat.Callback) {
        unregisterMediaController()
    }

    private fun registerMediaController(token: MediaSessionCompat.Token, callback: MediaControllerCompat.Callback){
        val mediaController = MediaControllerCompat(this, token)
        mediaController.registerCallback(callback)
        MediaControllerCompat.setMediaController(this, mediaController)
    }

    private fun unregisterMediaController(){
        val mediaController = MediaControllerCompat.getMediaController(this)
        if (mediaController != null) {
            mediaController.unregisterCallback(mediaExposer.callback)
            MediaControllerCompat.setMediaController(this, null)
        }
    }

    override fun observeMetadata(): LiveData<MediaMetadataCompat> = mediaExposer.observeMetadata()

    override fun observePlaybackState(): LiveData<PlaybackStateCompat> = mediaExposer.observePlaybackState()

    override fun observeRepeat(): LiveData<Int> = mediaExposer.observeRepeat()

    override fun observeShuffle(): LiveData<Int> = mediaExposer.observeShuffle()

    override fun observeQueueTitle(): LiveData<String> = mediaExposer.observeQueueTitle()

    override fun observeExtras(): LiveData<Bundle> = mediaExposer.observeExtras()

    override fun observeQueue(): LiveData<List<MediaSessionCompat.QueueItem>> = mediaExposer.observeQueue()

    private fun transportControls(): MediaControllerCompat.TransportControls? {
        return mediaController()?.transportControls
    }

    private fun mediaController(): MediaControllerCompat? {
        return MediaControllerCompat.getMediaController(this)
    }

    override fun playFromMediaId(mediaId: MediaId, sortType: SortType?, sortArranging: SortArranging?) {
        val bundle = if (sortType != null && sortArranging != null) {
            Bundle().apply {
                putString(MusicConstants.ARGUMENT_SORT_TYPE, sortType.toString())
                putString(MusicConstants.ARGUMENT_SORT_ARRANGING, sortArranging.toString())
            }
        } else null

        transportControls()?.playFromMediaId(mediaId.toString(), bundle)
    }

    override fun playMostPlayed(mediaId: MediaId) {
        val bundle = Bundle()
        bundle.putBoolean(MusicConstants.BUNDLE_MOST_PLAYED, true)
        transportControls()?.playFromMediaId(mediaId.toString(), bundle)
    }

    override fun playRecentlyAdded(mediaId: MediaId) {
        val bundle = Bundle()
        bundle.putBoolean(MusicConstants.BUNDLE_RECENTLY_PLAYED, true)
        transportControls()?.playFromMediaId(mediaId.toString(), bundle)
    }

    override fun skipToQueueItem(idInPlaylist: Long) {
        transportControls()?.skipToQueueItem(idInPlaylist)
    }

    override fun shuffle(mediaId: MediaId) {
        val bundle = Bundle()
        bundle.putString(MediaMetadataCompat.METADATA_KEY_MEDIA_ID, mediaId.toString())
        transportControls()?.sendCustomAction(MusicConstants.ACTION_SHUFFLE, bundle)
    }

    override fun skipToNext() {
        mediaController()?.skipToNext()
    }

    override fun skipToPrevious() {
        mediaController()?.skipToPrevious()
    }

    override fun playPause() {
        mediaController()?.playPause()
    }

    override fun seekTo(where: Long) {
        mediaController()?.seekTo(where)
    }

    override fun toggleShuffleMode() {
        mediaController()?.toggleShuffleMode()
    }

    override fun toggleRepeatMode() {
        mediaController()?.toggleRepeatMode()
        transportControls()?.setRepeatMode(PlaybackStateCompat.REPEAT_MODE_INVALID)
    }

    override fun togglePlayerFavorite() {
        transportControls()?.setRating(RatingCompat.newHeartRating(false))

    }

    override fun swap(from: Int, to: Int) {
        val bundle = bundleOf(
            MusicConstants.ARGUMENT_SWAP_FROM to from,
            MusicConstants.ARGUMENT_SWAP_TO to to
        )
        transportControls()?.sendCustomAction(MusicConstants.ACTION_SWAP, bundle)
    }

    override fun swapRelative(from: Int, to: Int) {
        val bundle = bundleOf(
            MusicConstants.ARGUMENT_SWAP_FROM to from,
            MusicConstants.ARGUMENT_SWAP_TO to to
        )
        transportControls()?.sendCustomAction(MusicConstants.ACTION_SWAP_RELATIVE, bundle)
    }

    override fun remove(position: Int) {
        val bundle = bundleOf(
            MusicConstants.ARGUMENT_REMOVE_POSITION to position
        )
        transportControls()?.sendCustomAction(MusicConstants.ACTION_REMOVE, bundle)
    }

    override fun removeRelative(position: Int) {
        val bundle = bundleOf(
            MusicConstants.ARGUMENT_REMOVE_POSITION to position
        )
        transportControls()?.sendCustomAction(MusicConstants.ACTION_REMOVE_RELATIVE, bundle)
    }

    override fun addToPlayNext(mediaId: MediaId) {
        val trackId = "${mediaId.leaf!!}"
        val item = MediaDescriptionCompat.Builder()
            .setMediaId(trackId)
            .setExtras(bundleOf(MusicConstants.IS_PODCAST to mediaId.isAnyPodcast))
            .build()
        MediaControllerCompat.getMediaController(this).addQueueItem(item, Int.MAX_VALUE)
    }

    override fun moveToPlayNext(mediaId: MediaId) {
//        val trackId = "${mediaId.leaf!!}"
//        val item = MediaDescriptionCompat.Builder()
//                .setMediaId(trackId)
//                .setExtras(bundleOf(MusicConstants.IS_PODCAST to mediaId.isAnyPodcast))
//                .build()
//        MediaControllerCompat.getMediaController(this).addQueueItem(item, Int.MAX_VALUE - 1)
    }

    override fun replayTenSeconds() {
        transportControls()?.sendCustomAction(MusicConstants.ACTION_REPLAY_10_SECONDS, null)
    }

    override fun forwardTenSeconds() {
        transportControls()?.sendCustomAction(MusicConstants.ACTION_FORWARD_10_SECONDS, null)
    }

    override fun replayThirtySeconds() {
        transportControls()?.sendCustomAction(MusicConstants.ACTION_REPLAY_30_SECONDS, null)
    }

    override fun forwardThirtySeconds() {
        transportControls()?.sendCustomAction(MusicConstants.ACTION_FORWARD_30_SECONDS, null)
    }
}