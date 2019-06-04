package dev.olog.msc.presentation.tabs.foldertree.di

import dagger.Component
import dev.olog.msc.app.injection.CoreComponent
import dev.olog.msc.app.injection.InjectionHelper
import dev.olog.msc.app.injection.viewmodel.ViewModelModule
import dev.olog.msc.core.dagger.scope.PerFragment
import dev.olog.msc.presentation.tabs.foldertree.FolderTreeFragment

@Component(
    modules = [
        ViewModelModule::class,
        FolderTreeFragmentModule::class
    ], dependencies = [CoreComponent::class]
)
@PerFragment
interface FolderTreeFragmentComponent : InjectionHelper<FolderTreeFragment> {

    @Component.Factory
    interface Factory {

        fun create(component: CoreComponent): FolderTreeFragmentComponent
    }

}
