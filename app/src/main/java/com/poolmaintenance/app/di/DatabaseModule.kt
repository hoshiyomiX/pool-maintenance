package com.poolmaintenance.app.di

import android.content.Context
import androidx.room.Room
import androidx.work.WorkManager
import com.poolmaintenance.app.data.ScheduleDao
import com.poolmaintenance.app.data.VillaDao
import com.poolmaintenance.app.data.VillaDatabase
import com.poolmaintenance.app.data.VillaRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    @Provides
    @Singleton
    fun provideDatabase(@ApplicationContext context: Context): VillaDatabase {
        return Room.databaseBuilder(
            context,
            VillaDatabase::class.java,
            "pool_maintenance_db"
        )
            .addMigrations(VillaDatabase.MIGRATION_1_2)
            .addMigrations(VillaDatabase.MIGRATION_2_3)
            .addMigrations(VillaDatabase.MIGRATION_3_4)
            .addMigrations(VillaDatabase.MIGRATION_4_5)
            .addMigrations(VillaDatabase.MIGRATION_5_6)
            .fallbackToDestructiveMigration()
            .build()
    }

    @Provides
    fun provideVillaDao(database: VillaDatabase): VillaDao {
        return database.villaDao()
    }

    @Provides
    fun provideScheduleDao(database: VillaDatabase): ScheduleDao {
        return database.scheduleDao()
    }

    @Provides
    @Singleton
    fun provideVillaRepository(villaDao: VillaDao, scheduleDao: ScheduleDao): VillaRepository {
        return VillaRepository(villaDao, scheduleDao)
    }

    @Provides
    @Singleton
    fun provideWorkManager(@ApplicationContext context: Context): WorkManager {
        return WorkManager.getInstance(context)
    }
}
