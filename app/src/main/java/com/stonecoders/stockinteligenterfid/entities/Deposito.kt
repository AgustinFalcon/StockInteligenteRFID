package com.stonecoders.stockinteligenterfid.entities

data class Deposito(
    val codigo: String,
    val direccion: String,
    val esreal: String,
    val estado: String,
    val negativo: String,
    val nombre: String,
    val stock: String,
    val sucursal: String
) {
    override fun toString(): String {
        return nombre
    }
}
