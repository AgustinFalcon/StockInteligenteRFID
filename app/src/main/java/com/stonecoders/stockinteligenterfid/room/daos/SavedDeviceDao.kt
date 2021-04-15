package com.stonecoders.stockinteligenterfid.room.daos

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.stonecoders.stockinteligenterfid.entities.SavedDevice
import kotlinx.coroutines.flow.Flow


@Dao
interface SavedDeviceDao {

    // Queries and actions
    @Query("SELECT *  FROM saved_devices ORDER BY address ASC")
    fun getAll(): Flow<List<SavedDevice>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(device: SavedDevice)

    @Query("DELETE from saved_devices")
    suspend fun deleteAll()
}