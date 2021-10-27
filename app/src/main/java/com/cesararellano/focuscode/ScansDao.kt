package com.cesararellano.focuscode

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query

// El Dao es una interfaz que nos permite interactuar con los datos almacenados en la BD de Room, usando SQL
@Dao
interface ScansDao {
    @Query("SELECT * FROM Scans")
    fun getAllScans(): LiveData<List<ScanItem>>

    @Insert
    fun insertScan(vararg scan: ScanItem)

    @Query("DELETE FROM Scans")
    fun deleteAllScans()
}