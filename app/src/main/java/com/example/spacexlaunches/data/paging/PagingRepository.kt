package com.example.spacexlaunches.data.repository

import android.content.Context
import android.util.Log
import com.example.spacexlaunches.data.MainDatabase
import com.example.spacexlaunches.data.models.LaunchEntity
import com.example.spacexlaunches.data.models.Launch
import com.example.spacexlaunches.data.models.Rocket
import com.example.spacexlaunches.utils.NetworkUtils
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class PaginationRepository(
    private val database: MainDatabase,
    private val apiService: com.example.spacexlaunches.data.ApiService
) {
    private var allLaunches: List<LaunchEntity> = emptyList()
    private val pageSize = 10

    suspend fun loadAllLaunches(): Boolean {
        return try {
            Log.d("PaginationRepo", "Loading all launches from API")

            val response = apiService.getSpaceXLaunches()

            if (response.isSuccessful) {
                val launches = response.body() ?: emptyList()

                Log.d("PaginationRepo", "Received ${launches.size} launches from API")

                val launchEntities = convertLaunchesToEntities(launches)

                allLaunches = launchEntities.sortedByDescending { it.flightNumber }

                withContext(Dispatchers.IO) {
                    database.getDao().insertAllLaunches(launchEntities)
                }

                Log.d("PaginationRepo", "Loaded ${allLaunches.size} launches into memory")

                true
            } else {
                Log.e("PaginationRepo", "API error: ${response.code()}")

                loadFromDatabase()
            }
        } catch (e: Exception) {
            Log.e("PaginationRepo", "Error loading launches: ${e.message}")

            loadFromDatabase()
        }
    }

    private suspend fun loadFromDatabase(): Boolean {
        return try {
            val launches = database.getDao().getAllLaunchesList()
            allLaunches = launches.sortedByDescending { it.flightNumber }

            Log.d("PaginationRepo", "Loaded ${allLaunches.size} launches from database")

            allLaunches.isNotEmpty()
        } catch (e: Exception) {
            Log.e("PaginationRepo", "Error loading from database: ${e.message}")
            false
        }
    }

    fun getPage(page: Int): List<LaunchEntity> {
        val start = (page - 1) * pageSize
        if (start >= allLaunches.size) {
            return emptyList()
        }
        val end = start + pageSize.coerceAtMost(allLaunches.size - start)
        return allLaunches.subList(start, end)
    }

    fun getTotalPages(): Int {
        return (allLaunches.size + pageSize - 1) / pageSize
    }

    fun getTotalItems(): Int {
        return allLaunches.size
    }

    fun getCurrentPageInfo(page: Int): String {
        val totalPages = getTotalPages()
        val startItem = (page - 1) * pageSize + 1
        val endItem = (page * pageSize).coerceAtMost(allLaunches.size)
        return "Showing $startItem-$endItem of ${allLaunches.size} launches"
    }

    private suspend fun convertLaunchesToEntities(launches: List<Launch>): List<LaunchEntity> {
        val launchEntities = mutableListOf<LaunchEntity>()

        for (launch in launches) {
            if (isValidLaunch(launch)) {
                val rocket = getRocketDetails(launch.rocket!!)
                val launchEntity = LaunchEntity(
                    id = launch.id!!,
                    name = launch.name!!,
                    dateUtc = launch.dateUtc!!,
                    success = launch.success,
                    rocketId = launch.rocket!!,
                    rocketType = rocket?.type,
                    details = launch.details,
                    flightNumber = launch.flightNumber ?: 0,
                    upcoming = launch.upcoming ?: false,
                    rocketName = rocket?.name,
                    rocketCompany = rocket?.company,
                    rocketCountry = rocket?.country,
                    rocketDescription = rocket?.description,
                    rocketImages = rocket?.flickrImages?.firstOrNull(),
                    rocketWikipedia = rocket?.wikipedia,
                    rocketActive = rocket?.active,
                    rocketStages = rocket?.stages,
                    rocketCostPerLaunch = rocket?.costPerLaunch,
                    rocketSuccessRate = rocket?.successRatePct
                )
                launchEntities.add(launchEntity)
            }
        }

        return launchEntities
    }

    private fun isValidLaunch(launch: Launch): Boolean {
        return launch.id != null &&
                launch.name != null &&
                launch.dateUtc != null &&
                launch.rocket != null
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

    fun getAllLaunches(): List<LaunchEntity> {
        return allLaunches
    }
    interface JavaCallback<T> {
        fun onResult(result: T)
    }

    @JvmOverloads
    fun loadAllLaunchesJava(callback: JavaCallback<Boolean>) {
        CoroutineScope(Dispatchers.IO).launch {
            val success = loadAllLaunches()
            callback.onResult(success)
        }
    }

    @JvmOverloads
    fun getAllLaunchesJava(callback: JavaCallback<List<LaunchEntity>>) {
        CoroutineScope(Dispatchers.IO).launch {
            val launches = getAllLaunches()
            callback.onResult(launches)
        }
    }

    @JvmOverloads
    fun loadDataWithNetworkCheck(context: Context, callback: JavaCallback<Boolean>) {
        CoroutineScope(Dispatchers.IO).launch {
            val success = if (NetworkUtils.isInternetAvailable(context)) {
                loadAllLaunches()
            } else {
                getAllLaunches().isNotEmpty()
            }
            callback.onResult(success)
        }
    }
}
