package dev.olog.msc.apilastfm.data

import dev.olog.msc.apilastfm.LastFmService
import dev.olog.msc.apilastfm.annotation.Proxy
import dev.olog.msc.apilastfm.mapper.LastFmNulls
import dev.olog.msc.apilastfm.mapper.toDomain
import dev.olog.msc.apilastfm.mapper.toModel
import dev.olog.msc.core.entity.LastFmTrack
import dev.olog.msc.core.entity.track.Song
import dev.olog.msc.core.gateway.track.SongGateway
import dev.olog.msc.data.db.AppDatabase
import dev.olog.msc.data.entity.LastFmTrackEntity
import dev.olog.msc.shared.TrackUtils
import dev.olog.msc.shared.utils.TextUtils
import dev.olog.msc.shared.utils.assertBackgroundThread
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import javax.inject.Inject

internal class LastFmRepoTrack @Inject constructor(
    appDatabase: AppDatabase,
    @Proxy private val lastFmService: LastFmService,
    private val songGateway: SongGateway
) {

    private val dao = appDatabase.lastFmDao()

    suspend fun shouldFetch(trackId: Long): Boolean {
        assertBackgroundThread()
        return dao.getTrack(trackId) == null
    }

    fun getOriginalItem(trackId: Long): Song? {
        assertBackgroundThread()
        return songGateway.getByParam(trackId).getItem()
    }

    suspend fun get(trackId: Long): LastFmTrack? {
        assertBackgroundThread()
        val cachedValue = getFromCache(trackId)
        if (cachedValue != null) {
            return cachedValue
        }

        val song = getOriginalItem(trackId)
        if (song != null) {
            return fetch(song)
        }
        return null
    }

    private suspend fun getFromCache(trackId: Long): LastFmTrack? {
        assertBackgroundThread()
        val track = dao.getTrack(trackId)
        return track?.toDomain()
    }

    private suspend fun fetch(track: Song): LastFmTrack {
        assertBackgroundThread()

        val trackId = track.id

        val trackTitle = TextUtils.addSpacesToDash(track.title)
        val trackArtist = if (track.artist == TrackUtils.UNKNOWN_ARTIST) "" else track.artist

        try {
            val trackInfo = lastFmService.getTrackInfoAsync(trackTitle, trackArtist).await().toDomain(trackId)
            return cacheAsync(trackInfo).await().toDomain()
        } catch (ex: Exception) {
            try {
                var trackInfo = lastFmService.getTrackInfoAsync(trackTitle, trackArtist).await().toDomain(trackId)
                try {
                    trackInfo = lastFmService.getTrackInfoAsync(trackInfo.title, trackInfo.artist).await().toDomain(trackId)
                } catch (ignored: Exception) {
                }
                return cacheAsync(trackInfo).await().toDomain()
            } catch (ex: Exception) {
                return cacheEmptyAsync(trackId).await().toDomain()
            }
        }
    }

    private suspend fun cacheAsync(model: LastFmTrack): Deferred<LastFmTrackEntity> = GlobalScope.async {
        assertBackgroundThread()
        val entity = model.toModel()
        dao.insertTrack(entity)
        entity
    }

    private suspend fun cacheEmptyAsync(trackId: Long): Deferred<LastFmTrackEntity> = GlobalScope.async {
        assertBackgroundThread()
        val entity = LastFmNulls.createNullTrack(trackId)
        dao.insertTrack(entity)
        entity
    }

    suspend fun delete(trackId: Long) = GlobalScope.launch {
        assertBackgroundThread()
        dao.deleteTrack(trackId)
    }

}