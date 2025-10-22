package com.example.spacexlaunches.data // Измените пакет

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import com.example.spacexlaunches.data.databases.Dao
import com.example.spacexlaunches.data.models.LaunchEntity

@Database(entities = [LaunchEntity::class], version = 3, exportSchema = false)
abstract class MainDatabase : RoomDatabase() {
    abstract fun getDao(): Dao

    companion object {
        @Volatile
        private var INSTANCE: MainDatabase? = null

        private val MIGRATION_1_2 = object : Migration(1, 2) {
            override fun migrate(database: SupportSQLiteDatabase) {
                database.execSQL("ALTER TABLE launches ADD COLUMN rocket_name TEXT")
                database.execSQL("ALTER TABLE launches ADD COLUMN rocket_company TEXT")
                database.execSQL("ALTER TABLE launches ADD COLUMN rocket_country TEXT")
                database.execSQL("ALTER TABLE launches ADD COLUMN rocket_description TEXT")
                database.execSQL("ALTER TABLE launches ADD COLUMN rocket_images TEXT")
                database.execSQL("ALTER TABLE launches ADD COLUMN rocket_wikipedia TEXT")
                database.execSQL("ALTER TABLE launches ADD COLUMN rocket_active INTEGER")
                database.execSQL("ALTER TABLE launches ADD COLUMN rocket_stages INTEGER")
                database.execSQL("ALTER TABLE launches ADD COLUMN rocket_cost_per_launch INTEGER")
                database.execSQL("ALTER TABLE launches ADD COLUMN rocket_success_rate INTEGER")
            }
        }


        fun getDb(context: Context): MainDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    MainDatabase::class.java,
                    "spacex_launches.db"
                )
                    .addMigrations(MIGRATION_1_2)
                    .fallbackToDestructiveMigration()
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }
}