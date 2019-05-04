package dev.olog.msc.presentation.base.music.service

import android.content.ComponentName
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
import dev.olog.msc.core.MediaId
import dev.olog.msc.presentation.base.BaseActivity
import dev.olog.msc.presentation.detail.sort.DetailSort
import dev.olog.msc.shared.MusicConstants
import dev.olog.msc.shared.extensions.unsubscribe
import io.reactivex.Observable
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import io.reactivex.subjects.BehaviorSubject

abstract class MusicGlueActivity : BaseActivity(), MediaProvider {

    private lateinit var mediaBrowser: MediaBrowserCompat
    private lateinit var callback : MediaControllerCompat.Callback

    private val publisher = BehaviorSubject.createDefault(MusicServiceConnectionState.NONE)
    private var connectionDisposable: Disposable? = null

    internal val metadataPublisher = BehaviorSubject.create<MediaMetadataCompat>()
    internal val statePublisher = BehaviorSubject.create<PlaybackStateCompat>()
    internal val repeatModePublisher = BehaviorSubject.create<Int>()
    internal val shuffleModePublisher = BehaviorSubject.create<Int>()
    internal val queuePublisher = BehaviorSubject.createDefault(mutableListOf<MediaSessionCompat.QueueItem>())
    internal val queueTitlePublisher = BehaviorSubject.create<String>()
    internal val extrasPublisher = BehaviorSubject.create<Bundle>()



    @CallSuper
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        mediaBrowser = MediaBrowserCompat(this,
                ComponentName(this, dev.olog.msc.musicservice.MusicService::class.java),
                MusicServiceConnection(this), null)

