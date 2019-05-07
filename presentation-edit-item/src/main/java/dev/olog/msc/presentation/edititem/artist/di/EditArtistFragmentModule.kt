package dev.olog.msc.presentation.edititem.artist.di

import androidx.lifecycle.ViewModel
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.multibindings.IntoMap
import dev.olog.msc.core.MediaId
import dev.olog.msc.presentation.edititem.artist.EditArtistFragment
import dev.olog.msc.presentation.edititem.artist.EditArtistFragmentViewModel
import dev.olog.presentation.base.ViewModelKey

@Module(includes = [EditArtistFragmentModule.Binding::class])
class EditArtistFragmentModule(private val fragment: EditArtistFragment) {

    @Provides
    fun provideMediaId(): MediaId {
        val mediaId = fragment.arguments!!.getString(EditArtistFragment.ARGUMENTS_MEDIA_ID)
        return MediaId.fromString(mediaId)
    }

    @Module
    interface Binding {

        @Binds
        @IntoMap
        @ViewModelKey(EditArtistFragmentViewModel::class)
        fun provideViewModel(viewModel: EditArtistFragmentViewModel): ViewModel

    }

}