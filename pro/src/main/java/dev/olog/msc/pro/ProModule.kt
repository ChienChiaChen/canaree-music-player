package dev.olog.msc.pro

import dagger.Binds
import dagger.Module

@Module
abstract class ProModule {

    @Binds
    internal abstract fun provideBilling(impl: BillingImpl): IBilling

}