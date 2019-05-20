package dev.olog.msc.presentation.base.paging

import androidx.paging.DataSource
import javax.inject.Provider

abstract class BaseDataSourceFactory<Model, Source : BaseDataSource<Model>>(
    protected val dataSourceProvider: Provider<Source>
) : DataSource.Factory<Int, Model>() {

    protected var dataSource: Source? = null

    override fun create(): DataSource<Int, Model> {
        dataSource?.onDetach()
        dataSource = dataSourceProvider.get()
        dataSource!!.onAttach()
        return dataSource!!
    }

    fun onDetach(){
        dataSource?.onDetach()
        dataSource = null
    }

}