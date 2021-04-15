package com.stonecoders.stockinteligenterfid.room.repositories

import androidx.annotation.WorkerThread
import com.stonecoders.stockinteligenterfid.entities.SavedDevice
import com.stonecoders.stockinteligenterfid.room.daos.SavedDeviceDao

class SavedDeviceRepository(private val savedDeviceDao: SavedDeviceDao) {

    val allDevices = savedDeviceDao.getAll()

    @Suppress("RedundantSuspendModifier")
    @WorkerThread
    suspend fun insertDevice(device: SavedDevice) {
        savedDeviceDao.insert(device)
    }


}