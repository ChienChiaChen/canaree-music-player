package dev.olog.msc.musicservice

import android.appwidget.AppWidgetManager
import android.content.Context
import android.content.Intent
import android.support.v4.media.session.MediaSessionCompat
import android.support.v4.media.session.PlaybackStateCompat
import dev.olog.msc.core.WidgetClasses
import dev.olog.msc.core.dagger.qualifier.ApplicationContext
import dev.olog.msc.core.dagger.scope.PerService
import dev.olog.msc.core.gateway.prefs.MusicPreferencesGateway
import dev.olog.msc.musicservice.model.PositionInQueue
import dev.olog.msc.shared.WidgetConstants
import dev.olog.msc.shared.extensions.getAppWidgetsIdsFor
import dev.olog.msc.shared.extensions.isPlaying
import javax.inject.Inject

@PerService
internal class PlayerState @Inject constructor(
        @ApplicationContext private val context: Context,
        private val mediaSession: MediaSessionCompat,
        private val musicPreferencesUseCase: MusicPreferencesGateway,
//        private val appShortcuts: AppShortcuts, TODO
        private val widgetClasses: WidgetClasses

){

    private val builder = PlaybackStateCompat.Builder()
    private var activeQueueId = MediaSessionCompat.QueueItem.UNKNOWN_ID.toLong()

    init {
        builder.setState(PlaybackStateCompat.STATE_PAUSED, musicPreferencesUseCase.getBookmark(), 0f)
                .setActions(getActions())
    }

    fun prepare(id: Long, bookmark: Long) {
        builder.setActiveQueueItemId(id)
        mediaSession.setPlaybackState(builder.build())

        notifyWidgetsOfStateChanged(false, bookmark)
    }

    fun update(state: Int, bookmark: Long, speed: Float): PlaybackStateCompat {
        return update(state, bookmark, null, speed)
    }

    /**
     * @param state one of: PlaybackStateCompat.STATE_PLAYING, PlaybackStateCompat.STATE_PAUSED
     */
    fun update(state: Int, bookmark: Long, id: Long?, speed: Float): PlaybackStateCompat {
        val isPlaying = state == PlaybackStateCompat.STATE_PLAYING

        if (isPlaying){
            disablePlayShortcut()
        } else {
            enablePlayShortcut()
        }

        builder.setState(state, bookmark, (if (isPlaying) speed else 0f))

        musicPreferencesUseCase.setBookmark(bookmark)

        if (id != null) {
            activeQueueId = id
            builder.setActiveQueueItemId(activeQueueId)
        }

        val playbackState = builder.build()

        notifyWidgetsOfStateChanged(isPlaying, bookmark)

        try {
            mediaSession.setPlaybackState(playbackState)
        } catch (ignored: IllegalStateException) {
            // random crash
        }

        return playbackState
    }

    fun updatePlaybackSpeed(speed: Float) {
        val currentState = mediaSession.controller?.playbackState
        if (currentState == null){
            builder.setState(PlaybackStateCompat.STATE_PAUSED, musicPreferencesUseCase.getBookmark(), 0f)
        } else {
            val stateSpeed = if (currentState.isPlaying()) speed else 0f
            builder.setState(currentState.state, currentState.position, stateSpeed)
        }
        mediaSession.setPlaybackState(builder.build())
    }

    fun updateActiveQueueId(id: Long){
        val state = builder.setActiveQueueItemId(id).build()
        mediaSession.setPlaybackState(state)
    }

    fun toggleSkipToActions(positionInQueue: PositionInQueue) {

        when {
            positionInQueue === PositionInQueue.FIRST -> {
                musicPreferencesUseCase.setSkipToPreviousVisibility(false)
                musicPreferencesUseCase.setSkipToNextVisibility(true)
                notifyWidgetsActionChanged(false, true)
            }
            positionInQueue === PositionInQueue.LAST -> {
                musicPreferencesUseCase.setSkipToPreviousVisibility(true)
                musicPreferencesUseCase.setSkipToNextVisibility(false)
                notifyWidgetsActionChanged(true, false)
            }
            positionInQueue === PositionInQueue.IN_MIDDLE -> {
                musicPreferencesUseCase.setSkipToPreviousVisibility(true)
                musicPreferencesUseCase.setSkipToNextVisibility(true)
                notifyWidgetsActionChanged(true, true)
            }
            positionInQueue == PositionInQueue.BOTH -> {
                musicPreferencesUseCase.setSkipToPreviousVisibility(false)
                musicPreferencesUseCase.setSkipToNextVisibility(false)
                notifyWidgetsActionChanged(false, false)
            }
        }

    }

    fun skipTo(toNext: Boolean) {
        val state = if (toNext) {
            PlaybackStateCompat.STATE_SKIPPING_TO_NEXT
        } else {
            PlaybackStateCompat.STATE_SKIPPING_TO_PREVIOUS
        }
        builder.setState(state, 0, 1f)

        mediaSession.setPlaybackState(builder.build())
    }

//    fun setEmptyQueue(){ TODO set empty queue on every error?
//        val localBuilder = PlaybackStateCompat.Builder(builder.build())
//        localBuilder.setState(PlaybackStateCompat.STATE_ERROR, 0, 0f)
//                .setErrorMessage(PlaybackStateCompat.ERROR_CODE_UNKNOWN_ERROR, context.getString(R.string.music_error_empty_queue))
//
//        mediaSession.setPlaybackState(localBuilder.build())
//    }

    private fun getActions(): Long {
        return PlaybackStateCompat.ACTION_PLAY_PAUSE or
                PlaybackStateCompat.ACTION_PLAY or
                PlaybackStateCompat.ACTION_PAUSE or
                PlaybackStateCompat.ACTION_STOP or
                PlaybackStateCompat.ACTION_PLAY_FROM_MEDIA_ID or
                PlaybackStateCompat.ACTION_PLAY_FROM_SEARCH or
                PlaybackStateCompat.ACTION_SKIP_TO_QUEUE_ITEM or
                PlaybackStateCompat.ACTION_SEEK_TO or
                PlaybackStateCompat.ACTION_SET_SHUFFLE_MODE or
                PlaybackStateCompat.ACTION_SET_REPEAT_MODE or
//                PlaybackStateCompat.ACTION_SET_RATING or
                PlaybackStateCompat.ACTION_SKIP_TO_NEXT or
                PlaybackStateCompat.ACTION_SKIP_TO_PREVIOUS
    }

    private fun notifyWidgetsOfStateChanged(isPlaying: Boolean, bookmark: Long){
        for (clazz in widgetClasses.get()) {
            val ids = context.getAppWidgetsIdsFor(clazz)

            val intent = Intent(context, clazz).apply {
                action = WidgetConstants.STATE_CHANGED
                putExtra(WidgetConstants.ARGUMENT_IS_PLAYING, isPlaying)
                putExtra(WidgetConstants.ARGUMENT_BOOKMARK, bookmark)
                putExtra(AppWidgetManager.EXTRA_APPWIDGET_IDS, ids)
            }

            context.sendBroadcast(intent)
        }
    }

    private fun notifyWidgetsActionChanged(showPrevious: Boolean, showNext: Boolean){
        for (clazz in widgetClasses.get()) {
            val ids = context.getAppWidgetsIdsFor(clazz)

            val intent = Intent(context, clazz).apply {
                action = WidgetConstants.ACTION_CHANGED
                putExtra(WidgetConstants.ARGUMENT_SHOW_PREVIOUS, showPrevious)
                putExtra(WidgetConstants.ARGUMENT_SHOW_NEXT, showNext)
                putExtra(AppWidgetManager.EXTRA_APPWIDGET_IDS, ids)
            }

            context.sendBroadcast(intent)
        }
    }

    private fun disablePlayShortcut(){
//        appShortcuts.disablePlay()
    }


    private fun enablePlayShortcut(){
//        appShortcuts.enablePlay()
    }

}