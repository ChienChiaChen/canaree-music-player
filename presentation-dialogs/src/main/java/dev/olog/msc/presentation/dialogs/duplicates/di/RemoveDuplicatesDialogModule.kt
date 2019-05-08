package dev.olog.msc.presentation.dialogs.duplicates.di

import dagger.Module
import dagger.Provides
import dev.olog.msc.core.MediaId
import dev.olog.msc.presentation.dialogs.duplicates.RemoveDuplicatesDialog

@Module
class RemoveDuplicatesDialogModule(private val fragment: RemoveDuplicatesDialog) {

    @Provides
    fun provideMediaId(): MediaId {
        val mediaId = fragment.arguments!!.getString(RemoveDuplicatesDialog.ARGUMENTS_MEDIA_ID)!!
        return MediaId.fromString(mediaId)
    }

    @Provides
    fun provideTitle(): String {
        return fragment.arguments!!.getString(RemoveDuplicatesDialog.ARGUMENTS_ITEM_TITLE)!!
    }

}