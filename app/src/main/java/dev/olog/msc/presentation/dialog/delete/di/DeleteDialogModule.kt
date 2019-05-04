package dev.olog.msc.presentation.dialog.delete.di

import androidx.lifecycle.Lifecycle
import dagger.Module
import dagger.Provides
import dev.olog.msc.core.MediaId
import dev.olog.msc.core.dagger.qualifier.FragmentLifecycle
import dev.olog.msc.presentation.dialog.delete.DeleteDialog

@Module
class DeleteDialogModule(
        private val fragment: DeleteDialog
) {

    @Provides
    @FragmentLifecycle
    fun provideLifecycle(): Lifecycle = fragment.lifecycle

    @Provides
    fun provideMediaId(): MediaId {
        val mediaId = fragment.arguments!!.getString(DeleteDialog.ARGUMENTS_MEDIA_ID)!!
        return MediaId.fromString(mediaId)
    }

    @Provides
    fun provideListSize(): Int {
        return fragment.arguments!!.getInt(DeleteDialog.ARGUMENTS_LIST_SIZE)
    }

    @Provides
    fun provideItemTitle(): String {
        return fragment.arguments!!.getString(DeleteDialog.ARGUMENTS_ITEM_TITLE)!!
    }
}