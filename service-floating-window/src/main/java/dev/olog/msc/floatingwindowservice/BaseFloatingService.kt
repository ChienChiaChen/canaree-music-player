package dev.olog.msc.floatingwindowservice

import androidx.annotation.CallSuper
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.ServiceLifecycleDispatcher
import dagger.android.AndroidInjection
import dev.olog.msc.floatingwindowservice.api.window.HoverMenuService

abstract class BaseFloatingService : HoverMenuService(), LifecycleOwner {

    @Suppress("LeakingThis")
    private val dispatcher = ServiceLifecycleDispatcher(this)

    @CallSuper
    override fun onCreate() {
        AndroidInjection.inject(this)
        super.onCreate()
    }

    @CallSuper
    override fun onDestroy() {
        dispatcher.onServicePreSuperOnDestroy()
        super.onDestroy()
    }

    override fun getLifecycle(): Lifecycle = dispatcher.lifecycle

}