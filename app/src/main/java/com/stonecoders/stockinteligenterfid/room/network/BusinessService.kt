package com.stonecoders.stockinteligenterfid.room.network

import com.stonecoders.stockinteligenterfid.entities.BusinessResponse
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

interface BusinessService {

    //
    @GET("ws/ws.php?operacion=3")
    suspend fun getBusiness(@Query("nempresa") code: String): Response<BusinessResponse>

}