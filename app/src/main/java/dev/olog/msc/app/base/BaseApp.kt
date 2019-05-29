package dev.olog.msc.app.base

import android.app.Application
import com.squareup.leakcanary.LeakCanary

abstract class BaseApp : Application(){

    override fun onCreate() {
        super.onCreate()
        if (LeakCanary.isInAnalyzerProcess(this)) {
            return
        }
        injectComponenet()
        initializeApp()
    }

    protected abstract fun injectComponenet()

    protected abstract fun initializeApp()

}