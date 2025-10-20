package com.example.spacexlaunches.data.repository

import com.example.spacexlaunches.data.databases.Item
import com.example.spacexlaunches.data.databases.MainDatabase
import kotlinx.coroutines.flow.Flow

class Repository(private val database: MainDatabase) {

    fun getAllItems(): Flow<List<Item>> {
        return database.getDao().getAllItems()
    }

    suspend fun insertItem(item: Item) {
        database.getDao().insertItem(item)
    }

    suspend fun insertSampleData() {
        val sampleItems = listOf(
            Item(name = "Rocket Model", price = 299),
            Item(name = "Space Suit", price = 599),
            Item(name = "Launch Ticket", price = 999),
            Item(name = "Mission Patch", price = 49),
            Item(name = "Space Food", price = 25)
        )

        sampleItems.forEach { item ->
            insertItem(item)
        }
    }
}