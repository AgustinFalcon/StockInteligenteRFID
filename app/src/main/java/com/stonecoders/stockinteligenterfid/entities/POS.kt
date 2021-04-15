package com.stonecoders.stockinteligenterfid.entities

data class POS(
    val codigo: String,
    val deposito: String,
    val dll: String,
    val estado: String,
    val lector: Any,
    val nombre: String,
    val potencialector: String,
    val puerto: String,
    val sucursal: String
)