package dev.olog.msc.app

import dagger.BindsInstance
import dagger.Component
import dagger.android.AndroidInjectionModule
import dagger.android.AndroidInjector
import dev.olog.msc.NavigatorModule
import dev.olog.msc.apilastfm.LastFmModule
import dev.olog.msc.app.modules.DialogModules
import dev.olog.msc.app.modules.PresentationModules
import dev.olog.msc.appshortcuts.AppShortcutsModule
import dev.olog.msc.appwidgets.di.WidgetBindingModule
import dev.olog.msc.data.di.PreferenceModule
import dev.olog.msc.data.di.RepositoryHelperModule
import dev.olog.msc.data.di.RepositoryModule
import dev.olog.msc.floatingwindowservice.di.FloatingWindowServiceInjector
import dev.olog.msc.musicservice.di.EqualizerModule
import dev.olog.msc.musicservice.di.MusicServiceInjector
import dev.olog.msc.presentation.ViewModelModule
import dev.olog.msc.presentation.sleeptimer.di.SleepTimerInjector
import javax.inject.Singleton

@Component(
    modules = arrayOf(
        AppModule::class,
        AppModule2::class,
        SchedulersModule::class,
        AppShortcutsModule::class,
        LastFmModule::class,
        AndroidInjectionModule::class,

//        // data
        RepositoryModule::class,
        RepositoryHelperModule::class,
        PreferenceModule::class,
//
//        // presentation
        SleepTimerInjector::class,
        DialogModules::class,
        PresentationModules::class,
        NavigatorModule::class,
        WidgetBindingModule::class,
        ViewModelModule::class,

//        // music service
        MusicServiceInjector::class,
        EqualizerModule::class,

//        // floating info service
        FloatingWindowServiceInjector::class
    )
)
@Singleton
interface AppComponent : AndroidInjector<App> {

    @Component.Factory
    interface Factory : AndroidInjector.Factory<App> {

        override fun create(@BindsInstance instance: App): AppComponent
    }

}