package com.example.fleetmanagement.Database

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity("Fleet_Cars")
@kotlinx.serialization.Serializable
data class CarEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0 ,
    @ColumnInfo("estimated_mileage")
    val est_milage: String?,
    @ColumnInfo("battery_level")
    var batteryLevel: Double? = null,
    @ColumnInfo("engine_health")
    var engineHealth: String? = null,
    @ColumnInfo("latitude")
    var latitude: Double? = null,
    @ColumnInfo("longitude")
    var longitude: Double? = null,
    @ColumnInfo("speed")
    var speed: Double? = null,
    @ColumnInfo("warning")
    var warning: String? = null, // Nullable as it might not be available initially
    @ColumnInfo("timestamp")
    var timestamp: String? = null // Nullable as it might not be available initially
)
