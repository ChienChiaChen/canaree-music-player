package dev.olog.msc.data.entity

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(tableName = "favorite_songs",
        indices = [(Index("songId"))]
)
internal data class FavoriteEntity(
        @PrimaryKey var songId: Long
)

@Entity(tableName = "favorite_podcast_songs",
        indices = [(Index("podcastId"))]
)
internal data class FavoritePodcastEntity(
        @PrimaryKey var podcastId: Long
)
