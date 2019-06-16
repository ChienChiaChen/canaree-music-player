package dev.olog.msc.app

import android.app.Activity
import android.app.Application
import android.app.Service
import android.appwidget.AppWidgetProvider
import androidx.fragment.app.Fragment
import dagger.BindsInstance
import dagger.Component
import dagger.android.AndroidInjectionModule
import dev.olog.msc.app.shortcuts.AppShortcutsModule
import dev.olog.msc.data.RepositoryHelperModule
import dev.olog.msc.data.RepositoryModule
import dev.olog.msc.data.api.last.fm.LastFmModule
import dev.olog.msc.data.prefs.PreferenceModule
import dev.olog.msc.floating.window.service.di.FloatingWindowServiceInjector
import dev.olog.msc.glide.GlideModule
import dev.olog.msc.music.service.di.MusicServiceInjector
import dev.olog.msc.presentation.ViewModelModule
import dev.olog.msc.presentation.about.di.AboutActivityInjector
import dev.olog.msc.presentation.app.widget.WidgetBindingModule
import dev.olog.msc.presentation.main.di.MainActivityInjector
import dev.olog.msc.presentation.preferences.di.PreferencesActivityInjector
import dev.olog.msc.presentation.shortcuts.playlist.chooser.di.PlaylistChooserActivityInjector
import javax.inject.Singleton

@Component(
    modules = arrayOf(
        CoreModule::class,
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
        ActivityBindingsModule::class,
        WidgetBindingModule::class,
        MainActivityInjector::class,
        AboutActivityInjector::class,
        PreferencesActivityInjector::class,
        PlaylistChooserActivityInjector::class,
        ViewModelModule::class,

//        // music service
        MusicServiceInjector::class,
        EqualizerModule::class,

//        // floating info service
        FloatingWindowServiceInjector::class
    )
)
@Singleton
interface CoreComponent {

    fun inject(instance: App)
    fun inject(instance: GlideModule)

    @Component.Factory
    interface Factory {

        fun create(@BindsInstance instance: Application): CoreComponent

    }

    companion object {
        private var component: CoreComponent? = null

        fun component(app: Application): CoreComponent {
            if (component == null) {
                // not double checking because it will be created in App.kt on main thread at app startup
                component = DaggerCoreComponent.factory().create(app)
            }
            return component!!
        }

        fun unsafeComponent(): CoreComponent = component!!
    }
}

fun Activity.coreComponent(): CoreComponent = CoreComponent.unsafeComponent()
fun Service.coreComponent(): CoreComponent = CoreComponent.unsafeComponent()
fun Fragment.coreComponent(): CoreComponent = CoreComponent.unsafeComponent()
fun AppWidgetProvider.coreComponent(): CoreComponent = CoreComponent.unsafeComponent()