package com.carcare.database

import androidx.lifecycle.LiveData
import androidx.room.*

@Dao
interface CartDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(vehicle: CartModelData)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(vehicle: List<CartModelData>)

    @Update
    fun update(vehicle: CartModelData)

    @Query("DELETE FROM cart_table WHERE serviceId = :cartId")
    fun delete(cartId: Int)

    @Query("DELETE FROM cart_table")
    fun deleteAll()

    @Query("SELECT * FROM cart_table")
    fun getAllCartData(): LiveData<List<CartModelData>>


}