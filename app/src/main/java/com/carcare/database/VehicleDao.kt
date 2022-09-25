package com.carcare.database

import androidx.lifecycle.LiveData
import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface VehicleDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(vehicle: VehicleModel)

    @Update
    fun update(vehicle: VehicleModel)

    @Delete
    fun delete(vehicle: VehicleModel)

    @Query("SELECT * FROM vehicle_table")
    fun getAllVehicles(): LiveData<List<VehicleModel>>

    @Query("SELECT * FROM vehicle_table WHERE is_primary = 1 ")
    fun getPrimaryVehicle(): LiveData<VehicleModel>

}