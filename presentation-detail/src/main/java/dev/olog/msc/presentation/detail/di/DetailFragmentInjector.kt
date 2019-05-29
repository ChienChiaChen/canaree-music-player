

package dev.olog.msc.presentation.detail.di

import androidx.lifecycle.ViewModel
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.multibindings.IntoMap
import dev.olog.msc.app.injection.coreComponent
import dev.olog.msc.core.MediaId
import dev.olog.msc.presentation.detail.DetailFragment
import dev.olog.msc.presentation.detail.DetailFragmentViewModel
import dev.olog.msc.shared.dagger.ViewModelKey

fun DetailFragment.inject() {
    DaggerDetailFragmentComponent.factory()
        .create(this, requireActivity().coreComponent())
        .inject(this)
}

@Module
abstract class DetailFragmentModule {

    @Binds
    @IntoMap
    @ViewModelKey(DetailFragmentViewModel::class)
    internal abstract fun provideViewModel(viewModel: DetailFragmentViewModel): ViewModel

    @Module
    companion object {

        @Provides
        @JvmStatic
        internal fun provideMediaId(fragment: DetailFragment): MediaId {
            val mediaId = fragment.arguments!!.getString(DetailFragment.ARGUMENTS_MEDIA_ID)!!
            return MediaId.fromString(mediaId)
        }
    }

}
