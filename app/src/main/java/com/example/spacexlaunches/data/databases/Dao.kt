package com.example.spacexlaunches.data.databases

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface Dao {
    @Insert
    fun insertItem(item: Item)

    @Query("SELECT * FROM items")
    fun getAllItems(): Flow<List<Item>>
}