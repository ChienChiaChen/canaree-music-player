package dev.olog.msc.presentation.related.artists.di

import androidx.lifecycle.Lifecycle
import androidx.lifecycle.ViewModel
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.multibindings.IntoMap
import dev.olog.msc.core.MediaId
import dev.olog.msc.core.dagger.qualifier.FragmentLifecycle
import dev.olog.msc.presentation.base.ViewModelKey
import dev.olog.msc.presentation.related.artists.RelatedArtistFragment
import dev.olog.msc.presentation.related.artists.RelatedArtistFragmentViewModel

@Module
class RelatedArtistFragmentModule(
    private val fragment: RelatedArtistFragment
) {

    @Provides
    @FragmentLifecycle
    internal fun lifecycle(): Lifecycle = fragment.lifecycle

    @Provides
    internal fun provideMediaId(): MediaId {
        val mediaId = fragment.arguments!!.getString(RelatedArtistFragment.ARGUMENTS_MEDIA_ID)!!
        return MediaId.fromString(mediaId)
    }

    @Module
    companion object {

        @Binds
        @IntoMap
        @JvmStatic
        @ViewModelKey(RelatedArtistFragmentViewModel::class)
        internal fun provideViewModel(viewModel: RelatedArtistFragmentViewModel): ViewModel {
            return viewModel
        }

    }

}