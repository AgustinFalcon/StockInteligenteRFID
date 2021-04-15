package com.stonecoders.stockinteligenterfid.entities

data class InventoryResponse(
        val epcs: List<String>,
        val stock: List<Stock>
)