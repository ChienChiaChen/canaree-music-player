package dev.olog.msc.imagecreation

import android.content.Context
import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleOwner
import dev.olog.msc.core.dagger.qualifier.ApplicationContext
import dev.olog.msc.core.dagger.qualifier.ProcessLifecycle
import dev.olog.msc.core.gateway.prefs.AppPreferencesGateway
import dev.olog.msc.core.interactor.all.GetAllFoldersUseCase
import dev.olog.msc.core.interactor.all.GetAllGenresUseCase
import dev.olog.msc.core.interactor.all.GetAllPlaylistsUseCase
import dev.olog.msc.imagecreation.utils.isLowMemoryDevice
import io.reactivex.disposables.CompositeDisposable
import javax.inject.Inject

internal class ImagesCreator @Inject constructor(
        @ApplicationContext private val context: Context,
        @ProcessLifecycle lifecycle: Lifecycle,
        private val getAllFoldersUseCase: GetAllFoldersUseCase,
        private val getAllPlaylistsUseCase: GetAllPlaylistsUseCase,
        private val getAllGenresUseCase: GetAllGenresUseCase,

        private val folderImagesCreator: FolderImagesCreator,
        private val playlistImagesCreator: PlaylistImagesCreator,
        private val genreImagesCreator: GenreImagesCreator,
        private val appPreferencesUseCase: AppPreferencesGateway

) : DefaultLifecycleObserver, IImageCreator {

    private val subscriptions = CompositeDisposable()

    init {
        lifecycle.addObserver(this)
    }

    override fun onStart(owner: LifecycleOwner) {
        if (isLowMemoryDevice(context)){
            return
        }

//        TODO images creation
//        Flowables.combineLatest(
//                getAllFoldersUseCase.execute().onErrorReturnItem(listOf()).asFlowable(),
//                appPreferencesUseCase.observeAutoCreateImages().asFlowable(),
//                { folders, create -> if (create) folders else listOf() })
//                .debounce(5, TimeUnit.SECONDS)
//                .switchMap { folderImagesCreator.execute() }
//                .subscribe({}, Throwable::printStackTrace)
//                .addTo(subscriptions)
//
//        Flowables.combineLatest(
//                getAllPlaylistsUseCase.execute().onErrorReturnItem(listOf()).asFlowable(),
//                appPreferencesUseCase.observeAutoCreateImages().asFlowable(),
//                { playlists, create -> if (create) playlists else listOf() })
//                .debounce(5, TimeUnit.SECONDS)
//                .switchMap { playlistImagesCreator.execute(it) }
//                .subscribe({}, Throwable::printStackTrace)
//                .addTo(subscriptions)
//
//        Flowables.combineLatest(
//                getAllGenresUseCase.execute().onErrorReturnItem(listOf()).asFlowable(),
//                appPreferencesUseCase.observeAutoCreateImages().asFlowable(),
//                { genres, create -> if (create) genres else listOf() })
//                .debounce(5, TimeUnit.SECONDS)
//                .switchMap { genreImagesCreator.execute(it) }
//                .subscribe({}, Throwable::printStackTrace)
//                .addTo(subscriptions)
    }

    override fun onStop(owner: LifecycleOwner) {
        subscriptions.clear()
    }

}