package dev.olog.msc.musicservice.model

internal data class PlayerMediaEntity(
        val mediaEntity: MediaEntity,
        val positionInQueue: PositionInQueue,
        val bookmark: Long
)