package dev.olog.msc.presentation.home.di

import dagger.BindsInstance
import dagger.Component
import dev.olog.msc.app.injection.CoreComponent
import dev.olog.msc.app.injection.InjectionHelper
import dev.olog.msc.core.dagger.scope.PerActivity
import dev.olog.msc.presentation.home.MainActivity
import dev.olog.msc.pro.ProModule

@Component(
    modules = [
        ProModule::class,
        MainActivityModule::class
    ], dependencies = [CoreComponent::class]
)
@PerActivity
interface MainActivityComponent : InjectionHelper<MainActivity> {

    @Component.Factory
    interface Factory {

        fun create(
            @BindsInstance instance: MainActivity,
            component: CoreComponent
        ): MainActivityComponent
    }

}