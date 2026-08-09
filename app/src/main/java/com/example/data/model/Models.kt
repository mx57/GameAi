package com.example.data.model

import androidx.compose.ui.graphics.Color

enum class WorldGenre(val titleRu: String, val iconName: String) {
    CYBERPUNK("Киберпанк 2088", "memory"),
    DARK_FANTASY("Тёмное фэнтези", "auto_awesome"),
    SCI_FI("Космическая одиссея", "rocket_launch"),
    DETECTIVE("Нуар детектив", "search"),
    POST_APOCALYPSE("Постапокалипсис", "shield_moon"),
    ADULT_18("18+ Эротика & Созлазн", "favorite")
}

data class RpgStats(
    val level: Int = 1,
    val xp: Int = 0,
    val hp: Int = 100,
    val maxHp: Int = 100,
    val manaOrEnergy: Int = 50,
    val maxManaOrEnergy: Int = 50,
    val intelligence: Int = 10,
    val charisma: Int = 10,
    val combat: Int = 10,
    val luck: Int = 10,
    val credsOrGold: Int = 250,
    val inventory: List<String> = listOf("Связи в Подполье", "Стандартный инфо-чип"),
    val activeBuffs: List<String> = listOf("Воля Искателя (+5% XP)")
)

data class CharacterSkill(
    val id: String,
    val name: String,
    val iconName: String,
    val level: Int = 1,
    val maxLevel: Int = 5,
    val description: String,
    val effectPerLevel: String,
    val statRequirement: String? = null
)

data class CharacterProfile(
    val characterId: String,
    val name: String,
    val title: String,
    val role: String,
    val avatarUrl: String? = null,
    val affinityLevel: Int = 75,
    val affinityTitle: String = "Надежный Союзник",
    val level: Int = 3,
    val availableSkillPoints: Int = 2,
    val bio: String = "Опытный проводник по изнанке мира. Всегда находит лазейки там, где другие видят только стены.",
    val quote: String = "\u00abДоверяй только протоколам, но проверяй их сам.\u00bb",
    val skills: List<CharacterSkill> = emptyList()
)

data class World(
    val id: String,
    val title: String,
    val genre: WorldGenre,
    val description: String,
    val loreSummary: String,
    val bgGradientHex: List<String>,
    val specialMechanicName: String,
    val primaryCharacterName: String,
    val primaryCharacterRole: String,
    val initialMessage: String,
    val initialChoices: List<String>,
    val defaultImagePrompt: String,
    val isCustom: Boolean = false,
    val authorName: String = "Realm AI Core"
)

data class StoryChoice(
    val id: String,
    val text: String,
    val statCheck: String? = null,
    val riskLevel: String = "Обычный"
)

data class StoryMessage(
    val id: Long = 0,
    val storyId: String,
    val sender: String,
    val senderName: String,
    val text: String,
    val choices: List<StoryChoice> = emptyList(),
    val imageUrl: String? = null,
    val timestamp: Long = System.currentTimeMillis(),
    val statChanges: String? = null
)

enum class ImageResolution(val label: String, val pixels: String) {
    RES_1K("1K", "1024x1024"),
    RES_2K("2K", "2048x2048"),
    RES_4K("4K", "3840x3840")
}

data class UserProfile(
    val name: String = "Искатель Судьбы",
    val title: String = "Мастер Реальности",
    val level: Int = 12,
    val currentXp: Int = 2840,
    val nextLevelXp: Int = 3500,
    val storiesCompleted: Int = 8,
    val customScenariosCreated: Int = 3,
    val isOnline: Boolean = true,
    val offlineGemmaMode: Boolean = false,
    val useHuggingFaceApi: Boolean = false,
    val selectedResolution: ImageResolution = ImageResolution.RES_2K,
    val isAmbientAudioEnabled: Boolean = true,
    val ambientVolume: Float = 0.35f,
    val allowMatureContent: Boolean = false
)

// GGUF Model information
data class GgufModel(
    val id: String,
    val name: String,
    val description: String,
    val filename: String,
    val sizeInBytes: Long,
    val quant: String,
    val isDownloaded: Boolean = false,
    val localPath: String? = null
)
