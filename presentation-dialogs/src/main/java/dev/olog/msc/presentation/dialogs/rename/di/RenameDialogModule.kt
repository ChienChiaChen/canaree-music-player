package dev.olog.msc.presentation.dialogs.rename.di

import androidx.lifecycle.ViewModel
import dagger.Binds
import dagger.Module
import dagger.multibindings.IntoMap
import dev.olog.msc.app.injection.coreComponent
import dev.olog.msc.presentation.dialogs.rename.RenameDialog
import dev.olog.msc.presentation.dialogs.rename.RenameDialogViewModel
import dev.olog.msc.shared.dagger.ViewModelKey

fun RenameDialog.inject() {
    DaggerRenameDialogComponent.factory()
        .create(requireActivity().coreComponent())
        .inject(this)
}

@Module
abstract class RenameDialogModule {

    @Binds
    @IntoMap
    @ViewModelKey(RenameDialogViewModel::class)
    abstract fun provideViewModel(viewModel: RenameDialogViewModel): ViewModel

}
