package com.cesararellano.focuscode

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.android.gms.maps.model.LatLng
import java.io.Serializable

@Entity(tableName = "scans")
class ScanItem(
    val scanCode: String,
    val scanDate: String,
    var scanType: String,
    @PrimaryKey(autoGenerate = true)
    var scanId: Int = 0,
    ): Serializable {

    private fun getLatLng():LatLng {
        val latLng = this.scanCode.substring(4).split(',');
        val lat = latLng[0].toDouble();
        val lng = latLng[1].toDouble();
        return LatLng(lat, lng);
    }
}