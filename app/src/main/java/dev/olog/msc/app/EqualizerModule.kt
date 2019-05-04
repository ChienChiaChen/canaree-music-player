package dev.olog.msc.app

import dagger.Binds
import dagger.Module
import dev.olog.msc.musicservice.equalizer.IBassBoost
import dev.olog.msc.musicservice.equalizer.IEqualizer
import dev.olog.msc.musicservice.equalizer.IVirtualizer
import dev.olog.msc.musicservice.equalizer.impl.BassBoostImpl
import dev.olog.msc.musicservice.equalizer.impl.EqualizerImpl
import dev.olog.msc.musicservice.equalizer.impl.VirtualizerImpl
import javax.inject.Singleton

@Module
abstract class EqualizerModule {

    @Binds
    @Singleton
    abstract fun provideEqualizer(equalizerImpl: EqualizerImpl): IEqualizer

    @Binds
    @Singleton
    abstract fun provideBassBoost(bassBoostImpl: BassBoostImpl): IBassBoost

    @Binds
    @Singleton
    abstract fun provideVirtualizer(virtualizerIml: VirtualizerImpl): IVirtualizer

}