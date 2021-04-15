package com.stonecoders.stockinteligenterfid.entities

import android.bluetooth.BluetoothDevice
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "saved_devices")
class SavedDevice(
    @PrimaryKey @ColumnInfo(name = "address") val address: String,
    @ColumnInfo(name = "name") val name: String
) {

    companion object {
        fun fromBluetoothDevice(btDevice: BluetoothDevice): SavedDevice {
            if (btDevice.name.isNullOrEmpty()) {
                return SavedDevice(btDevice.address, "Device")
            } else {
                return SavedDevice(btDevice.address, btDevice.name)
            }

        }
    }

}