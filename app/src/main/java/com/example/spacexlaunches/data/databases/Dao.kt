package com.example.spacexlaunches.data.databases

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface Dao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertLaunch(launch: LaunchEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAllLaunches(launches: List<LaunchEntity>)

    @Query("SELECT * FROM launches ORDER BY flight_number DESC")
    fun getAllLaunches(): Flow<List<LaunchEntity>>

    @Query("SELECT * FROM launches WHERE id = :id")
    suspend fun getLaunchById(id: String): LaunchEntity?

    @Query("DELETE FROM launches")
    suspend fun clearAllLaunches()
}