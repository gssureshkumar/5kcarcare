package com.carcare.database

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "vehicle_table")
data class VehicleModel(
    @PrimaryKey val uid: Long,
    @ColumnInfo(name = "car_model") val carModel: String?,
    @ColumnInfo(name = "is_primary") var isPrimary: Boolean?,
    @ColumnInfo(name = "model_name") val modelName: String?,
    @ColumnInfo(name = "registration") val registration: String?
)
