package com.cesararellano.focuscode

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.io.Serializable

// Esta clase es la entidad para la base de datos.
@Entity(tableName = "scans")
class ScanItem(
    val scanCode: String,
    val scanDate: String,
    var scanType: String,
    @PrimaryKey(autoGenerate = true) // Establecemos que los ids sean únicos y autoincrementables.
    var scanId: Int = 0,
): Serializable