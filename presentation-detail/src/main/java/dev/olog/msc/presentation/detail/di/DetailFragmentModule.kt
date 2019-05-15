package dev.olog.msc.presentation.detail.di

import androidx.lifecycle.ViewModel
import dagger.Module
import dagger.Provides
import dagger.multibindings.IntoMap
import dev.olog.msc.core.MediaId
import dev.olog.msc.core.dagger.qualifier.FragmentLifecycle
import dev.olog.msc.presentation.base.ViewModelKey
import dev.olog.msc.presentation.detail.DetailFragment
import dev.olog.msc.presentation.detail.DetailFragmentViewModel

@Module
class DetailFragmentModule(private val fragment: DetailFragment) {

    @Provides
    internal fun provideMediaId(): MediaId {
        val mediaId = fragment.arguments!!.getString(DetailFragment.ARGUMENTS_MEDIA_ID)
        return MediaId.fromString(mediaId)
    }

    @Provides
    @FragmentLifecycle
    internal fun provideLifecycle() = fragment.lifecycle

    @Module
    companion object {

        @Provides
        @JvmStatic
        @IntoMap
        @ViewModelKey(DetailFragmentViewModel::class)
        internal fun provideViewModel(viewModel: DetailFragmentViewModel): ViewModel {
            return viewModel
        }
    }


}