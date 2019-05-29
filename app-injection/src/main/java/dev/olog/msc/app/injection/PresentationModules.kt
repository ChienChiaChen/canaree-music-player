package dev.olog.msc.app.injection

import dagger.Module
import dev.olog.msc.presentation.about.di.AboutActivityInjector
import dev.olog.msc.presentation.categories.di.CategoriesFragmentsModule
import dev.olog.msc.presentation.create.playlist.di.CreatePlaylistModule
import dev.olog.msc.presentation.detail.di.DetailFragmentInjector
import dev.olog.msc.presentation.edititem.di.EditItemInjector
import dev.olog.msc.presentation.equalizer.di.EqualizerModule
import dev.olog.msc.presentation.home.di.MainActivityInjector
import dev.olog.msc.presentation.offlinelyrics.di.OfflineLyricsModule
import dev.olog.msc.presentation.player.di.PlayerFragmentModule
import dev.olog.msc.presentation.player.mini.di.MiniPlayerFragmentModule
import dev.olog.msc.presentation.playing.queue.di.PlayingQueueFragmentModule
import dev.olog.msc.presentation.preferences.di.PreferencesActivityInjector
import dev.olog.msc.presentation.recently.added.di.RecentlyAddedFragmentInjector
import dev.olog.msc.presentation.related.artists.di.RelatedArtistFragmentInjector
import dev.olog.msc.presentation.search.di.SearchFragmentModule
import dev.olog.msc.presentation.splash.di.SplashActivityModule
import dev.olog.msc.presentation.tabs.di.TabFragmentModule
import dev.olog.msc.presentation.tabs.foldertree.di.FolderTreeFragmentModule

@Module(
    includes = [
        MainActivityInjector::class,
        AboutActivityInjector::class,
        PreferencesActivityInjector::class,
        SplashActivityModule::class,

        MiniPlayerFragmentModule::class,
        CategoriesFragmentsModule::class,
        TabFragmentModule::class,

        CreatePlaylistModule::class,
        FolderTreeFragmentModule::class,
        DetailFragmentInjector::class,
        PlayerFragmentModule::class,
        SearchFragmentModule::class,
        EditItemInjector::class,
        PlayingQueueFragmentModule::class,
        RecentlyAddedFragmentInjector::class,
        RelatedArtistFragmentInjector::class,
        EqualizerModule::class,
        OfflineLyricsModule::class
    ]
)
abstract class PresentationModules