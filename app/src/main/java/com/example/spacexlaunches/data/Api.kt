package com.example.spacexlaunches.data

import android.util.Log
import com.example.spacexlaunches.data.models.LaunchEntity
import com.example.spacexlaunches.data.models.Launch
import com.example.spacexlaunches.data.models.Rocket
import com.example.spacexlaunches.utils.Constants
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class Api(private val database: MainDatabase) {

    private val retrofit = Retrofit.Builder()
        .baseUrl(Constants.BASE_URL)
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    private val apiService = retrofit.create(ApiService::class.java)

    fun fetchAndSaveLaunches(onSuccess: (() -> Unit)? = null, onError: (() -> Unit)? = null) {
        Log.d("MyLogApi", "Fetching launches from: ${Constants.BASE_URL}launches")

        CoroutineScope(Dispatchers.IO).launch {
            try {
                val response = apiService.getSpaceXLaunches()

                if (response.isSuccessful) {
                    val launches = response.body()
                    if (launches != null && launches.isNotEmpty()) {
                        Log.d("MyApiLog", "Success! Received ${launches.size} launches")

                        processAndSaveLaunches(launches, onSuccess)
                    } else {
                        Log.d("MyApiLog", "No launches received or empty list")
                        onError?.invoke()
                    }
                } else {
                    Log.d("MyApiLog", "Error in get launches. Code: ${response.code()}")
                    try {
                        Log.d("MyApiLog", "Error body: ${response.errorBody()?.string()}")
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                    onError?.invoke()
                }
            } catch (t: Throwable) {
                Log.d("MyApiLog", "Failure: ${t.message}")

                t.printStackTrace()
                onError?.invoke()
            }
        }
    }

    private suspend fun processAndSaveLaunches(launches: List<Launch>, onSuccess: (() -> Unit)? = null) {
        try {
            val launchEntities = mutableListOf<LaunchEntity>()

            for (launch in launches) {
                if (launch.id != null && launch.name != null && launch.dateUtc != null && launch.rocket != null) {
                    val rocketType = getRocketType(launch.rocket)
                    val rocketDetails = getRocketDetails(launch.rocket)

                    val launchEntity = LaunchEntity(
                        id = launch.id,
                        name = launch.name,
                        dateUtc = launch.dateUtc,
                        success = launch.success,
                        rocketId = launch.rocket,
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
                } else {
                    Log.w("MyApiLog", "Skipping launch due to missing required fields: ${launch.name}")
                }
            }

            if (launchEntities.isNotEmpty()) {
                database.getDao().insertAllLaunches(launchEntities)

                Log.d("MyApiLog", "Saved ${launchEntities.size} launches to database")

                onSuccess?.invoke()
            } else {
                Log.w("MyApiLog", "No valid launches to save")
                onSuccess?.invoke()
            }
        } catch (e: Exception) {
            Log.e("MyApiLog", "Error processing launches: ${e.message}", e)
        }
    }

    private suspend fun getRocketType(rocketId: String): String? {
        return try {
            val response = apiService.getRocketById(rocketId)
            if (response.isSuccessful) {
                response.body()?.type
            } else {
                Log.w("MyApiLog", "Failed to fetch rocket type for ID: $rocketId")
                null
            }
        } catch (e: Exception) {
            Log.e("MyApiLog", "Error fetching rocket type for $rocketId: ${e.message}")
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
            Log.e("MyApiLog", "Error fetching rocket details for $rocketId: ${e.message}")
            null
        }
    }
}
