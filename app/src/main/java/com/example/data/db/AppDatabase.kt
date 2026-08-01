package com.example.data.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(
    entities = [
        StoryEntity::class,
        ChatMessageEntity::class,
        CharacterStateEntity::class,
        AchievementEntity::class,
        SocialPostEntity::class
    ],
    version = 1,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun storyDao(): StoryDao
    abstract fun chatMessageDao(): ChatMessageDao
    abstract fun characterStateDao(): CharacterStateDao
    abstract fun achievementDao(): AchievementDao
    abstract fun socialPostDao(): SocialPostDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "realm_story_chat.db"
                ).fallbackToDestructiveMigration()
                .build()
                INSTANCE = instance
                instance
            }
        }
    }
}
