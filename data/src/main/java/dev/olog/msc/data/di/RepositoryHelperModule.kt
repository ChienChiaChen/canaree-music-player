package dev.olog.msc.data.di

import android.content.Context
import androidx.room.Room
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import dagger.Module
import dagger.Provides
import dev.olog.msc.core.dagger.qualifier.ApplicationContext
import dev.olog.msc.data.db.AppDatabase
import dev.olog.msc.data.di.Migrations.MIGRATION_15_16
import javax.inject.Singleton

@Module
abstract class RepositoryHelperModule {

    @Module
    companion object {
        @Provides
        @JvmStatic
        @Singleton
        internal fun provideRoomDatabase(@ApplicationContext context: Context): AppDatabase {
            return Room.databaseBuilder(context, AppDatabase::class.java, "db")
//            .fallbackToDestructiveMigration()
                .addMigrations(MIGRATION_15_16)
                .build()
        }
    }

}

internal object Migrations {

    val MIGRATION_15_16 = object : Migration(15, 16) {
        override fun migrate(database: SupportSQLiteDatabase) {
            database.execSQL("DROP TABLE last_fm_podcast")
            database.execSQL("DROP TABLE last_fm_podcast_album")
            database.execSQL("DROP TABLE last_fm_podcast_artist")
            database.execSQL("DROP TABLE mini_queue")
        }
    }

}