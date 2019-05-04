package dev.olog.msc.pro

import dagger.Binds
import dagger.Module
import dev.olog.msc.core.dagger.scope.PerActivity

@Module
abstract class ProModule {

    @Binds
    @PerActivity
    internal abstract fun provideBilling(impl: BillingImpl): IBilling

}