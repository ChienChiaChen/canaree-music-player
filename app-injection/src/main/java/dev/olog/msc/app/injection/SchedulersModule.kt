package dev.olog.msc.app.injection

import dagger.Binds
import dagger.Module
import dev.olog.msc.core.executors.ComputationDispatcher
import dev.olog.msc.core.executors.IoDispatcher
import kotlinx.coroutines.Dispatchers
import javax.inject.Inject
import javax.inject.Singleton
import kotlin.coroutines.CoroutineContext

@Module
abstract class SchedulersModule {

    @Binds
    @Singleton
    abstract fun provideCPU(scheduler: IODispatch): IoDispatcher

    @Binds
    @Singleton
    abstract fun provideIO(scheduler: ComputationDispatch): ComputationDispatcher

}

class ComputationDispatch @Inject constructor() : ComputationDispatcher {
    override val worker: CoroutineContext
        get() = Dispatchers.Default // TODO make a custom pool since on lower device can be only 1 thread??
}

class IODispatch @Inject constructor() : IoDispatcher {
    override val worker: CoroutineContext
        get() = Dispatchers.IO
}