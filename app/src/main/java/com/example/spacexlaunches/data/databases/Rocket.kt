package com.example.spacexlaunches.data.models

import com.google.gson.annotations.SerializedName

data class Rocket(
    @SerializedName("id")
    val id: String,

    @SerializedName("name")
    val name: String,

    @SerializedName("type")
    val type: String,

    @SerializedName("company")
    val company: String,

    @SerializedName("country")
    val country: String,

    @SerializedName("description")
    val description: String
)