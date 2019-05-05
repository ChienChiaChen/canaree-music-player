package dev.olog.msc.data.entity

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(tableName ="last_played_artists",
        indices = [(Index("id"))]
)
internal data class LastPlayedArtistEntity(
        @PrimaryKey var id: Long,
        var dateAdded: Long = System.currentTimeMillis()
)

@Entity(tableName = "last_played_albums",
        indices = [(Index("id"))]
)
internal data class LastPlayedAlbumEntity(
        @PrimaryKey var id: Long,
        var dateAdded: Long = System.currentTimeMillis()
)

@Entity(tableName = "last_played_podcast_albums",
        indices = [(Index("id"))]
)
internal data class LastPlayedPodcastAlbumEntity(
        @PrimaryKey var id: Long,
        var dateAdded: Long = System.currentTimeMillis()
)

@Entity(tableName = "last_played_podcast_artists",
        indices = [(Index("id"))]
)
internal data class LastPlayedPodcastArtistEntity(
        @PrimaryKey var id: Long,
        var dateAdded: Long = System.currentTimeMillis()
)