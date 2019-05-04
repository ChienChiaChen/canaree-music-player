package dev.olog.msc.musicservice

import android.app.AlarmManager
import android.app.PendingIntent
import android.app.SearchManager
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.support.v4.media.MediaBrowserCompat
import android.support.v4.media.MediaMetadataCompat
import android.support.v4.media.RatingCompat
import android.support.v4.media.session.MediaSessionCompat
import dagger.Lazy
import dev.olog.msc.core.Classes
import dev.olog.msc.core.MediaId
import dev.olog.msc.core.MediaIdCategory
import dev.olog.msc.core.interactor.SleepTimerUseCase
import dev.olog.msc.musicservice.helper.CarHelper
import dev.olog.msc.musicservice.helper.MediaIdHelper
import dev.olog.msc.musicservice.helper.MediaItemGenerator
import dev.olog.msc.musicservice.helper.WearHelper
import dev.olog.msc.musicservice.notification.MusicNotificationManager
import dev.olog.msc.shared.FileProvider
import dev.olog.msc.shared.MusicConstants
import dev.olog.msc.shared.extensions.asServicePendingIntent
import dev.olog.msc.shared.extensions.toast
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.rxkotlin.addTo
import javax.inject.Inject

class MusicService : dev.olog.msc.musicservice.BaseMusicService() {

    companion object {
        const val TAG = "MusicService"
    }

    @Inject lateinit var mediaSession: MediaSessionCompat
    @Inject lateinit var callback: MediaSessionCallback

    @Inject lateinit var currentSong : CurrentSong
    @Inject lateinit var playerMetadata: PlayerMetadata
    @Inject lateinit var notification: MusicNotificationManager
    @Inject lateinit var sleepTimerUseCase: SleepTimerUseCase
    @Inject lateinit var mediaItemGenerator: Lazy<MediaItemGenerator>
    @Inject lateinit var alarmManager: AlarmManager
    @Inject lateinit var lastFmScrobbling: LastFmScrobbling
    @Inject lateinit var classes: Classes

    private val subsriptions = CompositeDisposable()

    override fun onCreate() {
        super.onCreate()

        sessionToken = mediaSession.sessionToken

        mediaSession.setFlags(
                MediaSessionCompat.FLAG_HANDLES_MEDIA_BUTTONS or
                        MediaSessionCompat.FLAG_HANDLES_TRANSPORT_CONTROLS or
                        MediaSessionCompat.FLAG_HANDLES_QUEUE_COMMANDS)

        mediaSession.setMediaButtonReceiver(buildMediaButtonReceiverPendingIntent())
        mediaSession.setSessionActivity(buildSessionActivityPendingIntent())
        mediaSession.setRatingType(RatingCompat.RATING_HEART)
        mediaSession.setCallback(callback)

        mediaSession.isActive = true
    }

    override fun onDestroy() {
        super.onDestroy()
        resetSleepTimer()
        subsriptions.clear()
        mediaSession.setMediaButtonReceiver(null)
        mediaSession.setCallback(null)
        mediaSession.isActive = false
        mediaSession.release()
    }

    override fun handleAppShortcutPlay(intent: Intent) {
        try {
            mediaSession.controller.transportControls.play()
        } catch (ex: Exception){
            this.applicationContext.toast("Please check your storage permission, contact the developer if the problem persists")
        }
    }

    override fun handleAppShortcutShuffle(intent: Intent) {
        try {
            val bundle = Bundle()
            bundle.putString(MediaMetadataCompat.METADATA_KEY_MEDIA_ID, MediaId.shuffleAllId().toString())
            mediaSession.controller.transportControls.sendCustomAction(
                    MusicConstants.ACTION_SHUFFLE, bundle)
        } catch (ex: Exception){
            this.applicationContext.toast("Please check your storage permission, contact the developer if the problem persists")
        }
    }

    override fun handlePlayPause(intent: Intent) {
        callback.handlePlayPause()
    }

    override fun handleSkipNext(intent: Intent) {
        callback.onSkipToNext()
    }

