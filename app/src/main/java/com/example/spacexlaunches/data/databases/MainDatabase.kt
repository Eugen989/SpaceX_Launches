package com.example.spacexlaunches.data.databases

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(entities = [Item::class], version = 1, exportSchema = false)
abstract class MainDatabase : RoomDatabase() {
    abstract fun getDao(): Dao

    companion object {
        fun getDb(context: Context): MainDatabase {
            return Room.databaseBuilder(
                context.applicationContext,
                MainDatabase::class.java,
                "test.db"
            ).build()
        }
    }
}