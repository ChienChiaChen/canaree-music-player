package dev.olog.msc.presentation.home.di

import dagger.Subcomponent
import dagger.android.AndroidInjector
import dev.olog.msc.core.dagger.scope.PerActivity
import dev.olog.msc.presentation.home.MainActivity
import dev.olog.msc.pro.ProModule

@Subcomponent(modules = arrayOf(
        MainActivityModule::class,
        MainActivityFragmentsModule::class,
        ProModule::class
//        PlaylistTracksChooserInjector::class,
))
@PerActivity
interface MainActivitySubComponent : AndroidInjector<MainActivity> {

    @Subcomponent.Builder
    abstract class Builder : AndroidInjector.Builder<MainActivity>() {

        abstract fun module(module: MainActivityModule): Builder

        override fun seedInstance(instance: MainActivity) {
            module(MainActivityModule(instance))
        }
    }

}