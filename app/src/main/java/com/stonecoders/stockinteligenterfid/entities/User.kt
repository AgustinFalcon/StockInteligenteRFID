package com.stonecoders.stockinteligenterfid.entities

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey


@Entity(tableName = "user_table")
class User(
        @ColumnInfo(name = "name") val name: String,
        @ColumnInfo(name = "email") @PrimaryKey val email: String,
        @ColumnInfo(name = "pwd") val pwd: String,
        @ColumnInfo(name = "brand") val brand: String
)