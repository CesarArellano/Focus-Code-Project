package com.cesararellano.focuscode

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(entities = [ScanItem::class], version = 1)
abstract class AppDatabase: RoomDatabase() {
    abstract fun scans(): ScansDao

    companion object {
        @Volatile
        private  var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase {
            val tempInstance = INSTANCE

            if( tempInstance != null ) {
                return tempInstance
            }
            // Corrutina para crear bd
            synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "scans_database"
                ).build()
                INSTANCE = instance
                return instance
            }
        }
    }
}