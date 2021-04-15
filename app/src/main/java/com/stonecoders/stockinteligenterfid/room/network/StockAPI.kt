package com.stonecoders.stockinteligenterfid.room.network

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class StockAPI {


    companion object {
        private fun buildService(url: String): StockService {
            val builder: Retrofit.Builder = Retrofit.Builder()
                    .baseUrl(url)
                    .addConverterFactory(GsonConverterFactory.create())


            val retrofit: Retrofit = builder.build()
            val client: StockService = retrofit.create(StockService::class.java)
            return client
        }

        @Volatile
        private var INSTANCE: StockService? = null

        fun getInstance(url: String): StockService =
                INSTANCE ?: buildService(url).also {
                    INSTANCE = it
                }
    }

}

/*
class UsersDatabase : RoomDatabase() {

    companion object {

        @Volatile private var INSTANCE: UsersDatabase? = null

        fun getInstance(context: Context): UsersDatabase =
            INSTANCE ?: synchronized(this) {
                INSTANCE ?: buildDatabase(context).also { INSTANCE = it }
            }

        private fun buildDatabase(context: Context) =
            Room.databaseBuilder(context.applicationContext,
                    UsersDatabase::class.java, "Sample.db")
                    .build()
    }
}
 */