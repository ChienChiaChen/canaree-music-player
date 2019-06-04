

package dev.olog.msc.presentation.dialogs.delete.di

import androidx.lifecycle.ViewModel
import dagger.Binds
import dagger.Module
import dagger.multibindings.IntoMap
import dev.olog.msc.app.injection.coreComponent
import dev.olog.msc.presentation.dialogs.delete.DeleteDialog
import dev.olog.msc.presentation.dialogs.delete.DeleteDialogViewModel
import dev.olog.msc.shared.dagger.ViewModelKey

@Module
abstract class DeleteDialogModule {

    @Binds
    @IntoMap
    @ViewModelKey(DeleteDialogViewModel::class)
    abstract fun provideViewModel(viewModel: DeleteDialogViewModel): ViewModel
}

fun DeleteDialog.inject() {
    DaggerDeleteDialogComponent.factory()
        .create(requireActivity().coreComponent())
        .inject(this)
}
