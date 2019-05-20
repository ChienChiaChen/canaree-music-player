package dev.olog.msc.presentation.related.artists.di

import androidx.lifecycle.ViewModel
import dagger.Module
import dagger.Provides
import dagger.multibindings.IntoMap
import dev.olog.msc.core.MediaId
import dev.olog.msc.presentation.base.ViewModelKey
import dev.olog.msc.presentation.related.artists.RelatedArtistFragment
import dev.olog.msc.presentation.related.artists.RelatedArtistFragmentViewModel

@Module
class RelatedArtistFragmentModule(
    private val fragment: RelatedArtistFragment
) {

    @Provides
    internal fun provideMediaId(): MediaId {
        val mediaId = fragment.arguments!!.getString(RelatedArtistFragment.ARGUMENTS_MEDIA_ID)!!
        return MediaId.fromString(mediaId)
    }

    @Module
    companion object {

        @Provides
        @IntoMap
        @JvmStatic
        @ViewModelKey(RelatedArtistFragmentViewModel::class)
        internal fun provideViewModel(viewModel: RelatedArtistFragmentViewModel): ViewModel {
            return viewModel
        }

    }

}