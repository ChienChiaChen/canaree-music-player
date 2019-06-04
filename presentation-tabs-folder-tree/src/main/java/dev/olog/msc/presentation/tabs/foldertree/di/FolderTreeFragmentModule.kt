
package dev.olog.msc.presentation.tabs.foldertree.di

import androidx.lifecycle.ViewModel
import dagger.Binds
import dagger.Module
import dagger.multibindings.IntoMap
import dev.olog.msc.app.injection.coreComponent
import dev.olog.msc.presentation.tabs.foldertree.FolderTreeFragment
import dev.olog.msc.presentation.tabs.foldertree.FolderTreeFragmentViewModel
import dev.olog.msc.shared.dagger.ViewModelKey

fun FolderTreeFragment.inject() {
    DaggerFolderTreeFragmentComponent.factory()
        .create(requireActivity().coreComponent())
        .inject(this)
}

@Module
abstract class FolderTreeFragmentModule {

    @Binds
    @IntoMap
    @ViewModelKey(FolderTreeFragmentViewModel::class)
    internal abstract fun provideViewModel(viewModel: FolderTreeFragmentViewModel): ViewModel

}
