package com.stonecoders.stockinteligenterfid.entities

data class Branch(val code: String, val display: String) {
    override fun toString(): String {
        return "$display - $code"
    }
}
