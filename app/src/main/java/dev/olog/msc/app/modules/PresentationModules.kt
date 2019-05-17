package dev.olog.msc.app.modules

import dagger.Module
import dagger.android.ContributesAndroidInjector
import dev.olog.msc.presentation.about.di.AboutActivityInjector
import dev.olog.msc.presentation.create.playlist.di.CreatePlaylistInjector
import dev.olog.msc.presentation.detail.di.DetailFragmentInjector
import dev.olog.msc.presentation.edititem.di.EditItemInjector
import dev.olog.msc.presentation.equalizer.di.EqualizerInjector
import dev.olog.msc.presentation.home.di.MainActivityInjector
import dev.olog.msc.presentation.offlinelyrics.di.OfflineLyricsInjector
import dev.olog.msc.presentation.player.di.PlayerFragmentModule
import dev.olog.msc.presentation.playing.queue.di.PlayingQueueFragmentInjector
import dev.olog.msc.presentation.preferences.di.PreferencesActivityInjector
import dev.olog.msc.presentation.recently.added.di.RecentlyAddedFragmentInjector
import dev.olog.msc.presentation.related.artists.di.RelatedArtistFragmentInjector
import dev.olog.msc.presentation.search.di.SearchFragmentInjector
import dev.olog.msc.presentation.shortcuts.playlist.chooser.di.PlaylistChooserActivityModule
import dev.olog.msc.presentation.splash.SplashActivity
import dev.olog.msc.presentation.tabs.foldertree.di.FolderTreeFragmentModule

@Module(
    includes = [
        MainActivityInjector::class,
        AboutActivityInjector::class,
        PreferencesActivityInjector::class,
        PlaylistChooserActivityModule::class,

        CreatePlaylistInjector::class,
        FolderTreeFragmentModule::class,
        DetailFragmentInjector::class,
        PlayerFragmentModule::class,
        SearchFragmentInjector::class,
        EditItemInjector::class,
        PlayingQueueFragmentInjector::class,
        RecentlyAddedFragmentInjector::class,
        RelatedArtistFragmentInjector::class,
        EqualizerInjector::class,
        OfflineLyricsInjector::class
    ]
)
abstract class PresentationModules {

    @ContributesAndroidInjector
    abstract fun provideSplashActivity(): SplashActivity

}