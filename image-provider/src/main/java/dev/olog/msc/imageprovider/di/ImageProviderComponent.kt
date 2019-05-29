package dev.olog.msc.imageprovider.di

import dagger.Component
import dev.olog.msc.app.injection.AppComponent
import dev.olog.msc.app.injection.InjectionHelper
import dev.olog.msc.imageprovider.glide.GlideModule

@Component(dependencies = [AppComponent::class])
@PerImageProvider
interface ImageProviderComponent : InjectionHelper<GlideModule> {

    @Component.Factory
    interface Factory {

        fun create(component: AppComponent): ImageProviderComponent

    }

}