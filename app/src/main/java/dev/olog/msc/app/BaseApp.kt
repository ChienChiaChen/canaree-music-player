package dev.olog.msc.app

import android.app.Application
import com.squareup.leakcanary.LeakCanary
import dagger.android.AndroidInjector
import dagger.android.DispatchingAndroidInjector
import dagger.android.HasAndroidInjector
import javax.inject.Inject

abstract class BaseApp : Application(), HasAndroidInjector {

    @Inject
    @Volatile
    internal lateinit var androidInjector: DispatchingAndroidInjector<Any>

    override fun onCreate() {
        super.onCreate()
        if (LeakCanary.isInAnalyzerProcess(this)) {
            return
        }
        injectComponent()
        initializeApp()
    }

    protected abstract fun initializeApp()

    protected abstract fun injectComponent()

    override fun androidInjector(): AndroidInjector<Any> {
        return androidInjector
    }

}