        callback = MediaServiceCallback(this)
    }

    @CallSuper
    override fun onStart() {
        super.onStart()

        connectionDisposable = publisher.subscribe({
            when (it){
                MusicServiceConnectionState.CONNECTED -> onConnected()
                MusicServiceConnectionState.FAILED -> onConnectionFailed()
                else -> {}
            }

        }, Throwable::printStackTrace)

        mediaBrowser.connect()
    }

    @CallSuper
    override fun onStop() {
        super.onStop()
        connectionDisposable.unsubscribe()
        mediaBrowser.disconnect()
        val mediaController = MediaControllerCompat.getMediaController(this)
        if (mediaController != null){
            mediaController.unregisterCallback(callback)
            MediaControllerCompat.setMediaController(this, null)
        }
    }

    private fun onConnected(){
        try {
            val mediaController = MediaControllerCompat(this, mediaBrowser.sessionToken)
            mediaController.registerCallback(callback)
            MediaControllerCompat.setMediaController(this, mediaController)
            initialize(mediaController)
        } catch (ex: Exception){
            ex.printStackTrace()
            onConnectionFailed()
        }
    }

    private fun initialize(mediaController : MediaControllerCompat){
        callback.onMetadataChanged(mediaController.metadata)
        callback.onPlaybackStateChanged(mediaController.playbackState)
        callback.onRepeatModeChanged(mediaController.repeatMode)
        callback.onShuffleModeChanged(mediaController.shuffleMode)
        callback.onQueueChanged(mediaController.queue)
    }

    private fun onConnectionFailed(){
        val mediaController = MediaControllerCompat.getMediaController(this)
        if (mediaController != null){
            mediaController.unregisterCallback(callback)
            MediaControllerCompat.setMediaController(this, null)
        }
    }

    internal fun updateConnectionState(state: MusicServiceConnectionState){
        publisher.onNext(state)
    }

    override fun onMetadataChanged(): Observable<MediaMetadataCompat> {
        return metadataPublisher.observeOn(Schedulers.computation())
    }

    override fun onStateChanged(): Observable<PlaybackStateCompat> {
        return statePublisher.observeOn(Schedulers.computation())
    }

    override fun onRepeatModeChanged(): Observable<Int> {
        return repeatModePublisher.observeOn(Schedulers.computation())
    }

    override fun onShuffleModeChanged(): Observable<Int> {
        return shuffleModePublisher.observeOn(Schedulers.computation())
    }

    override fun onQueueTitleChanged(): Observable<String> {
        return queueTitlePublisher
    }

    override fun onExtrasChanged(): Observable<Bundle> {
        return extrasPublisher.observeOn(Schedulers.computation())
    }

    private fun getTransportControls(): MediaControllerCompat.TransportControls? {
        val mediaController = MediaControllerCompat.getMediaController(this)
        if (mediaController != null){
            return mediaController.transportControls
        }
        return null
    }

    override fun playFromMediaId(mediaId: MediaId, sort: DetailSort?) {
        val bundle = if (sort != null){
            Bundle().apply {
                putString(MusicConstants.ARGUMENT_SORT_TYPE, sort.sortType.toString())
                putString(MusicConstants.ARGUMENT_SORT_ARRANGING, sort.sortArranging.toString())
            }
        } else null

        getTransportControls()?.playFromMediaId(mediaId.toString(), bundle)
    }

    override fun playMostPlayed(mediaId: MediaId) {
        val bundle = Bundle()
        bundle.putBoolean(MusicConstants.BUNDLE_MOST_PLAYED, true)
        getTransportControls()?.playFromMediaId(mediaId.toString(), bundle)
    }

    override fun playRecentlyAdded(mediaId: MediaId) {
        val bundle = Bundle()
        bundle.putBoolean(MusicConstants.BUNDLE_RECENTLY_PLAYED, true)
        getTransportControls()?.playFromMediaId(mediaId.toString(), bundle)
    }

    override fun skipToQueueItem(idInPlaylist: Long) {
        getTransportControls()?.skipToQueueItem(idInPlaylist)
    }

    override fun shuffle(mediaId: MediaId) {
        val bundle = Bundle()
        bundle.putString(MediaMetadataCompat.METADATA_KEY_MEDIA_ID, mediaId.toString())
        getTransportControls()?.sendCustomAction(MusicConstants.ACTION_SHUFFLE, bundle)
    }

    override fun skipToNext() {
        getTransportControls()?.skipToNext()
    }

    override fun skipToPrevious() {
        getTransportControls()?.skipToPrevious()
    }

    override fun playPause() {
        val mediaController = MediaControllerCompat.getMediaController(this)
        mediaController?.playbackState?.let {
            val state = it.state
            when (state){
                PlaybackStateCompat.STATE_PLAYING -> getTransportControls()?.pause()
                PlaybackStateCompat.STATE_PAUSED -> getTransportControls()?.play()
                else -> { }
            }
        }
    }

    override fun seekTo(where: Long) {
        getTransportControls()?.seekTo(where)
    }

    override fun toggleShuffleMode() {
        getTransportControls()?.setShuffleMode(PlaybackStateCompat.SHUFFLE_MODE_INVALID)
    }

    override fun toggleRepeatMode() {
        getTransportControls()?.setRepeatMode(PlaybackStateCompat.REPEAT_MODE_INVALID)
    }

    override fun togglePlayerFavorite() {
        getTransportControls()?.setRating(RatingCompat.newHeartRating(false))

    }

    override fun onQueueChanged(): Observable<List<MediaSessionCompat.QueueItem>> {
        return queuePublisher
                .observeOn(Schedulers.computation())
                .map { it.toList() }
    }

    override fun swap(from: Int, to: Int) {
        val bundle = bundleOf(
                MusicConstants.ARGUMENT_SWAP_FROM to from,
                MusicConstants.ARGUMENT_SWAP_TO to to
        )
        getTransportControls()?.sendCustomAction(MusicConstants.ACTION_SWAP, bundle)
    }

    override fun swapRelative(from: Int, to: Int) {
        val bundle = bundleOf(
                MusicConstants.ARGUMENT_SWAP_FROM to from,
                MusicConstants.ARGUMENT_SWAP_TO to to
        )
        getTransportControls()?.sendCustomAction(MusicConstants.ACTION_SWAP_RELATIVE, bundle)
    }

    override fun remove(position: Int) {
        val bundle = bundleOf(
                MusicConstants.ARGUMENT_REMOVE_POSITION to position
        )
        getTransportControls()?.sendCustomAction(MusicConstants.ACTION_REMOVE, bundle)
    }

    override fun removeRelative(position: Int) {
        val bundle = bundleOf(
                MusicConstants.ARGUMENT_REMOVE_POSITION to position
        )
        getTransportControls()?.sendCustomAction(MusicConstants.ACTION_REMOVE_RELATIVE, bundle)
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
        getTransportControls()?.sendCustomAction(MusicConstants.ACTION_REPLAY_10_SECONDS, null)
    }

    override fun forwardTenSeconds() {
        getTransportControls()?.sendCustomAction(MusicConstants.ACTION_FORWARD_10_SECONDS, null)
    }

    override fun replayThirtySeconds() {
        getTransportControls()?.sendCustomAction(MusicConstants.ACTION_REPLAY_30_SECONDS, null)
    }

    override fun forwardThirtySeconds() {
        getTransportControls()?.sendCustomAction(MusicConstants.ACTION_FORWARD_30_SECONDS, null)
    }
}