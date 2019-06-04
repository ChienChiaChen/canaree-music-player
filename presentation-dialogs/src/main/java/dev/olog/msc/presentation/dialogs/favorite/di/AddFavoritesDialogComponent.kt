package dev.olog.msc.presentation.dialogs.favorite.di

import dagger.Component
import dev.olog.msc.app.injection.CoreComponent
import dev.olog.msc.app.injection.InjectionHelper
import dev.olog.msc.app.injection.viewmodel.ViewModelModule
import dev.olog.msc.core.dagger.scope.PerFragment
import dev.olog.msc.presentation.dialogs.favorite.AddFavoriteDialog

@Component(
    modules = [
        ViewModelModule::class,
        AddFavoriteDialogModule::class
    ], dependencies = [CoreComponent::class]
)
@PerFragment
interface AddFavoritesDialogComponent : InjectionHelper<AddFavoriteDialog> {

    @Component.Factory
    interface Factory {

        fun create(component: CoreComponent): AddFavoritesDialogComponent
    }

}