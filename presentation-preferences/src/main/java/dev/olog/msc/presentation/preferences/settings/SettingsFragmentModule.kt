package dev.olog.msc.presentation.preferences.settings

import dev.olog.msc.app.injection.coreComponent

fun SettingsFragment.inject() {
    DaggerSettingsFragmentComponent.factory()
        .create(coreComponent())
        .inject(this)
}