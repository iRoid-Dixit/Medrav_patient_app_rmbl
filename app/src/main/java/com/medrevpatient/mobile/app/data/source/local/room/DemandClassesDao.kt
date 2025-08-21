package com.medrevpatient.mobile.app.data.source.local.room

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Upsert
import kotlinx.coroutines.flow.Flow

@Dao
interface DemandClassesDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(entity: DemandClassesEntity)

    @Upsert
    suspend fun update(entity: DemandClassesEntity)

    @Query("SELECT * FROM DemandClasses")
    fun getAll(): Flow<List<DemandClassesEntity>>

    @Query("SELECT * FROM DemandClasses where id = :id")
    suspend fun getById(id: String): DemandClassesEntity?

    @Query("SELECT id FROM DemandClasses")
    suspend fun getIds(): List<String>?

    //fetch all record where type = 1 and type specific when type is other than 1
    @Query("SELECT * FROM DemandClasses WHERE :type = 1 OR type = :type")
    fun getByType(type: Int): Flow<List<DemandClassesEntity>>

    @Query("DELETE FROM DemandClasses WHERE id = :id")
    suspend fun deleteById(id: String)

    @Query("DELETE FROM DemandClasses")
    suspend fun clearAll()

}
