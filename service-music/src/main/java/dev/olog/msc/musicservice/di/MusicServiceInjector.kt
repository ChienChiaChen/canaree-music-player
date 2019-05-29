package dev.olog.msc.musicservice.di

import dev.olog.msc.app.injection.coreComponent
import dev.olog.msc.musicservice.MusicService

fun MusicService.inject() {
    DaggerMusicServiceComponent.factory()
        .create(this, coreComponent())
        .inject(this)
}