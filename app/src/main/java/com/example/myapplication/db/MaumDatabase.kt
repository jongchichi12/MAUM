package com.example.myapplication.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(
    entities = [SupportApplicationEntity::class],
    version = 1,
    exportSchema = false
)
abstract class MaumDatabase : RoomDatabase() {

    abstract fun supportApplicationDao(): SupportApplicationDao

    companion object {
        private const val DB_NAME = "maum.db"

        @Volatile
        private var instance: MaumDatabase? = null

        fun getInstance(context: Context): MaumDatabase {
            return instance ?: synchronized(this) {
                instance ?: Room.databaseBuilder(
                    context.applicationContext,
                    MaumDatabase::class.java,
                    DB_NAME
                ).build().also { instance = it }
            }
        }
    }
}
