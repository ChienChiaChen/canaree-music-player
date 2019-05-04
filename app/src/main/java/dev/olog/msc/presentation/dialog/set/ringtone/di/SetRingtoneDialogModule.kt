package dev.olog.msc.presentation.dialog.set.ringtone.di

import androidx.lifecycle.Lifecycle
import dagger.Module
import dagger.Provides
import dev.olog.msc.core.MediaId
import dev.olog.msc.core.dagger.qualifier.FragmentLifecycle
import dev.olog.msc.presentation.dialog.set.ringtone.SetRingtoneDialog

@Module
class SetRingtoneDialogModule(
        private val fragment: SetRingtoneDialog
) {

    @Provides
    @FragmentLifecycle
    fun provideLifecycle(): Lifecycle = fragment.lifecycle

    @Provides
    fun provideMediaId(): MediaId {
        val mediaId = fragment.arguments!!.getString(SetRingtoneDialog.ARGUMENTS_MEDIA_ID)
        return MediaId.fromString(mediaId)
    }

}