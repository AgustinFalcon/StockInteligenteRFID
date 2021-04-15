package com.stonecoders.stockinteligenterfid.entities

data class UserValidationResponse(
        val clave: String,
        val codigo: String,
        val impresora: String,
        val lector: String,
        val permiso: String,
        val usuario: String
)