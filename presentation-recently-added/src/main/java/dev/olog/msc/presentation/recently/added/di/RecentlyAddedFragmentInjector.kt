package dev.olog.msc.presentation.recently.added.di

import androidx.lifecycle.ViewModel
import dagger.*
import dagger.android.AndroidInjector
import dagger.multibindings.ClassKey
import dagger.multibindings.IntoMap
import dev.olog.msc.core.MediaId
import dev.olog.msc.core.dagger.scope.PerFragment
import dev.olog.msc.presentation.recently.added.RecentlyAddedFragment
import dev.olog.msc.presentation.recently.added.RecentlyAddedFragmentViewModel
import dev.olog.msc.shared.dagger.ViewModelKey

@Module(subcomponents = [RecentlyAddedFragmentSubComponent::class])
abstract class RecentlyAddedFragmentInjector {

    @Binds
    @IntoMap
    @ClassKey(RecentlyAddedFragment::class)
    internal abstract fun injectorFactory(builder: RecentlyAddedFragmentSubComponent.Factory): AndroidInjector.Factory<*>

}

@Module
class RecentlyAddedFragmentModule {

    @Provides
    @IntoMap
    @ViewModelKey(RecentlyAddedFragmentViewModel::class)
    internal fun provideViewModel(viewModel: RecentlyAddedFragmentViewModel): ViewModel{
        return viewModel
    }

    @Provides
    internal fun provideMediaId(fragment: RecentlyAddedFragment): MediaId {
        val mediaId = fragment.arguments!!.getString(RecentlyAddedFragment.ARGUMENTS_MEDIA_ID)!!
        return MediaId.fromString(mediaId)
    }
}


@Subcomponent(modules = [RecentlyAddedFragmentModule::class])
@PerFragment
interface RecentlyAddedFragmentSubComponent : AndroidInjector<RecentlyAddedFragment> {

    @Subcomponent.Factory
    interface Factory : AndroidInjector.Factory<RecentlyAddedFragment> {
        override fun create(@BindsInstance instance: RecentlyAddedFragment): RecentlyAddedFragmentSubComponent
    }

}