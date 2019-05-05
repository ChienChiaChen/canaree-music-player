package dev.olog.msc.musicservice.di

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
    internal abstract fun provideEqualizer(equalizerImpl: EqualizerImpl): IEqualizer

    @Binds
    @Singleton
    internal abstract fun provideBassBoost(bassBoostImpl: BassBoostImpl): IBassBoost

    @Binds
    @Singleton
    internal abstract fun provideVirtualizer(virtualizerIml: VirtualizerImpl): IVirtualizer

}