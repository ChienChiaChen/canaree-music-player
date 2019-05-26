package dev.olog.msc.app.base

import com.squareup.leakcanary.LeakCanary
import dagger.android.HasActivityInjector
import dagger.android.HasBroadcastReceiverInjector
import dagger.android.HasServiceInjector
import dagger.android.support.DaggerApplication

abstract class BaseApp : DaggerApplication(),
    HasActivityInjector,
    HasServiceInjector,
    HasBroadcastReceiverInjector {

    override fun onCreate() {
        super.onCreate()
        if (LeakCanary.isInAnalyzerProcess(this)) {
            return
        }
        initializeApp()
    }

    protected abstract fun initializeApp()

}