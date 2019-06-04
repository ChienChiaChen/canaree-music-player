package dev.olog.msc.presentation.popup.di

import dagger.Component
import dev.olog.msc.app.injection.CoreComponent
import dev.olog.msc.app.injection.InjectionHelper
import dev.olog.msc.presentation.popup.PopupFacade
import javax.inject.Scope

@Scope
annotation class PerPopup

@Component(dependencies = [CoreComponent::class])
@PerPopup
interface PopupComponent : InjectionHelper<PopupFacade> {

    @Component.Factory
    interface Factory {
        fun create(component: CoreComponent): PopupComponent
    }

}