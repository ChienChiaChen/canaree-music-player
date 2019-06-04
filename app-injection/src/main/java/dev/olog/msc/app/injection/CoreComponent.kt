package dev.olog.msc.app.injection

import android.app.Activity
import android.app.Application
import android.app.Service
import android.appwidget.AppWidgetProvider
import android.content.Context
import android.content.SharedPreferences
import androidx.fragment.app.Fragment
import androidx.lifecycle.Lifecycle
import dagger.BindsInstance
import dagger.Component
import dev.olog.msc.apilastfm.LastFmModule
import dev.olog.msc.app.injection.equalizer.EqualizerModule
import dev.olog.msc.core.IEncrypter
import dev.olog.msc.core.dagger.qualifier.ApplicationContext
import dev.olog.msc.core.dagger.qualifier.ProcessLifecycle
import dev.olog.msc.core.equalizer.IBassBoost
import dev.olog.msc.core.equalizer.IEqualizer
import dev.olog.msc.core.equalizer.IVirtualizer
import dev.olog.msc.core.executors.ComputationDispatcher
import dev.olog.msc.core.executors.IoDispatcher
import dev.olog.msc.core.gateway.*
import dev.olog.msc.core.gateway.podcast.PodcastAlbumGateway
import dev.olog.msc.core.gateway.podcast.PodcastArtistGateway
import dev.olog.msc.core.gateway.podcast.PodcastGateway
import dev.olog.msc.core.gateway.podcast.PodcastPlaylistGateway
import dev.olog.msc.core.gateway.prefs.*
import dev.olog.msc.core.gateway.track.*
import dev.olog.msc.data.di.PreferenceModule
import dev.olog.msc.data.di.RepositoryHelperModule
import dev.olog.msc.data.di.RepositoryModule
import dev.olog.msc.offlinelyrics.domain.ILyricsFromMetadata
import javax.inject.Singleton

@Component(
    modules = arrayOf(
        CoreModule::class,
        SchedulersModule::class,
        LastFmModule::class,

//        // data
        RepositoryModule::class,
        RepositoryHelperModule::class,
        PreferenceModule::class,
//
//        // presentation
//        SleepTimerModule::class,
//        DialogModules::class,
//        WidgetBindingModule::class,
        EqualizerModule::class

//        // floating info service
//        FloatingWindowServiceInjector::class
    )
)
@Singleton
interface CoreComponent {

    @ApplicationContext
    fun context(): Context

    @ProcessLifecycle
    fun lifecycle(): Lifecycle

    fun prefs(): AppPreferencesGateway
    fun musicPrefs(): MusicPreferencesGateway
    fun tutorialPrefs(): TutorialPreferenceGateway
    fun equalizerPrefs(): EqualizerPreferencesGateway
    fun sortPrefs(): SortPreferencesGateway

    fun folderGateway(): FolderGateway
    fun playlistGateway(): PlaylistGateway
    fun songGateway(): SongGateway
    fun albumGateway(): AlbumGateway
    fun artistGateway(): ArtistGateway
    fun genreGateway(): GenreGateway
    fun podcastPlaylistGateway(): PodcastPlaylistGateway
    fun podcastGateway(): PodcastGateway
    fun podcastAlbumGateway(): PodcastAlbumGateway
    fun podcastArtistGateway(): PodcastArtistGateway

    fun lastFmGateway(): LastFmGateway
    fun usedImageGateway(): UsedImageGateway
    fun playingQueueGateway(): PlayingQueueGateway
    fun favoriteGateway(): FavoriteGateway
    fun searchGateway(): SearchGateway
    fun recentSearches(): RecentSearchesGateway
    fun offlineLyrics(): OfflineLyricsGateway

    fun sharedPreferences(): SharedPreferences

    fun equalizer(): IEqualizer
    fun virtualizer(): IVirtualizer
    fun bassBoost(): IBassBoost

    fun encrypter(): IEncrypter
    fun lyricsFromMetadata(): ILyricsFromMetadata

    fun cpuDispatcher(): ComputationDispatcher
    fun ioDispatcher(): IoDispatcher


    @Component.Factory
    interface Factory {

        fun create(@BindsInstance instance: Application): CoreComponent
    }

    companion object {

        private var coreComponent: CoreComponent? = null

        fun coreComponent(app: Application): CoreComponent {
            if (coreComponent == null) {
                // not double checking because it will be created in App.kt on main thread at app startup
                coreComponent = DaggerCoreComponent.factory().create(app)
            }
            return coreComponent!!
        }

        internal fun safeCoreComponent(): CoreComponent = coreComponent!!

    }

}

fun Activity.coreComponent(): CoreComponent = CoreComponent.safeCoreComponent()
fun Service.coreComponent(): CoreComponent = CoreComponent.safeCoreComponent()
fun Fragment.coreComponent(): CoreComponent = CoreComponent.safeCoreComponent()
fun AppWidgetProvider.coreComponent(): CoreComponent = CoreComponent.safeCoreComponent()