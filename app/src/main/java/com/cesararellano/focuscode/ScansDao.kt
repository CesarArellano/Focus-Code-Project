package com.cesararellano.focuscode

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query

@Dao
interface ScansDao {
    @Query("SELECT * FROM Scans")
    fun getAllScans(): LiveData<List<ScanItem>>

    @Insert
    fun insertScan(vararg scan: ScanItem)

    @Delete
    fun deleteScan(scanId: String)

    @Delete
    fun deleteAllScans()
}