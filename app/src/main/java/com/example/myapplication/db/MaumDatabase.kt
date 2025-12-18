package com.example.myapplication.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(
    entities = [SupportApplicationEntity::class, ChatMessageEntity::class],
    version = 2,
    exportSchema = false
)
abstract class MaumDatabase : RoomDatabase() {

    abstract fun supportApplicationDao(): SupportApplicationDao
    abstract fun chatMessageDao(): ChatMessageDao

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
                )
                    .addMigrations(MIGRATION_1_2)
                    .build().also { instance = it }
            }
        }

        // 기존 버전에 채팅 테이블 추가
        val MIGRATION_1_2 = object : androidx.room.migration.Migration(1, 2) {
            override fun migrate(database: androidx.sqlite.db.SupportSQLiteDatabase) {
                database.execSQL(
                    """
                    CREATE TABLE IF NOT EXISTS chat_messages(
                        id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
                        role TEXT NOT NULL,
                        content TEXT NOT NULL,
                        created_at INTEGER NOT NULL
                    )
                    """.trimIndent()
                )
                database.execSQL("CREATE INDEX IF NOT EXISTS index_chat_messages_created_at ON chat_messages(created_at)")
            }
        }
    }
}
