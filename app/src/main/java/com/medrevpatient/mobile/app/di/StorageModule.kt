package com.medrevpatient.mobile.app.di

import android.content.Context
import androidx.room.Room
import com.medrevpatient.mobile.app.data.source.local.datastore.LocalManager
import com.medrevpatient.mobile.app.data.source.local.datastore.LocalManagerImpl
import com.medrevpatient.mobile.app.data.source.local.room.DemandClassesDatabase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
class StorageModule {

    @Singleton
    @Provides
    fun provideLocalManager(@ApplicationContext context: Context): LocalManager {
        return LocalManagerImpl(context)
    }

    @Singleton
    @Provides
    fun provideDataBase(@ApplicationContext context: Context): DemandClassesDatabase {
        return Room.databaseBuilder(
            context = context,
            klass = DemandClassesDatabase::class.java,
            name = "demand_classes.db"
        ).build()
    }

    @Singleton
    @Provides
    fun provideDemandClassesDao(database: DemandClassesDatabase) = database.demandClassesDao()

}