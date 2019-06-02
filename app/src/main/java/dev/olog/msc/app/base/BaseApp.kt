package dev.olog.msc.app.base

import android.app.Application
import com.squareup.leakcanary.LeakCanary

abstract class BaseApp : Application(){

    override fun onCreate() {
        super.onCreate()
        if (LeakCanary.isInAnalyzerProcess(this)) {
            return
        }
        injectComponent()
        initializeApp()
    }

    protected abstract fun injectComponent()

    protected abstract fun initializeApp()

}