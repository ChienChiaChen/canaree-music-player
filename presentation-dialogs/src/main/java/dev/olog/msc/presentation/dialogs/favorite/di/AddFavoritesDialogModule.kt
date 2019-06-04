package dev.olog.msc.presentation.dialogs.favorite.di

import androidx.lifecycle.ViewModel
import dagger.Binds
import dagger.Module
import dagger.multibindings.IntoMap
import dev.olog.msc.app.injection.coreComponent
import dev.olog.msc.presentation.dialogs.favorite.AddFavoriteDialog
import dev.olog.msc.presentation.dialogs.favorite.AddFavoriteDialogViewModel
import dev.olog.msc.shared.dagger.ViewModelKey

@Module
abstract class AddFavoriteDialogModule {

    @Binds
    @IntoMap
    @ViewModelKey(AddFavoriteDialogViewModel::class)
    abstract fun provideViewModel(viewModel: AddFavoriteDialogViewModel): ViewModel

}


fun AddFavoriteDialog.inject() {
    DaggerAddFavoritesDialogComponent.factory()
        .create(requireActivity().coreComponent())
        .inject(this)
}
