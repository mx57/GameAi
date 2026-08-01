package com.example.data.db

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface StoryDao {
    @Query("SELECT * FROM stories")
    fun getAllStories(): Flow<List<StoryEntity>>

    @Query("SELECT * FROM stories WHERE id = :id LIMIT 1")
    suspend fun getStoryById(id: String): StoryEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertStories(stories: List<StoryEntity>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertStory(story: StoryEntity)
}

@Dao
interface ChatMessageDao {
    @Query("SELECT * FROM chat_messages WHERE storyId = :storyId ORDER BY timestamp ASC")
    fun getMessagesForStory(storyId: String): Flow<List<ChatMessageEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMessage(message: ChatMessageEntity)

    @Query("DELETE FROM chat_messages WHERE storyId = :storyId")
    suspend fun clearMessagesForStory(storyId: String)
}

@Dao
interface CharacterStateDao {
    @Query("SELECT * FROM character_states WHERE storyId = :storyId LIMIT 1")
    fun getCharacterState(storyId: String): Flow<CharacterStateEntity?>

    @Query("SELECT * FROM character_states WHERE storyId = :storyId LIMIT 1")
    suspend fun getCharacterStateDirect(storyId: String): CharacterStateEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun saveCharacterState(state: CharacterStateEntity)
}

@Dao
interface AchievementDao {
    @Query("SELECT * FROM achievements")
    fun getAllAchievements(): Flow<List<AchievementEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAchievements(achievements: List<AchievementEntity>)

    @Query("UPDATE achievements SET isUnlocked = 1, unlockedAt = :timestamp WHERE id = :id")
    suspend fun unlockAchievement(id: String, timestamp: Long = System.currentTimeMillis())
}

@Dao
interface SocialPostDao {
    @Query("SELECT * FROM social_posts ORDER BY timestamp DESC")
    fun getAllPosts(): Flow<List<SocialPostEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPosts(posts: List<SocialPostEntity>)

    @Query("UPDATE social_posts SET likesCount = likesCount + 1, isLikedByMe = 1 WHERE id = :id")
    suspend fun likePost(id: String)
}
