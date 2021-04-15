package com.stonecoders.stockinteligenterfid.entities

data class TarjetasFormaspago(
    val banco: String,
    val codigo: String,
    val debitocredito: String,
    val dias: String,
    val estado: Int,
    val nombre: String,
    val tarjeta: String,
    val vigencia_desde: String,
    val vigencia_hasta: String
)