package dev.olog.msc.core.gateway

import io.reactivex.Completable
import io.reactivex.Observable

interface HasLastPlayed<T> {

    fun getLastPlayed(): Observable<List<T>>

    fun addLastPlayed(id: Long): Completable

}