package dev.olog.msc.presentation.tabs.foldertree.di

import androidx.lifecycle.ViewModel
import dagger.Binds
import dagger.Module
import dagger.android.ContributesAndroidInjector
import dagger.multibindings.IntoMap
import dev.olog.msc.presentation.tabs.foldertree.FolderTreeFragment
import dev.olog.msc.presentation.tabs.foldertree.FolderTreeFragmentViewModel
import dev.olog.presentation.base.ViewModelKey

@Module
abstract class FolderTreeFragmentModule {

    @ContributesAndroidInjector
    internal abstract fun provideFolderTreeFragment(): FolderTreeFragment

    @Binds
    @IntoMap
    @ViewModelKey(FolderTreeFragmentViewModel::class)
    internal abstract fun provideViewModel(viewModel: FolderTreeFragmentViewModel): ViewModel

}