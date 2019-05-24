package dev.olog.msc.presentation.dialogs.delete.di

import androidx.lifecycle.ViewModel
import dagger.Binds
import dagger.Module
import dagger.android.ContributesAndroidInjector
import dagger.multibindings.IntoMap
import dev.olog.msc.presentation.dialogs.delete.DeleteDialog
import dev.olog.msc.presentation.dialogs.delete.DeleteDialogViewModel
import dev.olog.msc.shared.dagger.ViewModelKey

@Module
abstract class DeleteDialogModule {

    @ContributesAndroidInjector
    abstract fun provideFragment(): DeleteDialog

    @Binds
    @IntoMap
    @ViewModelKey(DeleteDialogViewModel::class)
    abstract fun provideViewModel(viewModel: DeleteDialogViewModel): ViewModel
}