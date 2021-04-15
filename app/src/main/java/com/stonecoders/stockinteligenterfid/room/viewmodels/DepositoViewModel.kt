package com.stonecoders.stockinteligenterfid.room.viewmodels

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.stonecoders.stockinteligenterfid.entities.Deposito
import com.stonecoders.stockinteligenterfid.entities.MetaMetadatadetalle
import com.stonecoders.stockinteligenterfid.room.repositories.DepositoRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class DepositoViewModel : ViewModel() {
    private var repository = DepositoRepository("http://https://wanama.stockinteligente.com/")
    var depositos: MutableLiveData<List<Deposito>> = MutableLiveData(emptyList())
    var currentDeposito: MutableLiveData<Deposito> = MutableLiveData()
    val metadata: MutableLiveData<List<MetaMetadatadetalle>> = MutableLiveData(emptyList())

    private fun getDepositos() = CoroutineScope(Dispatchers.IO).launch {
        val call = repository.getInitialData()
        if (call.isSuccessful) {
            if (call.body() != null) {
                Log.d(TAG, "CALL IS : ${call.body()}")
                depositos.postValue(call.body()?.depositos)
                metadata.postValue(call.body()?.meta_metadatadetalle)
            } else {
                Log.w(TAG, call.message())
            }
        } else {
            Log.w(TAG, "${repository.baseURL} - ${call.code()}")
        }
    }

    fun retrieveDepositos(baseURL: String) {
        repository = DepositoRepository(baseURL)
        getDepositos()
    }

    fun setCurrentDeposito(deposito: Deposito) {
        currentDeposito.value = deposito
        Log.d(TAG, "Now deposito is ${currentDeposito.value}")
    }


    companion object {
        private const val TAG = "DepositoVM"
    }
}