package dev.olog.msc.data.di

import android.content.Context
import android.content.SharedPreferences
import android.preference.PreferenceManager
import com.f2prateek.rx.preferences2.RxSharedPreferences
import dagger.Binds
import dagger.Module
import dagger.Provides
import dev.olog.msc.core.dagger.qualifier.ApplicationContext
import dev.olog.msc.core.gateway.prefs.*
import dev.olog.msc.data.prefs.EqualizerPreferenceImpl
import dev.olog.msc.data.prefs.MusicPreferencesImpl
import dev.olog.msc.data.prefs.TutorialPreferenceImpl
import dev.olog.msc.data.prefs.app.AppPreferencesImpl
import dev.olog.msc.data.prefs.app.AppSortingImpl
import javax.inject.Singleton

@Module
abstract class PreferenceModule{


    @Module
    companion object {
        @Provides
        @JvmStatic
        @Singleton
        internal fun provideSharedPreferences(@ApplicationContext context: Context): SharedPreferences {
            return PreferenceManager.getDefaultSharedPreferences(context)
        }

        @Provides
        @JvmStatic
        @Singleton
        internal fun provideRxPreferences(preferences: SharedPreferences): RxSharedPreferences {
            return RxSharedPreferences.create(preferences)
        }
    }

    @Binds
    @Singleton
    internal abstract fun provideEqualizerPreferences(dataStore: EqualizerPreferenceImpl): EqualizerPreferencesGateway

    @Binds
    @Singleton
    internal abstract fun provideTutorialPreferences(dataStore: TutorialPreferenceImpl): TutorialPreferenceGateway

    @Binds
    @Singleton
    internal abstract fun provideAppPreferences(dataStore: AppPreferencesImpl): AppPreferencesGateway

    @Binds
    @Singleton
    internal abstract fun provideMusicPreferences(dataStore: MusicPreferencesImpl): MusicPreferencesGateway

    @Binds
    @Singleton
    internal abstract fun provideSortingPrefs(impl: AppSortingImpl): SortPreferencesGateway

}