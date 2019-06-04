package dev.olog.msc.presentation.dialogs.duplicates.di

import androidx.lifecycle.ViewModel
import dagger.Binds
import dagger.Module
import dagger.multibindings.IntoMap
import dev.olog.msc.app.injection.coreComponent
import dev.olog.msc.presentation.dialogs.duplicates.RemoveDuplicatesDialog
import dev.olog.msc.presentation.dialogs.duplicates.RemoveDuplicatesDialogViewModel
import dev.olog.msc.shared.dagger.ViewModelKey

fun RemoveDuplicatesDialog.inject() {
    DaggerRemoveDuplicatesDialogComponent.factory()
        .create(requireActivity().coreComponent())
        .inject(this)
}

@Module
abstract class RemoveDuplicatesDialogModule {

    @Binds
    @IntoMap
    @ViewModelKey(RemoveDuplicatesDialogViewModel::class)
    abstract fun provideViewModel(viewModel: RemoveDuplicatesDialogViewModel): ViewModel

}
