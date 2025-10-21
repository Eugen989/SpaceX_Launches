package com.example.spacexlaunches.data.paging

import androidx.paging.ExperimentalPagingApi
import androidx.paging.LoadType
import androidx.paging.PagingState
import androidx.paging.RemoteMediator
import androidx.room.withTransaction
import com.example.spacexlaunches.data.databases.LaunchEntity
import com.example.spacexlaunches.data.databases.MainDatabase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import retrofit2.HttpException
import java.io.IOException

@OptIn(ExperimentalPagingApi::class)
class LaunchesRemoteMediator(
    private val database: MainDatabase,
    private val apiService: com.example.spacexlaunches.data.ApiService
) : RemoteMediator<Int, LaunchEntity>() {

    override suspend fun load(loadType: LoadType, state: PagingState<Int, LaunchEntity>): MediatorResult {
        return try {
            val response = apiService.getSpaceXLaunches()
            val launches = if (response.isSuccessful) {
                response.body() ?: emptyList()
            } else {
                emptyList()
            }

            database.withTransaction {
                if (loadType == LoadType.REFRESH) {
                    database.getDao().clearAllLaunches()
                }

                val launchEntities = mutableListOf<LaunchEntity>()
                launches.forEach { launch ->
                    if (launch.id != null && launch.name != null && launch.dateUtc != null && launch.rocket != null) {
                        val rocketType = getRocketType(launch.rocket)
                        val launchEntity = LaunchEntity(
                            id = launch.id,
                            name = launch.name,
                            dateUtc = launch.dateUtc,
                            success = launch.success,
                            rocketId = launch.rocket,
                            rocketType = rocketType,
                            details = launch.details,
                            flightNumber = launch.flightNumber ?: 0,
                            upcoming = launch.upcoming ?: false
                        )
                        launchEntities.add(launchEntity)
                    }
                }

                if (launchEntities.isNotEmpty()) {
                    database.getDao().insertAllLaunches(launchEntities)
                }
            }

            MediatorResult.Success(endOfPaginationReached = true)
        } catch (e: IOException) {
            MediatorResult.Error(e)
        } catch (e: HttpException) {
            MediatorResult.Error(e)
        }
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