package com.stonecoders.stockinteligenterfid.room.viewmodels

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.stonecoders.stockinteligenterfid.entities.Articulo
import com.stonecoders.stockinteligenterfid.entities.Stock
import com.stonecoders.stockinteligenterfid.room.repositories.ArticlesRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.lang.NullPointerException

class ArticleViewModel : ViewModel() {
    private var repository = ArticlesRepository()
    val allArticles: MutableLiveData<List<Articulo>> = MutableLiveData(emptyList())
    val allInventory: MutableLiveData<List<Stock>> = MutableLiveData(emptyList())
    val allParents: MutableLiveData<List<Articulo>> = MutableLiveData(emptyList())

    fun retrieveArticles(baseUrl: String, sucursal: String) {
        repository = ArticlesRepository(baseUrl)
        getArticles(sucursal)
    }

    private fun getArticles(sucursal: String) = CoroutineScope(Dispatchers.IO).launch {
        val call = repository.getAllArticles(sucursal = sucursal)
        allArticles.postValue(call)
    }

    fun retrieveParents(baseUrl: String, sucursal: String) = CoroutineScope(Dispatchers.IO).launch {
        repository = ArticlesRepository(baseUrl)
        if (allArticles.value?.isEmpty() == true) {
            val call = repository.getAllArticles(sucursal)
            allArticles.postValue(call)
            val finalList = call.filter { it.metadatadetalle1 == null }
            allParents.postValue(finalList)
        } else {

            allParents.postValue(allArticles.value?.filter { it.metadatadetalle1 == null } )


        }
    }


    fun retrieveInventory(baseUrl: String, deposito: String) = CoroutineScope(Dispatchers.IO).launch {
        repository = ArticlesRepository(baseUrl)
        val call = repository.getAllInventory(deposito = deposito)
        allInventory.postValue(call)
    }


    companion object {
        private const val TAG = "ArticleVM"
    }
}