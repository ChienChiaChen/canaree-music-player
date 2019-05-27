package dev.olog.msc.presentation.detail.di

import androidx.lifecycle.ViewModel
import dagger.*
import dagger.android.AndroidInjector
import dagger.multibindings.ClassKey
import dagger.multibindings.IntoMap
import dev.olog.msc.core.MediaId
import dev.olog.msc.core.dagger.scope.PerFragment
import dev.olog.msc.presentation.detail.DetailFragment
import dev.olog.msc.presentation.detail.DetailFragmentViewModel
import dev.olog.msc.shared.dagger.ViewModelKey

@Module(subcomponents = [DetailFragmentSubComponent::class])
abstract class DetailFragmentInjector {

    @Binds
    @IntoMap
    @ClassKey(DetailFragment::class)
    internal abstract fun injectorFactory(builder: DetailFragmentSubComponent.Factory): AndroidInjector.Factory<*>

}

@Module
class DetailFragmentModule {

    @Provides
    @IntoMap
    @ViewModelKey(DetailFragmentViewModel::class)
    internal fun provideViewModel(viewModel: DetailFragmentViewModel): ViewModel {
        return viewModel
    }

    @Provides
    internal fun provideMediaId(fragment: DetailFragment): MediaId {
        val mediaId = fragment.arguments!!.getString(DetailFragment.ARGUMENTS_MEDIA_ID)!!
        return MediaId.fromString(mediaId)
    }

}

@Subcomponent(modules = [DetailFragmentModule::class])
@PerFragment
interface DetailFragmentSubComponent : AndroidInjector<DetailFragment> {

    @Subcomponent.Factory
    interface Factory : AndroidInjector.Factory<DetailFragment> {
        override fun create(@BindsInstance instance: DetailFragment): DetailFragmentSubComponent
    }

}
