package com.stonecoders.stockinteligenterfid.entities

data class ArticlesResponse(
    val actualizaciones: Int,
    val articulos: List<Articulo>,
    val limit: Int,
    val total: String
)