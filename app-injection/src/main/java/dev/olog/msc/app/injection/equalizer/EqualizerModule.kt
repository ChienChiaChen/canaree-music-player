package dev.olog.msc.app.injection.equalizer

import dagger.Binds
import dagger.Module
import dev.olog.msc.core.equalizer.IBassBoost
import dev.olog.msc.core.equalizer.IEqualizer
import dev.olog.msc.core.equalizer.IVirtualizer
import javax.inject.Singleton

@Module
abstract class EqualizerModule {

    @Binds
    @Singleton
    internal abstract fun provideEqualizer(equalizerImpl: EqualizerImpl): IEqualizer

    @Binds
    @Singleton
    internal abstract fun provideBassBoost(bassBoostImpl: BassBoostImpl): IBassBoost

    @Binds
    @Singleton
    internal abstract fun provideVirtualizer(virtualizerIml: VirtualizerImpl): IVirtualizer

}