package dev.olog.msc.presentation.home.di

import dev.olog.msc.app.injection.coreComponent
import dev.olog.msc.presentation.home.MainActivity

fun MainActivity.inject() {
    DaggerMainActivityComponent.factory()
        .create(this, coreComponent())
        .inject(this)
}
