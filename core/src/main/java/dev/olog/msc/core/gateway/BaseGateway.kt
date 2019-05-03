package dev.olog.msc.core.gateway

import io.reactivex.Observable


interface BaseGateway<T, in Params> {

    fun getAll(): Observable<List<T>>

    fun getAllNewRequest() : Observable<List<T>>

    fun getByParam(param: Params): Observable<T>

}

