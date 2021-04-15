package com.stonecoders.stockinteligenterfid.room.repositories

import com.stonecoders.stockinteligenterfid.room.network.BusinessService
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class BusinessRepository(var code: String) {


    private var builder: Retrofit.Builder = Retrofit.Builder()
            .baseUrl("https://stockinteligente.com/")
            .addConverterFactory(GsonConverterFactory.create())


    private var retrofit: Retrofit = builder.build()
    private var client: BusinessService = retrofit.create(BusinessService::class.java)

    suspend fun getBusiness() = client.getBusiness(code = code)

    /*
    client.getBusiness("wbyg").enqueue(object : Callback<JsonObject> {
        override fun onFailure(call: Call<JsonObject>?, t: Throwable?) {
            // TODO: Handle Network error
            Log.d(TAG, "Error is: $call ", t)

        }
        override fun onResponse(call: Call<JsonObject>?, r: Response<JsonObject>?) {
            val gsonBuilder = GsonBuilder()
            gsonBuilder.setPrettyPrinting()
            val gson = gsonBuilder.create()
            val businessResponse = gson.fromJson(r?.body()?.get("empresa")?.asJsonArray?.get(0)?.toString(), BusinessResponse::class.java)
            Log.d(TAG, "Response is:  $businessResponse")

        }

    }
    )

    */


    companion object {
        const val TAG = "BusinessRepo"
    }
}