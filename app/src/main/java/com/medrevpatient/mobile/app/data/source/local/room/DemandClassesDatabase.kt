package com.medrevpatient.mobile.app.data.source.local.room

import androidx.room.Database
import androidx.room.RoomDatabase


@Database(entities = [DemandClassesEntity::class], version = 1)
abstract class DemandClassesDatabase : RoomDatabase() {
    abstract fun demandClassesDao(): DemandClassesDao
}