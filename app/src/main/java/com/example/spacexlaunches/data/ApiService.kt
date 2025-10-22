package com.example.spacexlaunches.data

import com.example.spacexlaunches.data.models.Launch
import com.example.spacexlaunches.data.models.Rocket
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Path

interface ApiService {
    @GET("launches")
    suspend fun getSpaceXLaunches(): Response<List<Launch>>

    @GET("rockets/{id}")
    suspend fun getRocketById(@Path("id") id: String): Response<Rocket>
}
