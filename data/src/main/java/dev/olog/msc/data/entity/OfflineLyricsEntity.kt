package dev.olog.msc.data.entity

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(tableName = "offline_lyrics",
        indices = [(Index("trackId"))]
)
internal data class OfflineLyricsEntity(
        @PrimaryKey var trackId: Long,
        var lyrics: String
)