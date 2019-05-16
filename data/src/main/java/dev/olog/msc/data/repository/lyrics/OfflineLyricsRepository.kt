package dev.olog.msc.data.repository.lyrics

import dev.olog.msc.core.entity.OfflineLyrics
import dev.olog.msc.core.gateway.OfflineLyricsGateway
import dev.olog.msc.data.db.AppDatabase
import dev.olog.msc.data.entity.OfflineLyricsEntity
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.reactive.flow.asFlow
import javax.inject.Inject

internal class OfflineLyricsRepository @Inject constructor(
    appDatabase: AppDatabase

) : OfflineLyricsGateway {

    private val dao = appDatabase.offlineLyricsDao()

    override fun observeLyrics(id: Long): Flow<String> {
        return dao.observeLyrics(id)
            .asFlow()
            .map {
                if (it.isEmpty()) ""
                else it[0].lyrics
            }
    }

    override suspend fun saveLyrics(offlineLyrics: OfflineLyrics) {
        dao.saveLyrics(OfflineLyricsEntity(offlineLyrics.trackId, offlineLyrics.lyrics))

    }
}