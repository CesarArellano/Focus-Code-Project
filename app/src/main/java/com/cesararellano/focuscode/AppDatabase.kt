package com.cesararellano.focuscode

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

// En este archivo creamos nuestra base de datos con Room.
// Le indicamos que las entidades serán de tipo ScanItem.
@Database(entities = [ScanItem::class], version = 1)
abstract class AppDatabase: RoomDatabase() {
    // Hacemos uso de nuestra interfaz ScansDao
    abstract fun scans(): ScansDao

    // Hacemos un companion object para realizar la instancia de nuestra base de datos sólo si es necesario.
    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null
        // En esta función se ocupó el patrón Singleton para saber si es necesario crear la base de datos o si es que ya está creada devolveríamos la instancia previa.
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
                    "scans_database" // Nombre de la BD.
                ).build()
                INSTANCE = instance
                return instance
            }
        }
    }
}