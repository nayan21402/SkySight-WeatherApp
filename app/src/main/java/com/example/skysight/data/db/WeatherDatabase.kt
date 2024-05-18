package com.example.skysight.data.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(entities = [WeatherData::class], version = 6, exportSchema = false)
abstract class WeatherDatabase : RoomDatabase() {

    abstract fun itemDao(): WeatherDao

    companion object {
        @Volatile
        private var Instance: WeatherDatabase? = null

        fun getDatabase(context: Context): WeatherDatabase {
            // if the Instance is not null, return it, otherwise create a new database instance.
            return Instance ?: synchronized(this) {
                Room.databaseBuilder(context, WeatherDatabase::class.java, "Weather")
                    .fallbackToDestructiveMigration()
                    .build()
                    .also { Instance = it }
            }
        }
    }
}