package com.example.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.data.model.DocumentEntity
import com.example.data.model.MistakeEntity
import com.example.data.model.QuestionEntity
import com.example.data.model.TestAttemptEntity
import com.example.data.model.TestEntity

@Database(
    entities = [
        QuestionEntity::class,
        DocumentEntity::class,
        TestEntity::class,
        TestAttemptEntity::class,
        MistakeEntity::class
    ],
    version = 1,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun questionDao(): QuestionDao
    abstract fun documentDao(): DocumentDao
    abstract fun testDao(): TestDao
    abstract fun mistakeDao(): MistakeDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getInstance(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "testforge_database"
                )
                    .fallbackToDestructiveMigration(dropAllTables = true)
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }
}
