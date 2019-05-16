package dev.olog.msc.core.gateway

import dev.olog.msc.core.entity.OfflineLyrics
import kotlinx.coroutines.flow.Flow

interface OfflineLyricsGateway {

    fun observeLyrics(id: Long): Flow<String>
    suspend fun saveLyrics(offlineLyrics: OfflineLyrics)

}