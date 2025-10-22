package com.example.spacexlaunches.data.repository

import android.util.Log
import com.example.spacexlaunches.data.MainDatabase
import com.example.spacexlaunches.data.models.LaunchEntity
import com.example.spacexlaunches.data.models.Launch
import com.example.spacexlaunches.data.models.Rocket
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import retrofit2.HttpException
import java.io.IOException

class LaunchRepository(
    private val database: MainDatabase,
    private val apiService: com.example.spacexlaunches.data.ApiService
) {

    suspend fun loadLaunchesFromApi(): Boolean {
        return try {
            Log.d("LaunchRepository", "Loading launches from API")

            val response = apiService.getSpaceXLaunches()

            if (response.isSuccessful) {
                val launches = response.body() ?: emptyList()

                Log.d("LaunchRepository", "Received ${launches.size} launches from API")

                val launchEntities = mutableListOf<LaunchEntity>()
                for (launch in launches) {
                    if (isValidLaunch(launch)) {
                        val rocketType = getRocketType(launch.rocket!!)
                        val rocketDetails = getRocketDetails(launch.rocket!!)

                        val launchEntity = LaunchEntity(
                            id = launch.id!!,
                            name = launch.name!!,
                            dateUtc = launch.dateUtc!!,
                            success = launch.success,
                            rocketId = launch.rocket!!,
                            rocketType = rocketType,
                            details = launch.details,
                            flightNumber = launch.flightNumber ?: 0,
                            upcoming = launch.upcoming ?: false,
                            rocketName = rocketDetails?.name,
                            rocketCompany = rocketDetails?.company,
                            rocketCountry = rocketDetails?.country,
                            rocketDescription = rocketDetails?.description,
                            rocketImages = rocketDetails?.flickrImages?.firstOrNull(),
                            rocketWikipedia = rocketDetails?.wikipedia,
                            rocketActive = rocketDetails?.active,
                            rocketStages = rocketDetails?.stages,
                            rocketCostPerLaunch = rocketDetails?.costPerLaunch,
                            rocketSuccessRate = rocketDetails?.successRatePct
                        )
                        launchEntities.add(launchEntity)
                    }
                }

                if (launchEntities.isNotEmpty()) {
                    database.getDao().insertAllLaunches(launchEntities)

                    Log.d("LaunchRepository", "Saved ${launchEntities.size} launches to database")

                    true
                } else {
                    Log.w("LaunchRepository", "No valid launches to save")
                    false
                }
            } else {
                Log.e("LaunchRepository", "API error: ${response.code()}")
                false
            }
        } catch (e: Exception) {
            Log.e("LaunchRepository", "Error loading launches: ${e.message}")
            false
        }
    }

    private fun isValidLaunch(launch: Launch): Boolean {
        return launch.id != null &&
                launch.name != null &&
                launch.dateUtc != null &&
                launch.rocket != null
    }

    private suspend fun getRocketType(rocketId: String): String? {
        return try {
            val response = apiService.getRocketById(rocketId)
            if (response.isSuccessful) {
                response.body()?.type
            } else {
                null
            }
        } catch (e: Exception) {
            null
        }
    }

    private suspend fun getRocketDetails(rocketId: String): Rocket? {
        return try {
            val response = apiService.getRocketById(rocketId)
            if (response.isSuccessful) {
                response.body()
            } else {
                null
            }
        } catch (e: Exception) {
            null
        }
    }
}