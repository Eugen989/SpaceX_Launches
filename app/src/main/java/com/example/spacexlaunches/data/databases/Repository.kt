package com.example.spacexlaunches.data.repository

import com.example.spacexlaunches.data.MainDatabase
import com.example.spacexlaunches.data.models.LaunchEntity
import kotlinx.coroutines.flow.Flow

class Repository(private val database: MainDatabase) {

    fun getAllLaunches(): Flow<List<LaunchEntity>> {
        return database.getDao().getAllLaunches()
    }

    suspend fun insertLaunch(launch: LaunchEntity) {
        database.getDao().insertLaunch(launch)
    }

    suspend fun insertAllLaunches(launches: List<LaunchEntity>) {
        database.getDao().insertAllLaunches(launches)
    }

    suspend fun clearAllLaunches() {
        database.getDao().clearAllLaunches()
    }

    suspend fun hasData(): Boolean {
        val launches = database.getDao().getAllLaunches()
        var hasData = false
        launches.collect { list ->
            hasData = list.isNotEmpty()
        }
        return hasData
    }
}
