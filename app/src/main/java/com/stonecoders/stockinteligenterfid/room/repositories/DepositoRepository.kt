package com.stonecoders.stockinteligenterfid.room.repositories

import com.stonecoders.stockinteligenterfid.entities.ConfigurationResponse
import com.stonecoders.stockinteligenterfid.room.network.StockService
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class DepositoRepository(val baseURL: String) {
    private var builder: Retrofit.Builder = Retrofit.Builder()
            .baseUrl(baseURL)
            .addConverterFactory(GsonConverterFactory.create())


    private var retrofit: Retrofit = builder.build()

    var client = retrofit.create(StockService::class.java)


    suspend fun getInitialData(): Response<ConfigurationResponse> {

        //Log.d("GAFAD", client.getInitialData().raw().request().url().toString())
        return client.getInitialData()
    }

}