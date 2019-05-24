package dev.olog.msc.app.modules

import dagger.Module
import dev.olog.msc.presentation.dialogs.delete.di.DeleteDialogModule
import dev.olog.msc.presentation.dialogs.duplicates.di.RemoveDuplicatesDialogModule
import dev.olog.msc.presentation.dialogs.favorite.di.AddFavoriteDialogModule
import dev.olog.msc.presentation.dialogs.play.later.di.PlayLaterDialogModule
import dev.olog.msc.presentation.dialogs.playlist.di.ClearPlaylistDialogModule
import dev.olog.msc.presentation.dialogs.playlist.di.NewPlaylistDialogModule
import dev.olog.msc.presentation.dialogs.rename.di.RenameDialogModule
import dev.olog.msc.presentation.dialogs.ringtone.di.SetRingtoneDialogModule

@Module(
        includes = [
            AddFavoriteDialogModule::class,
            PlayLaterDialogModule::class,
            PlayLaterDialogModule::class,
            SetRingtoneDialogModule::class,
            RenameDialogModule::class,
            ClearPlaylistDialogModule::class,
            DeleteDialogModule::class,
            NewPlaylistDialogModule::class,
            RemoveDuplicatesDialogModule::class
        ]
)
abstract class DialogModules