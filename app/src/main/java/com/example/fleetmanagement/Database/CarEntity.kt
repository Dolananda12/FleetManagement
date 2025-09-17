package com.example.fleetmanagement.Database

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity("Fleet_Cars")
@kotlinx.serialization.Serializable
data class CarEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0 ,
    @ColumnInfo("pitch")
    val pitch: Double?,
    @ColumnInfo("datetime")
    var datetime:String? = null,
    @ColumnInfo("yaw")
    var yaw: Double? = null,
    @ColumnInfo("roll")
    var roll: Double? = null,
    @ColumnInfo("latitude")
    var latitude: Double? = null,
    @ColumnInfo("longitude")
    var longitude: Double? = null,
    @ColumnInfo("accel_x")
    var accel_x : Double? = null,
    @ColumnInfo("accel_y")
    var accel_y: Double? = null ,
    @ColumnInfo("accel_z")
    var accel_z: Double? = null,
    @ColumnInfo("timestamp")
    var timestamp: Double? = null
)
