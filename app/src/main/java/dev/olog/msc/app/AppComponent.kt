package dev.olog.msc.app

import dagger.Component
import dagger.android.AndroidInjectionModule
import dagger.android.AndroidInjector
import dev.olog.msc.NavigatorModule
import dev.olog.msc.apilastfm.LastFmModule
import dev.olog.msc.appshortcuts.AppShortcutsModule
import dev.olog.msc.appwidgets.di.WidgetBindingModule
import dev.olog.msc.data.di.PreferenceModule
import dev.olog.msc.data.di.RepositoryHelperModule
import dev.olog.msc.data.di.RepositoryModule
import dev.olog.msc.floatingwindowservice.di.FloatingWindowServiceInjector
import dev.olog.msc.imagecreation.domain.ImageCreationInjector
import dev.olog.msc.musicservice.di.EqualizerModule
import dev.olog.msc.musicservice.di.MusicServiceInjector
import dev.olog.msc.presentation.ViewModelModule
import dev.olog.msc.presentation.about.di.AboutActivityInjector
import dev.olog.msc.presentation.detail.di.DetailFragmentInjector
import dev.olog.msc.presentation.edititem.di.EditItemInjector
import dev.olog.msc.presentation.equalizer.di.EqualizerInjector
import dev.olog.msc.presentation.home.di.MainActivityInjector
import dev.olog.msc.presentation.offlinelyrics.di.OfflineLyricsInjector
import dev.olog.msc.presentation.player.di.PlayerFragmentModule
import dev.olog.msc.presentation.playing.queue.di.PlayingQueueFragmentInjector
import dev.olog.msc.presentation.preferences.di.PreferencesActivityInjector
import dev.olog.msc.presentation.recently.added.di.RecentlyAddedFragmentInjector
import dev.olog.msc.presentation.search.di.SearchFragmentInjector
import dev.olog.msc.presentation.shortcuts.playlist.chooser.di.PlaylistChooserActivityInjector
import dev.olog.msc.presentation.sleeptimer.di.SleepTimerInjector
import dev.olog.msc.presentation.tabs.di.TabFragmentInjector
import dev.olog.msc.presentation.tabs.foldertree.di.FolderTreeFragmentModule
import javax.inject.Singleton

@Component(modules = arrayOf(
        AppModule::class,
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
        NavigatorModule::class,
        ActivityBindingsModule::class,
        EqualizerInjector::class,
        OfflineLyricsInjector::class,
        SleepTimerInjector::class,
        WidgetBindingModule::class,
        MainActivityInjector::class,
        AboutActivityInjector::class,
        PreferencesActivityInjector::class,
        PlaylistChooserActivityInjector::class,
        ViewModelModule::class,

        TabFragmentInjector::class,
        FolderTreeFragmentModule::class,
        DetailFragmentInjector::class,
        PlayerFragmentModule::class,
        SearchFragmentInjector::class,
        EditItemInjector::class,
        PlayingQueueFragmentInjector::class,
        RecentlyAddedFragmentInjector::class,

        ImageCreationInjector::class,

//        // music service
        MusicServiceInjector::class,
        EqualizerModule::class,

//        // floating info service
        FloatingWindowServiceInjector::class
))
@Singleton
interface AppComponent: AndroidInjector<App> {

    @Component.Builder
    abstract class Builder : AndroidInjector.Builder<App>() {

        internal abstract fun module(module: AppModule): Builder

        override fun seedInstance(instance: App) {
            module(AppModule(instance))
        }

    }

}