package dev.olog.msc.presentation.detail.di

import dagger.BindsInstance
import dagger.Component
import dev.olog.msc.app.injection.CoreComponent
import dev.olog.msc.app.injection.InjectionHelper
import dev.olog.msc.core.dagger.scope.PerFragment
import dev.olog.msc.presentation.detail.DetailFragment

@Component(
    modules = [DetailFragmentModule::class],
    dependencies = [CoreComponent::class]
)
@PerFragment
interface DetailFragmentComponent : InjectionHelper<DetailFragment> {

    @Component.Factory
    interface Factory {

        fun create(@BindsInstance fragment: DetailFragment, component: CoreComponent): DetailFragmentComponent
    }

}