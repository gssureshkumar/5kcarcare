package com.carcare.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase

@Database(entities = [VehicleModel::class], version = 1)
abstract class VehicleDatabase : RoomDatabase() {

    abstract fun vehicleDao(): VehicleDao

    companion object {
        private var instance: VehicleDatabase? = null

        @Synchronized
        fun getInstance(ctx: Context): VehicleDatabase {
            if(instance == null)
                instance = Room.databaseBuilder(ctx.applicationContext, VehicleDatabase::class.java,
                    "vehicle_database")
                    .fallbackToDestructiveMigration()
                    .build()

            return instance!!

        }


    }

}