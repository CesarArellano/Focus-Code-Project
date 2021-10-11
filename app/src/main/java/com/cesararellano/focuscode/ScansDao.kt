package com.cesararellano.focuscode

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query

@Dao
interface ScansDao {
    @Query("SELECT * FROM Scans")
    fun getAllScans(): LiveData<List<ScanItem>>

    @Insert
    fun insertScan(vararg scan: ScanItem)

    @Query("DELETE FROM Scans WHERE scanId = :scanId")
    fun deleteScan(scanId: Int)

    @Query("DELETE FROM Scans")
    fun deleteAllScans()
}