package com.example.spacexlaunches.data.paging

import android.util.Log
import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.example.spacexlaunches.data.databases.LaunchEntity
import com.example.spacexlaunches.data.models.Launch
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class LaunchesPagingSource(
    private val apiService: com.example.spacexlaunches.data.ApiService,
    private val database: com.example.spacexlaunches.data.databases.MainDatabase,
    private val onDataSourceChanged: (String) -> Unit
) : PagingSource<Int, LaunchEntity>() {

    companion object {
        private const val PAGE_SIZE = 10
    }

    override fun getRefreshKey(state: PagingState<Int, LaunchEntity>): Int? {
        return state.anchorPosition?.let { anchorPosition ->
            state.closestPageToPosition(anchorPosition)?.prevKey?.plus(1)
                ?: state.closestPageToPosition(anchorPosition)?.nextKey?.minus(1)
        }
    }

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, LaunchEntity> {
        return try {
            val page = params.key ?: 0
            val pageSize = params.loadSize

            Log.d("PagingDebug", "Loading page: $page, size: $pageSize")

            val launchesFromApi = loadFromApi()

            if (launchesFromApi.isNotEmpty()) {
                onDataSourceChanged("API")
                return loadPageFromList(launchesFromApi, page, pageSize)
            } else {
                onDataSourceChanged("DATABASE")
                return loadFromDatabase(page, pageSize)
            }

        } catch (exception: Exception) {
            Log.e("PagingDebug", "Error loading page: ${exception.message}")

            val page = params.key ?: 0
            val pageSize = params.loadSize

            loadFromDatabase(page, pageSize)
        }
    }

    private suspend fun loadFromApi(): List<Launch> {
        return try {
            val response = apiService.getSpaceXLaunches()
            if (response.isSuccessful) {
                response.body()?.sortedByDescending { it.flightNumber } ?: emptyList()
            } else {
                emptyList()
            }
        } catch (e: Exception) {
            emptyList()
        }
    }

    private suspend fun loadPageFromList(
        allLaunches: List<Launch>,
        page: Int,
        pageSize: Int
    ): LoadResult<Int, LaunchEntity> {
        val start = page * pageSize
        if (start >= allLaunches.size) {
            return LoadResult.Page(
                data = emptyList(),
                prevKey = if (page > 0) page - 1 else null,
                nextKey = null
            )
        }

        val end = (start + pageSize).coerceAtMost(allLaunches.size)
        val pageLaunches = allLaunches.subList(start, end)

        Log.d("PagingDebug", "Loading items $start to $end from ${allLaunches.size} total launches")

        val launchEntities = convertLaunchesToEntities(pageLaunches)

        if (launchEntities.isNotEmpty()) {
            withContext(Dispatchers.IO) {
                database.getDao().insertAllLaunches(launchEntities)
            }
        }

        val nextKey = if (end < allLaunches.size) page + 1 else null
        val prevKey = if (page > 0) page - 1 else null

        Log.d("PagingDebug", "Next page: $nextKey, Prev page: $prevKey")

        return LoadResult.Page(
            data = launchEntities,
            prevKey = prevKey,
            nextKey = nextKey
        )
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

    private suspend fun loadFromDatabase(page: Int, pageSize: Int): LoadResult<Int, LaunchEntity> {
        return try {
            val offset = page * pageSize
            val launches = database.getDao().getPagedLaunches(offset, pageSize)

            Log.d("PagingDebug", "Loaded ${launches.size} launches from database for page $page (offset: $offset)")

            val nextKey = if (launches.size == pageSize) page + 1 else null
            val prevKey = if (page > 0) page - 1 else null

            LoadResult.Page(
                data = launches,
                prevKey = prevKey,
                nextKey = nextKey
            )
        } catch (e: Exception) {
            Log.e("PagingDebug", "Error loading from database: ${e.message}")

            LoadResult.Error(e)
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
}