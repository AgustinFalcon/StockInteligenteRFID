package com.stonecoders.stockinteligenterfid.room.repositories

import androidx.annotation.WorkerThread
import com.stonecoders.stockinteligenterfid.entities.User
import com.stonecoders.stockinteligenterfid.entities.UserValidationResponse
import com.stonecoders.stockinteligenterfid.room.daos.UserDao
import com.stonecoders.stockinteligenterfid.room.network.StockAPI
import retrofit2.Response

class UserRepository(private val userDao: UserDao) {

    var baseUrl = "https://wanama.stockinteligente.com/"
    val allUsers = userDao.getAll()

    @Suppress("RedundantSuspendModifier")
    @WorkerThread
    suspend fun insertUser(user: User) {
        userDao.insert(user)
    }

    suspend fun validateUser(name: String, pwd: String): Response<UserValidationResponse> {
        return StockAPI.getInstance(baseUrl).validateUser(name, pwd)
    }
}