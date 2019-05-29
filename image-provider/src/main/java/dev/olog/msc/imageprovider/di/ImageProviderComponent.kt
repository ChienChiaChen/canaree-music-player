package dev.olog.msc.imageprovider.di

import dagger.Component
import dev.olog.msc.app.injection.CoreComponent
import dev.olog.msc.app.injection.InjectionHelper
import dev.olog.msc.imageprovider.glide.GlideModule

@Component(dependencies = [CoreComponent::class])
@PerImageProvider
interface ImageProviderComponent : InjectionHelper<GlideModule> {

    @Component.Factory
    interface Factory {

        fun create(component: CoreComponent): ImageProviderComponent

    }

}