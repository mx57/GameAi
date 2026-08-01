package com.example.data.db

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "stories")
data class StoryEntity(
    @PrimaryKey val id: String,
    val title: String,
    val genreName: String,
    val description: String,
    val loreSummary: String,
    val specialMechanicName: String,
    val primaryCharacterName: String,
    val primaryCharacterRole: String,
    val initialMessage: String,
    val initialChoicesJson: String, // Comma or JSON separated
    val defaultImagePrompt: String,
    val isCustom: Boolean = false,
    val authorName: String = "Realm AI",
    val playCount: Int = 0,
    val rating: Float = 4.9f
)

@Entity(tableName = "chat_messages")
data class ChatMessageEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val storyId: String,
    val sender: String,
    val senderName: String,
    val text: String,
    val choicesJson: String,
    val imageUrl: String? = null,
    val timestamp: Long = System.currentTimeMillis(),
    val statChanges: String? = null
)

@Entity(tableName = "character_states")
data class CharacterStateEntity(
    @PrimaryKey val storyId: String,
    val playerLevel: Int = 1,
    val playerXp: Int = 0,
    val hp: Int = 100,
    val maxHp: Int = 100,
    val energy: Int = 50,
    val maxEnergy: Int = 50,
    val intelligence: Int = 10,
    val charisma: Int = 10,
    val combat: Int = 10,
    val luck: Int = 10,
    val credsOrGold: Int = 250,
    val inventoryJson: String = "Связи в подполье,Карманный хак-терминал",
    val affinitiesJson: String = "" // Character relationships JSON
)

@Entity(tableName = "achievements")
data class AchievementEntity(
    @PrimaryKey val id: String,
    val titleRu: String,
    val descriptionRu: String,
    val iconName: String,
    val xpReward: Int,
    val isUnlocked: Boolean = false,
    val unlockedAt: Long? = null
)

@Entity(tableName = "social_posts")
data class SocialPostEntity(
    @PrimaryKey val id: String,
    val authorName: String,
    val authorAvatar: String,
    val storyTitle: String,
    val postContent: String,
    val likesCount: Int = 12,
    val commentsCount: Int = 4,
    val choiceHighlight: String? = null,
    val timestamp: Long = System.currentTimeMillis(),
    val isLikedByMe: Boolean = false
)
