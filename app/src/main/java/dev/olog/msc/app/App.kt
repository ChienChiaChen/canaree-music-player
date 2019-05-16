package dev.olog.msc.app

import android.annotation.SuppressLint
import android.app.AlarmManager
import android.content.Context
import androidx.lifecycle.LifecycleOwner
import androidx.preference.PreferenceManager
import dagger.android.AndroidInjector
import dagger.android.support.DaggerApplication
import dev.olog.msc.BuildConfig
import dev.olog.msc.R
import dev.olog.msc.app.base.ThemedApp
import dev.olog.msc.core.AppShortcuts
import dev.olog.msc.core.gateway.LastFmGateway
import dev.olog.msc.core.gateway.podcast.PodcastGateway
import dev.olog.msc.core.gateway.track.SongGateway
import dev.olog.msc.core.interactor.SleepTimerUseCase
import dev.olog.msc.imagecreation.IImageCreator
import dev.olog.msc.musicservice.MusicService
import dev.olog.msc.presentation.base.ImageViews
import dev.olog.msc.shared.PendingIntents
import dev.olog.msc.shared.Permissions
import dev.olog.msc.shared.TrackUtils
import dev.olog.msc.shared.updatePermissionValve
import io.alterac.blurkit.BlurKit
import io.reactivex.Completable
import io.reactivex.schedulers.Schedulers
import javax.inject.Inject

@SuppressLint("StaticFieldLeak")
lateinit var app: Context


class App : ThemedApp() {

    @Suppress("unused")
    @Inject
    lateinit var appShortcuts: AppShortcuts
    @Suppress("unused")
    @Inject
    lateinit var imagesCreator: IImageCreator
    @Suppress("unused")

    @Inject
    lateinit var lastFmGateway: LastFmGateway
    @Inject
    lateinit var songGateway: SongGateway
    @Inject
    lateinit var podcastGateway: PodcastGateway
    @Inject
    lateinit var alarmManager: AlarmManager
    @Inject
    lateinit var sleepTimerUseCase: SleepTimerUseCase

    override fun onCreate() {
        TrackUtils.initialize(
            getString(R.string.common_unknown_artist),
            getString(R.string.common_unknown_album)
        )
        super.onCreate()
    }

    override fun initializeApp() {
        initializeComponents()
        initializeConstants()
        resetSleepTimer()

        registerActivityLifecycleCallbacks(CustomTabsActivityLifecycleCallback())
    }

    override fun onStart(owner: LifecycleOwner) {
        updatePermissionValve(this, Permissions.canReadStorage(this))
    }

    private fun initializeComponents() {
        initializeAsync {
            BlurKit.init(this)
        }

        if (BuildConfig.DEBUG) {
//            Traceur.enableLogging()
//            LeakCanary.install(this)
//            Stetho.initializeWithDefaults(this)
        }
    }

    private fun initializeConstants() {
        ImageViews.initialize(this)
        PreferenceManager.setDefaultValues(this, R.xml.prefs, false)
    }

    private fun resetSleepTimer() {
        sleepTimerUseCase.reset()
        alarmManager.cancel(PendingIntents.stopMusicServiceIntent(this, MusicService::class.java))
    }

    override fun applicationInjector(): AndroidInjector<out DaggerApplication> {
        return DaggerAppComponent.builder().create(this)
    }

    private fun initializeAsync(what: () -> Unit){
        val disposable = Completable.fromCallable { what }
            .subscribeOn(Schedulers.computation())
            .subscribe({}, Throwable::printStackTrace)
    }

}
