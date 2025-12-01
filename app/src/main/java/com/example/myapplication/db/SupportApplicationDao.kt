package com.example.myapplication.db

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface SupportApplicationDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsert(entity: SupportApplicationEntity): Long

    @Query("SELECT * FROM support_applications ORDER BY createdAt DESC")
    suspend fun getAll(): List<SupportApplicationEntity>

    @Query("SELECT * FROM support_applications ORDER BY createdAt DESC LIMIT 1")
    suspend fun getLatest(): SupportApplicationEntity?
}
