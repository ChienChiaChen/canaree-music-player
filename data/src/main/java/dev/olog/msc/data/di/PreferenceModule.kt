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

@Module(includes = [PreferenceModule.Bindings::class])
class PreferenceModule{


    @Provides
    @Singleton
    internal fun provideSharedPreferences(@ApplicationContext context: Context): SharedPreferences {
        return PreferenceManager.getDefaultSharedPreferences(context)
    }

    @Provides
    @Singleton
    internal fun provideRxPreferences(preferences: SharedPreferences): RxSharedPreferences {
        return RxSharedPreferences.create(preferences)
    }

    @Module
    internal interface Bindings {

        @Binds
        @Singleton
        fun provideEqualizerPreferences(dataStore: EqualizerPreferenceImpl): EqualizerPreferencesGateway

        @Binds
        @Singleton
        fun provideTutorialPreferences(dataStore: TutorialPreferenceImpl): TutorialPreferenceGateway

        @Binds
        @Singleton
        fun provideAppPreferences(dataStore: AppPreferencesImpl): AppPreferencesGateway

        @Binds
        @Singleton
        fun provideMusicPreferences(dataStore: MusicPreferencesImpl): MusicPreferencesGateway

        @Binds
        @Singleton
        fun provideSortingPrefs(impl: AppSortingImpl): SortPreferencesGateway

    }

}