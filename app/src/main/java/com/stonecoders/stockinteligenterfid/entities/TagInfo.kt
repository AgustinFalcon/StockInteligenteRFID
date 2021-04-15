package com.stonecoders.stockinteligenterfid.entities

import com.rscja.deviceapi.entity.UHFTAGInfo


data class TagInfo(val epc: String?, val rssi: String?) {

    companion object {
        @JvmStatic
        fun fromLegacyTag(tag: UHFTAGInfo): TagInfo {
            return TagInfo(
                epc = tag.epc,
                rssi = tag.rssi,
            )
        }


    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as TagInfo

        if (epc != other.epc) return false

        return true
    }

    override fun hashCode(): Int {
        return epc?.hashCode() ?: 0
    }
}