package com.stonecoders.stockinteligenterfid.room.viewmodels

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.stonecoders.stockinteligenterfid.entities.Empresa
import com.stonecoders.stockinteligenterfid.room.repositories.BusinessRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class BusinessViewModel : ViewModel() {
    private var repository = BusinessRepository("")
    private var brand: MutableLiveData<String> = MutableLiveData(repository.code)
    var business: MutableLiveData<Empresa> = MutableLiveData()
    fun selectBrand(code: String) {
        brand.value = code
        repository.code = code
        getBusiness()
    }


    private fun getBusiness() = CoroutineScope(Dispatchers.IO).launch {
        val call = repository.getBusiness()
        if (call.isSuccessful) {
            if (call.body() != null) {
                business.postValue(call.body()!!.empresa[0])
            }
        } else {
            Log.d(TAG, "${call.message()} - ${call.raw().request().url()}")

        }
    }

    companion object {
        private const val TAG = "BusinessViewModel"
    }


}

class BusinessViewModelFactory(private val repository: BusinessRepository) : ViewModelProvider.Factory {
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(BusinessViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return BusinessViewModel() as T
        }

        throw IllegalArgumentException("Unknownv view model class")
    }
}
