package com.stonecoders.stockinteligenterfid.entities

data class Usuario(
    val apellido: String,
    val clave: String,
    val codigo: String,
    val estado: String,
    val nombre: String,
    val permisopos: Any,
    val pin_tarjeta: String,
    val usuario: String
)