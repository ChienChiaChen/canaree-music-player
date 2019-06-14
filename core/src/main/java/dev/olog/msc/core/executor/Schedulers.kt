package dev.olog.msc.core.executor

import io.reactivex.Scheduler

interface Schedulers {

    val worker: Scheduler
    val ui: Scheduler

}