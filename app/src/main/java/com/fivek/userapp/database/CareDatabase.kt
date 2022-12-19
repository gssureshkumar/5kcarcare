package com.fivek.userapp.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(entities = [VehicleModel::class, CartModelData::class ], version = 1)
abstract class CareDatabase : RoomDatabase() {

    abstract fun vehicleDao(): VehicleDao
    abstract fun cartDao(): CartDao

    companion object {
        private var instance: CareDatabase? = null

        @Synchronized
        fun getInstance(ctx: Context): CareDatabase {
            if(instance == null)
                instance = Room.databaseBuilder(ctx.applicationContext, CareDatabase::class.java,
                    "vehicle_database")
                    .fallbackToDestructiveMigration()
                    .build()

            return instance!!

        }


    }

}