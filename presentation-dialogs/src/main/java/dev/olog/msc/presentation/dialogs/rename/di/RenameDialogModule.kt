package dev.olog.msc.presentation.dialogs.rename.di

import androidx.lifecycle.Lifecycle
import dagger.Module
import dagger.Provides
import dev.olog.msc.core.MediaId
import dev.olog.msc.core.dagger.qualifier.FragmentLifecycle
import dev.olog.msc.presentation.dialogs.rename.RenameDialog


@Module
class RenameDialogModule(
        private val fragment: RenameDialog
) {

    @Provides
    @FragmentLifecycle
    fun provideLifecycle(): Lifecycle = fragment.lifecycle

    @Provides
    fun provideMediaId(): MediaId {
        val mediaId = fragment.arguments!!.getString(RenameDialog.ARGUMENTS_MEDIA_ID)
        return MediaId.fromString(mediaId)
    }

    @Provides
    fun provideTitle() : String {
        return fragment.arguments!!.getString(RenameDialog.ARGUMENTS_ITEM_TITLE)
    }

}