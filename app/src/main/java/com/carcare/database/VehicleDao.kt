package com.carcare.database

import androidx.lifecycle.LiveData
import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface VehicleDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(vehicle: VehicleModel)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(vehicle: List<VehicleModel>)

    @Update
    fun update(vehicle: VehicleModel)

    @Query("DELETE FROM vehicle_table WHERE id = :vehicleId")
    fun delete(vehicleId: String)

    @Query("SELECT * FROM vehicle_table")
    fun getAllVehicles(): LiveData<List<VehicleModel>>

    @Query("SELECT * FROM vehicle_table WHERE is_primary = 1 ")
    fun getPrimaryVehicle(): LiveData<VehicleModel>

}