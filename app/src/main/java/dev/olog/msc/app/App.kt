package dev.olog.msc.app

import android.app.AlarmManager
import android.content.Context
import androidx.preference.PreferenceManager
import dev.olog.msc.BuildConfig
import dev.olog.msc.R
import dev.olog.msc.app.base.ThemedApp
import dev.olog.msc.app.injection.CoreComponent
import dev.olog.msc.appshortcuts.AppShortcuts
import dev.olog.msc.core.interactor.SleepTimerUseCase
import dev.olog.msc.dagger.DaggerAppComponent
import dev.olog.msc.musicservice.MusicService
import dev.olog.msc.presentation.base.ImageViews
import dev.olog.msc.shared.PendingIntents
import dev.olog.msc.shared.TrackUtils
import io.alterac.blurkit.BlurKit
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import javax.inject.Inject

class App : ThemedApp() {

    private lateinit var appShortcuts: AppShortcuts

    @Inject
    lateinit var sleepTimerUseCase: SleepTimerUseCase

    override fun initializeApp() {
        initializeComponents()
        initializeConstants()
        resetSleepTimer()

        registerActivityLifecycleCallbacks(CustomTabsActivityLifecycleCallback())
    }

    private fun initializeComponents() {
        appShortcuts = AppShortcuts.instance(this)
        ImageViews.initialize(this)
        GlobalScope.launch {
            BlurKit.init(this@App)
        }

        if (BuildConfig.DEBUG) {
//            DebugProbes.install()
//            Traceur.enableLogging()
//            LeakCanary.install(this)
//            Stetho.initializeWithDefaults(this)
        }
    }

    private fun initializeConstants() {
        TrackUtils.initialize(
            getString(R.string.common_unknown_artist),
            getString(R.string.common_unknown_album)
        )
        PreferenceManager.setDefaultValues(this, R.xml.prefs, false)
    }

    private fun resetSleepTimer() {
        sleepTimerUseCase.reset()
        val alarmManager = getSystemService(Context.ALARM_SERVICE) as AlarmManager
        alarmManager.cancel(PendingIntents.stopMusicServiceIntent(this, MusicService::class.java))
    }

    override fun injectComponenet() {
        val coreComponent = CoreComponent.coreComponent(this)
        DaggerAppComponent.factory().create(coreComponent).inject(this)
    }

}
