package com.stonecoders.stockinteligenterfid.room.viewmodels

import androidx.lifecycle.*
import com.stonecoders.stockinteligenterfid.entities.SavedDevice
import com.stonecoders.stockinteligenterfid.room.repositories.SavedDeviceRepository
import kotlinx.coroutines.launch

class SavedDevicesViewModel(private val repository: SavedDeviceRepository) : ViewModel() {
    val allDevices: LiveData<List<SavedDevice>> = repository.allDevices.asLiveData()

    var tempData: SavedDevice? = null
    fun insert(device: SavedDevice) = viewModelScope.launch {
        repository.insertDevice(device)
    }

}

class SavedDevicesViewModelFactory(private val repository: SavedDeviceRepository) :
    ViewModelProvider.Factory {
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(SavedDevicesViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return SavedDevicesViewModel(repository) as T
        }

        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
