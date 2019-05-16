package dev.olog.msc.musicservice

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.support.v4.media.MediaDescriptionCompat
import android.support.v4.media.MediaMetadataCompat
import android.support.v4.media.RatingCompat
import android.support.v4.media.session.MediaSessionCompat
import android.view.KeyEvent
import dev.olog.msc.core.MediaId
import dev.olog.msc.core.dagger.scope.PerService
import dev.olog.msc.musicservice.ActionManager.Action
import dev.olog.msc.musicservice.interfaces.Player
import dev.olog.msc.shared.MusicConstants
import javax.inject.Inject

@PerService
internal class MediaSessionCallback @Inject constructor(
    private val actionManager: ActionManager,
    private val player: Player,
    private val mediaButton: MediaButton

) : MediaSessionCompat.Callback() {

    init {
        actionManager.callback = player
        onPrepare()
    }

    override fun onPrepare() {
        actionManager.dispatchAction(Action.Prepare)
    }

    override fun onPlayFromMediaId(mediaIdAsString: String, extras: Bundle?) {
        if (extras != null){
            val mediaId = MediaId.fromString(mediaIdAsString)
            actionManager.dispatchAction(Action.PlayFromMediaId(mediaId, extras))
        }
    }

    override fun onPlayFromSearch(query: String, extras: Bundle) {
        updatePodcastPosition()
        actionManager.dispatchAction(Action.PlayFromSearch(query, extras))
    }

    override fun onPlayFromUri(uri: Uri, extras: Bundle?) {
        updatePodcastPosition()
        actionManager.dispatchAction(Action.PlayFromUri(uri))
    }

    override fun onPlay() {
        actionManager.dispatchAction(Action.Resume)
    }

    override fun onPause() {
        updatePodcastPosition()
        actionManager.dispatchAction(Action.Pause(true))
    }

    override fun onStop() {
        onPause()
    }

    override fun onSkipToNext() {
        onSkipToNext(false)
    }

    private fun onTrackEnded() {
        onSkipToNext(true)
    }

    /**
     * Try to skip to next song, if can't, restart current
     */
    private fun onSkipToNext(trackEnded: Boolean) {
        updatePodcastPosition()
        actionManager.dispatchAction(Action.SkipToNext(trackEnded))
    }

    override fun onSkipToPrevious() {
        updatePodcastPosition()
        actionManager.dispatchAction(Action.SkipToPrevious(player.getBookmark()))
    }

    override fun onSkipToQueueItem(id: Long) {
        updatePodcastPosition()
        actionManager.dispatchAction(Action.SkipToQueueItem(id = id))
    }

    override fun onSeekTo(pos: Long) {
        updatePodcastPosition()
        actionManager.dispatchAction(Action.Seek(pos = pos))
    }

    override fun onSetRating(rating: RatingCompat?) {
        onSetRating(rating, null)
    }

    override fun onSetRating(rating: RatingCompat?, extras: Bundle?) {
        actionManager.dispatchAction(Action.SetRating)
    }

    @Suppress("MoveLambdaOutsideParentheses")
    override fun onCustomAction(action: String?, extras: Bundle?) {
        if (action == null){
            return
        }
        when (action){
            MusicConstants.ACTION_SWAP -> {
                val from = extras!!.getInt(MusicConstants.ARGUMENT_SWAP_FROM, 0)
                val to = extras!!.getInt(MusicConstants.ARGUMENT_SWAP_TO, 0)
                actionManager.dispatchAction(Action.Swap(from, to, false))
            }
            MusicConstants.ACTION_SWAP_RELATIVE -> {
                val from = extras!!.getInt(MusicConstants.ARGUMENT_SWAP_FROM, 0)
                val to = extras!!.getInt(MusicConstants.ARGUMENT_SWAP_TO, 0)
                actionManager.dispatchAction(Action.Swap(from, to, true))
            }
            MusicConstants.ACTION_REMOVE -> {
                val position = extras!!.getInt(MusicConstants.ARGUMENT_REMOVE_POSITION)
                actionManager.dispatchAction(Action.Remove(position, false, { if (it) { onStop() } }))
            }
            MusicConstants.ACTION_REMOVE_RELATIVE -> {
                val position = extras!!.getInt(MusicConstants.ARGUMENT_REMOVE_POSITION)
                actionManager.dispatchAction(Action.Remove(position, true, { if (it) { onStop() } }))
            }
            MusicConstants.ACTION_SHUFFLE -> {
                updatePodcastPosition()
                val mediaId = extras!!.getString(MediaMetadataCompat.METADATA_KEY_MEDIA_ID)!!
                actionManager.dispatchAction(Action.PlayShuffle(MediaId.fromString(mediaId)))
            }
            MusicConstants.ACTION_FORWARD_10_SECONDS -> {
                actionManager.dispatchAction(Action.ForwardBy(10))
            }
            MusicConstants.ACTION_REPLAY_10_SECONDS -> {
                actionManager.dispatchAction(Action.ReplayBy(10))
            }
            MusicConstants.ACTION_FORWARD_30_SECONDS -> {
                actionManager.dispatchAction(Action.ForwardBy(30))
            }
            MusicConstants.ACTION_REPLAY_30_SECONDS -> {
                actionManager.dispatchAction(Action.ReplayBy(30))
            }
        }
    }

    override fun onSetRepeatMode(repeatMode: Int) {
        actionManager.dispatchAction(Action.RepeatChanged)
    }

    override fun onSetShuffleMode(unused: Int) {
        actionManager.dispatchAction(Action.ShuffleChanged)
    }

    override fun onMediaButtonEvent(mediaButtonEvent: Intent): Boolean {
        val event = mediaButtonEvent.getParcelableExtra<KeyEvent>(Intent.EXTRA_KEY_EVENT)

        if (event.action == KeyEvent.ACTION_DOWN) {
            val keyCode = event.keyCode

            when (keyCode) {
                KeyEvent.KEYCODE_MEDIA_PLAY_PAUSE -> handlePlayPause()
                KeyEvent.KEYCODE_MEDIA_NEXT -> onSkipToNext()
                KeyEvent.KEYCODE_MEDIA_PREVIOUS -> onSkipToPrevious()
                KeyEvent.KEYCODE_MEDIA_STOP -> player.stopService()
                KeyEvent.KEYCODE_MEDIA_PAUSE -> actionManager.dispatchAction(Action.Pause(false))
                KeyEvent.KEYCODE_MEDIA_PLAY -> onPlay()
                KeyEvent.KEYCODE_MEDIA_FAST_FORWARD -> onTrackEnded()
                else -> mediaButton.onNextEvent(mediaButtonEvent)
            }
        }

        return true
    }


    /**
    Play later
     */
    override fun onAddQueueItem(description: MediaDescriptionCompat) {
//        val split = description.mediaId!!.split(",") TODO
//        val position = queue.playLater(split.map { it.trim().toLong() },
//                description.extras!!.getBoolean(MusicConstants.IS_PODCAST))
//        playerState.toggleSkipToActions(position)
    }

    /**
    When [index] == [Int.MAX_VALUE] -> play next
     */
    override fun onAddQueueItem(description: MediaDescriptionCompat, index: Int) {
//        when (index) { TODO
//            Int.MAX_VALUE -> {
////                 play next
//                val split = description.mediaId!!.split(",")
//                val position = queue.playNext(split.map { it.trim().toLong() },
//                        description.extras!!.getBoolean(MusicConstants.IS_PODCAST))
//                playerState.toggleSkipToActions(position)
//            }
//        }
    }

    /**
     * this function DO NOT KILL service on pause
     */
    fun handlePlayPause() {
        if (player.isPlaying()) {
            player.onPause(false, true)
        } else {
            onPlay()
        }
    }

    private fun updatePodcastPosition() {
//        queue.updatePodcastPosition(player.getBookmark()) TODO
    }

}