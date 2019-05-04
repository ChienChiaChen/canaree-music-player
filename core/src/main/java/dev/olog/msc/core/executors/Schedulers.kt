package dev.olog.msc.core.executors

import io.reactivex.Scheduler

interface Schedulers {

    val worker: Scheduler
    val ui: Scheduler

}