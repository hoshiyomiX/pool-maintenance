package com.poolmaintenance.app.di

import android.content.Context
import androidx.room.Room
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
            .fallbackToDestructiveMigration()
            .build()
    }

    @Provides
    fun provideVillaDao(database: VillaDatabase): VillaDao {
        return database.villaDao()
    }

    @Provides
    @Singleton
    fun provideVillaRepository(villaDao: VillaDao): VillaRepository {
        return VillaRepository(villaDao)
    }
}
