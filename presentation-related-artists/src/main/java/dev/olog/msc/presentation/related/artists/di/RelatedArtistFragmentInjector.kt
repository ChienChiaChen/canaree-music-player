package dev.olog.msc.presentation.related.artists.di

import androidx.lifecycle.ViewModel
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.multibindings.IntoMap
import dev.olog.msc.app.injection.coreComponent
import dev.olog.msc.core.MediaId
import dev.olog.msc.presentation.related.artists.RelatedArtistFragment
import dev.olog.msc.presentation.related.artists.RelatedArtistFragmentViewModel
import dev.olog.msc.shared.dagger.ViewModelKey

fun RelatedArtistFragment.inject() {
    DaggerRelatedArtistsFragmentComponent.factory()
        .create(this, requireActivity().coreComponent())
        .inject(this)
}

@Module
abstract class RelatedArtistFragmentModule {

    @Binds
    @IntoMap
    @ViewModelKey(RelatedArtistFragmentViewModel::class)
    internal abstract fun provideViewModel(viewModel: RelatedArtistFragmentViewModel): ViewModel

    @Module
    companion object {
        @Provides
        @JvmStatic
        internal fun provideMediaId(fragment: RelatedArtistFragment): MediaId {
            val mediaId = fragment.arguments!!.getString(RelatedArtistFragment.ARGUMENTS_MEDIA_ID)!!
            return MediaId.fromString(mediaId)
        }
    }
}


