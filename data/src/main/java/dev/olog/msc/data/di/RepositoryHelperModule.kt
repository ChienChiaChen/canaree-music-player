package dev.olog.msc.data.di

import android.content.Context
import androidx.room.Room
import dagger.Module
import dagger.Provides
import dev.olog.msc.core.dagger.qualifier.ApplicationContext
import dev.olog.msc.data.db.AppDatabase
import javax.inject.Singleton

@Module
class RepositoryHelperModule {

    @Provides
    @Singleton
    internal fun provideRoomDatabase(@ApplicationContext context: Context): AppDatabase {
        return Room.databaseBuilder(context, AppDatabase::class.java, "db")
            .fallbackToDestructiveMigration() // 1 to 2
            .build()
    }

}