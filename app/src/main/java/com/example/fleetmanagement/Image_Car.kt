package com.example.fleetmanagement

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey


@Entity("Fleet_Cars")
@kotlinx.serialization.Serializable
data class Image_Car(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo("image_path")
    val image_path : Int?=null,
    @ColumnInfo("car_name")
    val car_name : String?=null,
    @ColumnInfo("company_name")
    val company_name : String?=null,
)