package com.stonecoders.stockinteligenterfid.room.network

import com.stonecoders.stockinteligenterfid.entities.ArticlesResponse
import com.stonecoders.stockinteligenterfid.entities.ConfigurationResponse
import com.stonecoders.stockinteligenterfid.entities.InventoryResponse
import com.stonecoders.stockinteligenterfid.entities.UserValidationResponse
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Query

interface StockService {
    //TODO: Look up the different branches on the link corresponding to that business
    //fun getBranches
    //fun getArticles (branch & offset shit)

    //TODO: Logging

    @GET("ws_datos.php?operacion=50&sucursal=1")
    suspend fun getInitialData(): Response<ConfigurationResponse>

    @POST("ws_antena.php?operacion=32")
    suspend fun validateUser(@Query("nombre") nombre: String, @Query("clave") clave: String): Response<UserValidationResponse>

    @GET("ws_datos.php?operacion=52&alldata=1")
    suspend fun getArticleData(@Query("sucursal") sucursal: String, @Query("offset") offset: Int): Response<ArticlesResponse>

    @GET("ws_datos.php?operacion=33")
    suspend fun getInventoryData(@Query("deposito") deposito: String): Response<InventoryResponse>
}