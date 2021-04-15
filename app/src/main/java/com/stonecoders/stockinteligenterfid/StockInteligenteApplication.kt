package com.stonecoders.stockinteligenterfid

import android.app.Application
import com.stonecoders.stockinteligenterfid.room.HelperDatabase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.SupervisorJob

class StockInteligenteApplication : Application() {
    val appScope = CoroutineScope(SupervisorJob())

    private val db = {
        HelperDatabase.getDatabase(this, appScope)
    }


}