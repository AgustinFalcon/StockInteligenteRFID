package com.stonecoders.stockinteligenterfid.room.repositories

import com.stonecoders.stockinteligenterfid.entities.Articulo
import com.stonecoders.stockinteligenterfid.entities.Stock
import com.stonecoders.stockinteligenterfid.room.network.StockAPI
import java.io.IOException

class ArticlesRepository {
    var baseUrl = ""

    constructor(baseUrl: String) {
        this.baseUrl = baseUrl
    }

    constructor() {
        baseUrl = "https://wanama.stockinteligente.com/"
    }

    suspend fun getAllInventory(deposito: String): List<Stock> {
        val call = StockAPI.getInstance(baseUrl).getInventoryData(deposito)
        if (call.isSuccessful && call.body() != null) {
            return call.body()!!.stock
        } else {
            return emptyList()
        }
    }

    @Throws(IOException::class)
    suspend fun getAllArticles(sucursal: String): List<Articulo> {
        val initialData = StockAPI.getInstance(baseUrl).getArticleData(sucursal = sucursal, offset = 0)
        val body = initialData.body()
        if (body == null || !initialData.isSuccessful) {
            throw IOException("Error in net call")
        } else {
//            body.total
//            body.limit
//            body.articulos

            var offset = body.limit
            var articlesRead: Int = offset

            var articles: MutableList<Articulo> = mutableListOf<Articulo>()

            while (articlesRead < body.total.toInt()) {
                StockAPI.getInstance(baseUrl).getArticleData(sucursal, offset)
                    .body()?.articulos?.let {
                    articles.addAll(it)
                }
                offset += body.limit
                articlesRead = offset


            }

            return articles
        }
    }

    companion object {
        const val TAG = "ArticleRepo"
    }
}