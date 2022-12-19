package com.fivek.userapp.database

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "cart_table")
data class CartModelData(
    @PrimaryKey val serviceId: Int,
    @ColumnInfo(name = "serviceName") val serviceName: String?,
    @ColumnInfo(name = "serviceImg") var serviceImg: String?,
    @ColumnInfo(name = "elite") var elite: Boolean?,
    @ColumnInfo(name = "price") val price: Long =0,
    @ColumnInfo(name = "offer") val offer: Long=0
)
