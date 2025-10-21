package com.example.spacexlaunches.data.databases

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "launches")
data class LaunchEntity(
    @PrimaryKey
    @ColumnInfo(name = "id")
    val id: String,

    @ColumnInfo(name = "name")
    val name: String,

    @ColumnInfo(name = "date_utc")
    val dateUtc: String,

    @ColumnInfo(name = "success")
    val success: Boolean?,

    @ColumnInfo(name = "rocket_id")
    val rocketId: String,

    @ColumnInfo(name = "rocket_type")
    val rocketType: String? = null,

    @ColumnInfo(name = "details")
    val details: String?,

    @ColumnInfo(name = "flight_number")
    val flightNumber: Int,

    @ColumnInfo(name = "upcoming")
    val upcoming: Boolean
)
