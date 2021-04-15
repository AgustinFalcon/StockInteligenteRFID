package com.stonecoders.stockinteligenterfid.room

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import com.stonecoders.stockinteligenterfid.entities.SavedDevice
import com.stonecoders.stockinteligenterfid.entities.User
import com.stonecoders.stockinteligenterfid.room.daos.SavedDeviceDao
import com.stonecoders.stockinteligenterfid.room.daos.UserDao
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch


@Database(entities = arrayOf(SavedDevice::class, User::class), version = 3, exportSchema = false)
abstract class HelperDatabase : RoomDatabase() {
    abstract fun savedDeviceDao(): SavedDeviceDao
    abstract fun userDao(): UserDao

    private class HelperDatabaseCallback(private val scope: CoroutineScope) : RoomDatabase.Callback() {
        override fun onCreate(db: SupportSQLiteDatabase) {
            super.onCreate(db)
            INSTANCE?.let { database ->
                scope.launch {
                    populateDatabase(database.savedDeviceDao())
                }
            }
        }

        suspend fun populateDatabase(deviceDao: SavedDeviceDao) {
            // Delete all content here.
            deviceDao.deleteAll()
            deviceDao.insert(SavedDevice("E8:23:6D:7B:A1:3C", "RFID Device 1"))

            // TODO: Add your own words!
        }
    }

    companion object {

        @Volatile
        private var INSTANCE: HelperDatabase? = null

        fun getDatabase(context: Context, scope: CoroutineScope): HelperDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                        context.applicationContext,
                        HelperDatabase::class.java,
                        "helper_database"
                )
                        .addCallback(HelperDatabaseCallback(scope))
                        .fallbackToDestructiveMigration()
                        .build()
                INSTANCE = instance

                instance
            }


        }
    }

}