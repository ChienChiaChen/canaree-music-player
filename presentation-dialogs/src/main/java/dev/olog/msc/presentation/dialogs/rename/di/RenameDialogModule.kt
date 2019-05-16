package dev.olog.msc.presentation.dialogs.rename.di

import androidx.lifecycle.ViewModel
import dagger.Binds
import dagger.Module
import dagger.android.ContributesAndroidInjector
import dagger.multibindings.IntoMap
import dev.olog.msc.presentation.base.ViewModelKey
import dev.olog.msc.presentation.dialogs.rename.RenameDialog
import dev.olog.msc.presentation.dialogs.rename.RenameDialogViewModel


@Module
abstract class RenameDialogModule {

    @ContributesAndroidInjector
    abstract fun provideFragment(): RenameDialog

    @Binds
    @IntoMap
    @ViewModelKey(RenameDialogViewModel::class)
    abstract fun provideViewModel(viewModel: RenameDialogViewModel): ViewModel

}