package com.approagency.pharmacy.data.local.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.approagency.pharmacy.data.local.dao.TestGroupDao
import com.approagency.pharmacy.data.local.dao.TestItemDao
import com.approagency.pharmacy.data.local.entities.TestGroupEntity
import com.approagency.pharmacy.data.local.entities.TestItemEntity

@Database(
    entities = [
        TestGroupEntity::class,
        TestItemEntity::class
    ],
    version = 1,
    exportSchema = false
)
abstract class LabDatabase : RoomDatabase() {
    abstract fun testGroupDao(): TestGroupDao
    abstract fun testItemDao(): TestItemDao

    companion object {
        @Volatile
        private var INSTANCE: LabDatabase? = null

        fun getInstance(context: Context): LabDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    LabDatabase::class.java,
                    "lab_tests.db"  // This will use your existing database
                )
                    .createFromAsset("lab_tests.db")  // IMPORTANT: Copy from assets
                    .fallbackToDestructiveMigration()  // For development only
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }
}