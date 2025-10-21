package com.example.spacexlaunches.data.repository

import android.util.Log
import com.example.spacexlaunches.data.databases.LaunchEntity
import com.example.spacexlaunches.data.databases.MainDatabase
import com.example.spacexlaunches.data.models.Launch
import kotlinx.coroutines.Dispatchers
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
                val rocketType = getRocketType(launch.rocket!!)
                val launchEntity = LaunchEntity(
                    id = launch.id!!,
                    name = launch.name!!,
                    dateUtc = launch.dateUtc!!,
                    success = launch.success,
                    rocketId = launch.rocket!!,
                    rocketType = rocketType,
                    details = launch.details,
                    flightNumber = launch.flightNumber ?: 0,
                    upcoming = launch.upcoming ?: false
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
}