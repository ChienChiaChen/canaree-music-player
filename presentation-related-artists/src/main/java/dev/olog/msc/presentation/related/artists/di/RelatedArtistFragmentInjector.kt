package dev.olog.msc.presentation.related.artists.di

import androidx.lifecycle.ViewModel
import dagger.*
import dagger.android.AndroidInjector
import dagger.multibindings.ClassKey
import dagger.multibindings.IntoMap
import dev.olog.msc.core.MediaId
import dev.olog.msc.core.dagger.scope.PerFragment
import dev.olog.msc.presentation.related.artists.RelatedArtistFragment
import dev.olog.msc.presentation.related.artists.RelatedArtistFragmentViewModel
import dev.olog.msc.shared.dagger.ViewModelKey

@Module(subcomponents = [RelatedArtistFragmentSubComponent::class])
abstract class RelatedArtistFragmentInjector {

    @Binds
    @IntoMap
    @ClassKey(RelatedArtistFragment::class)
    internal abstract fun injectorFactory(builder: RelatedArtistFragmentSubComponent.Factory): AndroidInjector.Factory<*>

}

@Module
class RelatedArtistFragmentModule {
    @Provides
    @IntoMap
    @ViewModelKey(RelatedArtistFragmentViewModel::class)
    internal fun provideViewModel(viewModel: RelatedArtistFragmentViewModel): ViewModel {
        return viewModel
    }

    @Provides
    internal fun provideMediaId(fragment: RelatedArtistFragment): MediaId {
        val mediaId = fragment.arguments!!.getString(RelatedArtistFragment.ARGUMENTS_MEDIA_ID)!!
        return MediaId.fromString(mediaId)
    }
}

@Subcomponent(modules = [RelatedArtistFragmentModule::class])
@PerFragment
interface RelatedArtistFragmentSubComponent : AndroidInjector<RelatedArtistFragment> {

    @Subcomponent.Factory
    interface Factory : AndroidInjector.Factory<RelatedArtistFragment> {
        override fun create(@BindsInstance instance: RelatedArtistFragment): RelatedArtistFragmentSubComponent
    }

}