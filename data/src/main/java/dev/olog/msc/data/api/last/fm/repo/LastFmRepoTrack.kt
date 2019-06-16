package dev.olog.msc.data.api.last.fm.repo

import android.provider.MediaStore
import dev.olog.msc.core.entity.LastFmTrack
import dev.olog.msc.core.entity.Song
import dev.olog.msc.core.gateway.SongGateway
import dev.olog.msc.data.api.last.fm.LastFmService
import dev.olog.msc.data.api.last.fm.annotation.Proxy
import dev.olog.msc.data.api.last.fm.mapper.LastFmNulls
import dev.olog.msc.data.api.last.fm.mapper.toDomain
import dev.olog.msc.data.api.last.fm.mapper.toModel
import dev.olog.msc.data.dao.AppDatabase
import dev.olog.msc.data.entity.LastFmTrackEntity
import dev.olog.msc.data.utils.TextUtils
import kotlinx.coroutines.rx2.awaitFirst
import javax.inject.Inject

class LastFmRepoTrack @Inject constructor(
    appDatabase: AppDatabase,
    @Proxy private val lastFmService: LastFmService,
    private val songGateway: SongGateway
) {

    private val dao = appDatabase.lastFmDao()

    suspend fun shouldFetch(trackId: Long): Boolean {
        return dao.getTrack(trackId) == null
    }

    suspend fun getOriginalItem(trackId: Long): Song? {
        return songGateway.getByParam(trackId).awaitFirst()
    }

    suspend fun get(trackId: Long): LastFmTrack? {
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
        val track = dao.getTrack(trackId)
        return track?.toDomain()
    }

    private suspend fun fetch(track: Song): LastFmTrack {

        val trackId = track.id

        val trackTitle = TextUtils.addSpacesToDash(track.title)
        val trackArtist = if (track.artist == MediaStore.UNKNOWN_STRING) "" else track.artist

        try {
            val trackInfo = lastFmService.getTrackInfoAsync(trackTitle, trackArtist).await().toDomain(trackId)
            return cache(trackInfo).toDomain()
        } catch (ex: Exception) {
            try {
                var trackInfo = lastFmService.searchTrackAsync(trackTitle, trackArtist).await().toDomain(trackId)
                try {
                    trackInfo =
                        lastFmService.getTrackInfoAsync(trackInfo.title, trackInfo.artist).await().toDomain(trackId)
                } catch (ignored: Exception) {
                }
                return cache(trackInfo).toDomain()
            } catch (ex: Exception) {
                return cacheEmpty(trackId).toDomain()
            }
        }
    }

    private suspend fun cache(model: LastFmTrack): LastFmTrackEntity {
        val entity = model.toModel()
        dao.insertTrack(entity)
        return entity
    }

    private suspend fun cacheEmpty(trackId: Long): LastFmTrackEntity {
        val entity = LastFmNulls.createNullTrack(trackId)
        dao.insertTrack(entity)
        return entity
    }

    suspend fun delete(trackId: Long) {
        dao.deleteTrack(trackId)
    }

}