
package dev.olog.msc.presentation.recently.added.di

import androidx.lifecycle.ViewModel
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.multibindings.IntoMap
import dev.olog.msc.app.injection.coreComponent
import dev.olog.msc.core.MediaId
import dev.olog.msc.presentation.navigator.Fragments
import dev.olog.msc.presentation.recently.added.RecentlyAddedFragment
import dev.olog.msc.presentation.recently.added.RecentlyAddedFragmentViewModel
import dev.olog.msc.shared.dagger.ViewModelKey

fun RecentlyAddedFragment.inject() {
    DaggerRecentlyAddedFragmentComponent.factory()
        .create(this, requireActivity().coreComponent())
        .inject(this)
}

@Module
abstract class RecentlyAddedFragmentModule {

    @Binds
    @IntoMap
    @ViewModelKey(RecentlyAddedFragmentViewModel::class)
    internal abstract fun provideViewModel(viewModel: RecentlyAddedFragmentViewModel): ViewModel

    @Module
    companion object {
        @Provides
        @JvmStatic
        internal fun provideMediaId(fragment: RecentlyAddedFragment): MediaId {
            val mediaId = fragment.arguments!!.getString(Fragments.ARGUMENTS_MEDIA_ID)!!
            return MediaId.fromString(mediaId)
        }
    }
}