    override fun handleSkipPrevious(intent: Intent) {
        callback.onSkipToPrevious()
    }

    override fun handleSkipToItem(intent: Intent) {
        val id = intent.getIntExtra(MusicConstants.EXTRA_SKIP_TO_ITEM_ID, -1)
        callback.onSkipToQueueItem(id.toLong())
    }

    override fun handleMediaButton(intent: Intent) {
        androidx.media.session.MediaButtonReceiver.handleIntent(mediaSession, intent)
    }

    override fun handleToggleFavorite() {
        callback.onSetRating(null)
    }

    override fun handleSleepTimerEnd(intent: Intent) {
        sleepTimerUseCase.reset()
        mediaSession.controller.transportControls.pause()
    }

    override fun handlePlayFromVoiceSearch(intent: Intent) {
        val voiceParams = intent.extras!!
        val query = voiceParams.getString(SearchManager.QUERY)!!
        callback.onPlayFromSearch(query, voiceParams)
    }

    override fun handlePlayFromUri(intent: Intent) {
        intent.data?.let { uri ->
            callback.onPlayFromUri(uri, null)
        }
    }

    private fun resetSleepTimer(){
        sleepTimerUseCase.reset()
        alarmManager.cancel(PendingIntents.stopMusicServiceIntent(this))
    }

    override fun onGetRoot(clientPackageName: String, clientUid: Int, rootHints: Bundle?): BrowserRoot? {
        if (clientPackageName == packageName){
            return BrowserRoot(MediaIdHelper.MEDIA_ID_ROOT, null)
        }

        if (CarHelper.isValidCarPackage(clientPackageName)){
            grantUriPermissions(CarHelper.AUTO_APP_PACKAGE_NAME)
            return BrowserRoot(MediaIdHelper.MEDIA_ID_ROOT, null)
        }
        if (WearHelper.isValidWearCompanionPackage(clientPackageName)){
            return BrowserRoot(MediaIdHelper.MEDIA_ID_ROOT, null)
        }

        return null
    }

    override fun onLoadChildren(parentId: String, result: Result<MutableList<MediaBrowserCompat.MediaItem>>) {
        if (parentId == MediaIdHelper.MEDIA_ID_ROOT){
            result.sendResult(MediaIdHelper.getLibraryCategories(this))
            return
        }
        val mediaIdCategory = MediaIdCategory.values()
                .toList()
                .firstOrNull { it.toString() == parentId }

        if (mediaIdCategory != null){
            result.detach()
            mediaItemGenerator.get().getCategoryChilds(mediaIdCategory)
                    .map { it.toMutableList() }
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(result::sendResult, Throwable::printStackTrace)
                    .addTo(subsriptions)
            return
        }
        val mediaId = MediaId.fromString(parentId)
        result.detach()

        mediaItemGenerator.get().getCategoryValueChilds(mediaId)
                .map { it.toMutableList() }
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(result::sendResult, Throwable::printStackTrace)
                .addTo(subsriptions)

    }

    private fun grantUriPermissions(packageName: String){
        try {
            grantUriPermission(packageName,
                    Uri.parse("content://media/external/audio/albumart"),
                    Intent.FLAG_GRANT_READ_URI_PERMISSION)
        } catch (ex: Exception){}

        try {
            grantUriPermission(packageName,
                    FileProvider.getUriForPath(this, cacheDir.path),
                    Intent.FLAG_GRANT_READ_URI_PERMISSION)
        } catch (ex: Throwable){}
    }

    private fun buildMediaButtonReceiverPendingIntent(): PendingIntent {
        val intent = Intent(Intent.ACTION_MEDIA_BUTTON)
        intent.setClass(this, this.javaClass)
        return intent.asServicePendingIntent(this, PendingIntent.FLAG_UPDATE_CURRENT)
    }

    private fun buildSessionActivityPendingIntent(): PendingIntent {
        return PendingIntent.getActivity(this, 0,
                Intent(this, classes.mainClass()::class.java), PendingIntent.FLAG_CANCEL_CURRENT)
    }
}