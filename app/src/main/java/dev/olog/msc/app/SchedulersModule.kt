package dev.olog.msc.app

import dagger.Binds
import dagger.Module
import dev.olog.msc.core.coroutines.ComputationDispatcher
import dev.olog.msc.core.coroutines.IoDispatcher
import dev.olog.msc.core.executors.ComputationScheduler
import dev.olog.msc.core.executors.IoScheduler
import io.reactivex.Scheduler
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import kotlinx.coroutines.Dispatchers
import javax.inject.Inject
import javax.inject.Singleton
import kotlin.coroutines.CoroutineContext

@Module
abstract class SchedulersModule {

    @Binds
    @Singleton
    abstract fun provideComputationSchedulers(scheduler: ComputationSchedulers) : ComputationScheduler

    @Binds
    @Singleton
    abstract fun provideIoSchedulers(scheduler: IoSchedulers) : IoScheduler

    @Binds
    @Singleton
    abstract fun provideCPU(scheduler: IODispatch) : IoDispatcher

    @Binds
    @Singleton
    abstract fun provideIO(scheduler: ComputationDispatch) : ComputationDispatcher

}

class ComputationDispatch @Inject constructor(): ComputationDispatcher {
    override val worker: CoroutineContext
        get() = Dispatchers.Default
}

class IODispatch @Inject constructor(): IoDispatcher{
    override val worker: CoroutineContext
        get() = Dispatchers.Default
}

class ComputationSchedulers @Inject constructor(): ComputationScheduler {

    override val worker: Scheduler
        get() = Schedulers.computation()

    override val ui: Scheduler
        get() = AndroidSchedulers.mainThread()
}

class IoSchedulers @Inject constructor(): IoScheduler {

    override val worker: Scheduler
        get() = Schedulers.io()

    override val ui: Scheduler
        get() = AndroidSchedulers.mainThread()
}