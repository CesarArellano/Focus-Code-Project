package com.cesararellano.focuscode

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.io.Serializable

@Entity(tableName = "scans")
class ScanItem(
    val scanCode: String,
    val scanDate: String,
    var scanType: String,
    @PrimaryKey(autoGenerate = true)
    var scanId: Int = 0,
    ): Serializable