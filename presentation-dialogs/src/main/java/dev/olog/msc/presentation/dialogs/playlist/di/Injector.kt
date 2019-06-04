package dev.olog.msc.presentation.dialogs.playlist.di

import dev.olog.msc.app.injection.coreComponent
import dev.olog.msc.presentation.dialogs.playlist.ClearPlaylistDialog
import dev.olog.msc.presentation.dialogs.playlist.NewPlaylistDialog

fun ClearPlaylistDialog.inject() {
    DaggerClearPlaylistDialogComponent.factory()
        .create(requireActivity().coreComponent())
        .inject(this)
}

fun NewPlaylistDialog.inject() {
    DaggerNewPlaylistDialogComponent.factory()
        .create(requireActivity().coreComponent())
        .inject(this)
}