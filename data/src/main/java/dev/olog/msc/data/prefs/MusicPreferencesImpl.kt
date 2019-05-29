package dev.olog.msc.data.prefs

import android.content.Context
import android.content.SharedPreferences
import androidx.core.content.edit
import com.f2prateek.rx.preferences2.RxSharedPreferences
import dev.olog.msc.core.dagger.qualifier.ApplicationContext
import dev.olog.msc.core.entity.LastMetadata
import dev.olog.msc.core.gateway.prefs.MusicPreferencesGateway
import dev.olog.msc.data.R
import io.reactivex.BackpressureStrategy
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.reactive.flow.asFlow
import javax.inject.Inject

private const val TAG = "MusicPreferences"

private const val BOOKMARK = "$TAG.bookmark"
private const val SHUFFLE_MODE = "$TAG.mode.shuffle"
private const val REPEAT_MODE = "$TAG.mode.repeat"

private const val ID_IN_PLAYLIST = "$TAG.id.in.playlist"

private const val SKIP_PREVIOUS = "$TAG.skip.previous"
private const val SKIP_NEXT = "$TAG.skip.next"

private const val LAST_TITLE = "$TAG.last.title"
private const val LAST_SUBTITLE = "$TAG.last.subtitle"
private const val LAST_ID = "$TAG.last.id"

private const val PLAYBACK_SPEED = "$TAG.playback_speed"

private const val LAST_POSITION = "$TAG.last_position"

internal class MusicPreferencesImpl @Inject constructor(
    @ApplicationContext private val context: Context,
    private val preferences: SharedPreferences,
    private val rxPreferences: RxSharedPreferences

) : MusicPreferencesGateway {

    override fun getBookmark(): Long {
        return preferences.getLong(BOOKMARK, 0)
    }

    override fun setBookmark(bookmark: Long) {
        preferences.edit { putLong(BOOKMARK, bookmark) }
    }

    override fun getLastIdInPlaylist(): Int {
        return preferences.getInt(ID_IN_PLAYLIST, 0)
    }

    override fun setLastIdInPlaylist(idInPlaylist: Int) {
        preferences.edit { putInt(ID_IN_PLAYLIST, idInPlaylist) }
    }

    override fun observeLastIdInPlaylist(): Flow<Int> {
        return rxPreferences.getInteger(ID_IN_PLAYLIST).asObservable()
            .toFlowable(BackpressureStrategy.LATEST)
            .asFlow()
    }

    override fun getRepeatMode(): Int {
        return preferences.getInt(REPEAT_MODE, 0)
    }

    override fun setRepeatMode(repeatMode: Int) {
        preferences.edit { putInt(REPEAT_MODE, repeatMode) }
    }

    override fun getShuffleMode(): Int {
        return preferences.getInt(SHUFFLE_MODE, 0)
    }

    override fun setShuffleMode(shuffleMode: Int) {
        preferences.edit { putInt(SHUFFLE_MODE, shuffleMode) }
    }

    override fun setSkipToPreviousVisibility(visible: Boolean) {
        preferences.edit { putBoolean(SKIP_PREVIOUS, visible) }
    }

    override fun observeSkipToPreviousVisibility(): Flow<Boolean> {
        return rxPreferences.getBoolean(SKIP_PREVIOUS, true).asObservable()
            .toFlowable(BackpressureStrategy.LATEST)
            .asFlow()
            .distinctUntilChanged()
    }

    override fun setSkipToNextVisibility(visible: Boolean) {
        preferences.edit { putBoolean(SKIP_NEXT, visible) }
    }

    override fun observeSkipToNextVisibility(): Flow<Boolean> {
        return rxPreferences.getBoolean(SKIP_NEXT, true).asObservable()
            .toFlowable(BackpressureStrategy.LATEST)
            .asFlow()
            .distinctUntilChanged()
    }

    override fun isMidnightMode(): Flow<Boolean> {
        val key = context.getString(R.string.prefs_midnight_mode_key)
        return rxPreferences.getBoolean(key, false)
            .asObservable()
            .toFlowable(BackpressureStrategy.LATEST)
            .asFlow()
            .distinctUntilChanged()
    }

    override fun getLastMetadata(): LastMetadata {
        return LastMetadata(
            preferences.getString(LAST_TITLE, "")!!,
            preferences.getString(LAST_SUBTITLE, "")!!,
            preferences.getLong(LAST_ID, -1)
        )
    }

    override fun setLastMetadata(metadata: LastMetadata) {
        val (title, subtitle, image) = metadata
        preferences.edit {
            putLong(LAST_ID, metadata.id)
            putString(LAST_TITLE, title)
            putString(LAST_SUBTITLE, subtitle)
        }
    }

    override fun observeLastMetadata(): Flow<LastMetadata> {
        return rxPreferences.getString(LAST_TITLE)
            .asObservable()
            .toFlowable(BackpressureStrategy.LATEST)
            .asFlow()
            .map { getLastMetadata() }
    }

    override fun setDefault() {
        setMidnightMode(false)
        setCrossFade(0)
        setGapless(false)
    }

    private fun setMidnightMode(enable: Boolean) {
        preferences.edit {
            putBoolean(context.getString(R.string.prefs_midnight_mode_key), enable)
        }
    }

    private fun setCrossFade(value: Int) {
        val key = context.getString(R.string.prefs_cross_fade_key)
        preferences.edit { putInt(key, value) }
    }

    override fun observeCrossFade(): Flow<Int> {
        val key = context.getString(R.string.prefs_cross_fade_key)
        return rxPreferences.getInteger(key, 0)
            .asObservable()
            .toFlowable(BackpressureStrategy.LATEST)
            .asFlow()
            .map { it * 1000 }
    }

    override fun observeGapless(): Flow<Boolean> {
        val key = context.getString(R.string.prefs_gapless_key)
        return rxPreferences.getBoolean(key, false)
            .asObservable()
            .toFlowable(BackpressureStrategy.LATEST)
            .asFlow()
            .distinctUntilChanged()
    }

    private fun setGapless(enabled: Boolean) {
        val key = context.getString(R.string.prefs_gapless_key)
        preferences.edit { putBoolean(key, enabled) }
    }

    override fun observePlaybackSpeed(): Flow<Float> {
        return rxPreferences.getFloat(PLAYBACK_SPEED, 1f)
            .asObservable()
            .toFlowable(BackpressureStrategy.LATEST)
            .asFlow()
    }

    override fun setPlaybackSpeed(speed: Float) {
        preferences.edit {
            putFloat(PLAYBACK_SPEED, speed)
        }
    }

    override fun getPlaybackSpeed(): Float {
        return preferences.getFloat(PLAYBACK_SPEED, 1f)
    }

    override fun setLastPositionInQueue(position: Int) {
        preferences.edit {
            putInt(LAST_POSITION, position)
        }
    }

    override fun observeLastPositionInQueue(): Flow<Int> {
        return rxPreferences.getInteger(LAST_POSITION, -1)
            .asObservable()
            .toFlowable(BackpressureStrategy.LATEST)
            .asFlow()
    }

    override fun getLastPositionInQueue(): Int {
        return preferences.getInt(LAST_POSITION, -1)
    }
}