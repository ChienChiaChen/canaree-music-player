package dev.olog.msc.presentation.detail.di

import androidx.lifecycle.ViewModel
import dagger.Module
import dagger.Provides
import dagger.multibindings.IntoMap
import dev.olog.msc.core.MediaId
import dev.olog.msc.presentation.detail.DetailFragment
import dev.olog.msc.presentation.detail.DetailFragmentViewModel
import dev.olog.msc.shared.dagger.ViewModelKey

@Module
class DetailFragmentModule(private val fragment: DetailFragment) {

    @Provides
    internal fun provideMediaId(): MediaId {
        val mediaId = fragment.arguments!!.getString(DetailFragment.ARGUMENTS_MEDIA_ID)
        return MediaId.fromString(mediaId)
    }

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