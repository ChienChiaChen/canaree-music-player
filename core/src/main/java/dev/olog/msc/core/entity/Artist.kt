package dev.olog.msc.core.entity

data class Artist (
        val id: Long,
        val name: String,
        val albumArtist: String,
        val songs: Int,
        val albums: Int
)