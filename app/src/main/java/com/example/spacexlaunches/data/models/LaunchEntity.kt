package com.example.spacexlaunches.data.models

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import java.io.Serializable

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
    val upcoming: Boolean,

    @ColumnInfo(name = "rocket_name")
    val rocketName: String? = null,

    @ColumnInfo(name = "rocket_company")
    val rocketCompany: String? = null,

    @ColumnInfo(name = "rocket_country")
    val rocketCountry: String? = null,

    @ColumnInfo(name = "rocket_description")
    val rocketDescription: String? = null,

    @ColumnInfo(name = "rocket_images")
    val rocketImages: String? = null,

    @ColumnInfo(name = "rocket_wikipedia")
    val rocketWikipedia: String? = null,

    @ColumnInfo(name = "rocket_active")
    val rocketActive: Boolean? = null,

    @ColumnInfo(name = "rocket_stages")
    val rocketStages: Int? = null,

    @ColumnInfo(name = "rocket_cost_per_launch")
    val rocketCostPerLaunch: Int? = null,

    @ColumnInfo(name = "rocket_success_rate")
    val rocketSuccessRate: Int? = null
) : Serializable