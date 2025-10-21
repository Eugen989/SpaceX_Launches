package com.example.spacexlaunches.data

import com.example.spacexlaunches.data.models.Launch
import com.example.spacexlaunches.data.models.Rocket
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Path

interface ApiService {
    @GET("launches")
    fun getSpaceXLaunches(): Call<List<Launch>>

    @GET("rockets/{id}")
    fun getRocketById(@Path("id") id: String): Call<Rocket>
}