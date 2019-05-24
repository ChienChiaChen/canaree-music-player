package dev.olog.msc.presentation.edititem.track.di

import androidx.lifecycle.ViewModel
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.multibindings.IntoMap
import dev.olog.msc.core.MediaId
import dev.olog.msc.core.dagger.qualifier.FragmentLifecycle
import dev.olog.msc.presentation.edititem.track.EditTrackFragment
import dev.olog.msc.presentation.edititem.track.EditTrackFragmentViewModel
import dev.olog.msc.shared.dagger.ViewModelKey

@Module(includes = [EditTrackFragmentModule.Binding::class])
class EditTrackFragmentModule(
        private val fragment: EditTrackFragment

) {

    @Provides
    fun provideMediaId(): MediaId {
        val mediaId = fragment.arguments!!.getString(EditTrackFragment.ARGUMENTS_MEDIA_ID)
        return MediaId.fromString(mediaId)
    }

    @Provides
    @FragmentLifecycle
    fun provideLifecycle() = fragment.lifecycle

    @Module
    interface Binding {

        @Binds
        @IntoMap
        @ViewModelKey(EditTrackFragmentViewModel::class)
        fun provideViewModel(viewModel: EditTrackFragmentViewModel): ViewModel

    }


}