package dev.olog.msc.presentation.about.di

import dagger.BindsInstance
import dagger.Component
import dev.olog.msc.app.injection.CoreComponent
import dev.olog.msc.app.injection.InjectionHelper
import dev.olog.msc.core.dagger.scope.PerActivity
import dev.olog.msc.presentation.about.AboutActivity
import dev.olog.msc.pro.ProModule

@Component(
    modules = [
        ProModule::class,
        AboutActivityModule::class
    ], dependencies = [CoreComponent::class]
)
@PerActivity
interface AboutActivityComponent : InjectionHelper<AboutActivity> {

    @Component.Factory
    interface Factory {

        fun create(
            @BindsInstance instance: AboutActivity,
            component: CoreComponent
        ): AboutActivityComponent
    }

}
