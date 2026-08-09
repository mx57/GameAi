package com.example.viewmodel

import android.app.Application
import android.content.Context
import android.widget.Toast
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.db.AppDatabase
import com.example.data.model.*
import com.example.data.repository.StoryRepository
import com.example.util.AmbientAudioEngine
import com.example.util.AmbientPreset
import com.example.util.StoryExporter
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

class StoryViewModel(application: Application) : AndroidViewModel(application) {

    private val repository = StoryRepository(AppDatabase.getDatabase(application))

    val allStories: StateFlow<List<World>> = repository.allStories.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = emptyList()
    )

    private val _selectedWorld = MutableStateFlow<World?>(null)
    val selectedWorld: StateFlow<World?> = _selectedWorld.asStateFlow()

    private val _messages = MutableStateFlow<List<StoryMessage>>(emptyList())
    val messages: StateFlow<List<StoryMessage>> = _messages.asStateFlow()

    private val _rpgStats = MutableStateFlow(RpgStats())
    val rpgStats: StateFlow<RpgStats> = _rpgStats.asStateFlow()

    private val _characterProfile = MutableStateFlow<CharacterProfile?>(null)
    val characterProfile: StateFlow<CharacterProfile?> = _characterProfile.asStateFlow()

    private val _userProfile = MutableStateFlow(UserProfile())
    val userProfile: StateFlow<UserProfile> = _userProfile.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    val achievements = repository.achievements.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = emptyList()
    )

    val socialPosts = repository.socialPosts.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = emptyList()
    )

    // Multiplayer Room State
    private val _multiplayerRoomCode = MutableStateFlow<String?>(null)
    val multiplayerRoomCode: StateFlow<String?> = _multiplayerRoomCode.asStateFlow()

    private val _activeRoomPlayers = MutableStateFlow<List<String>>(listOf("Вы (Лидер)", "Искатель_99", "Alex_Cyber"))
    val activeRoomPlayers: StateFlow<List<String>> = _activeRoomPlayers.asStateFlow()

    init {
        viewModelScope.launch {
            repository.initializeDefaultDataIfEmpty()
        }
    }

    fun selectWorld(world: World) {
        _selectedWorld.value = world

        // Sync ambient audio preset to world genre
        AmbientAudioEngine.setGenrePreset(world.genre)
        if (_userProfile.value.isAmbientAudioEnabled && !AmbientAudioEngine.isPlaying) {
            AmbientAudioEngine.startAmbient()
        }

        // Initialize Character Profile with Skills
        _characterProfile.value = generateCharacterProfileForWorld(world)

        viewModelScope.launch {
            repository.getMessagesForStory(world.id).collect { msgs ->
                if (msgs.isEmpty()) {
                    val initMsg = repository.resetAndStartStory(world)
                    _messages.value = listOf(initMsg)
                } else {
                    _messages.value = msgs
                }
            }
        }
        viewModelScope.launch {
            repository.getCharacterState(world.id).collect { stats ->
                _rpgStats.value = stats
            }
        }
    }

    private fun generateCharacterProfileForWorld(world: World): CharacterProfile {
        val skills = when (world.genre) {
            WorldGenre.CYBERPUNK -> listOf(
                CharacterSkill("sk_1", "Взлом Сетей (Hacking)", "memory", 2, 5, "Быстрое проникновение в терминалы и отключение охранных систем.", "+15% к успешному взлому за уровень"),
                CharacterSkill("sk_2", "Оптический Стелс", "visibility_off", 1, 5, "Мгновенное укрытие в тенях цифрового города.", "+10% к маскировке и избеганию боев"),
                CharacterSkill("sk_3", "Нейро-Оверклок", "bolt", 1, 5, "Перегрузка имплантов для повторного хода в диалоге.", "+1 доп. перевыбор варианта ответа")
            )
            WorldGenre.DARK_FANTASY -> listOf(
                CharacterSkill("sk_1", "Магический Щит Рун", "shield", 2, 5, "Защитный барьер от темных заклинаний и ловушек.", "+20 HP барьера в боях"),
                CharacterSkill("sk_2", "Зрение Древних", "auto_awesome", 1, 5, "Распознавание иллюзий, проклятий и тайных знаков.", "Открывает скрытые опции диалога"),
                CharacterSkill("sk_3", "Огненная Руна", "flare", 1, 5, "Взрывная магия против монстров и стражников.", "+25 к боевому урону")
            )
            WorldGenre.SCI_FI -> listOf(
                CharacterSkill("sk_1", "Грави-Шок", "rocket_launch", 1, 5, "Импульс гравитации для отражения атак.", "+15 к Бою в космосе"),
                CharacterSkill("sk_2", "Ксено-Лингвистика", "psychology", 2, 5, "Понимание языков чужих рас и древних инопланетных артефактов.", "Снижает сложность проверок Харизмы"),
                CharacterSkill("sk_3", "Ремонт Двигателя", "build", 1, 5, "Экспресс-восстановление систем корабля.", "+30% к шансу выживания при аварии")
            )
            else -> listOf(
                CharacterSkill("sk_1", "Харизматичное Доверие", "psychology", 2, 5, "Легкое убеждение ключевых персонажей в диалоге.", "+20% к вероятности успеха убеждения"),
                CharacterSkill("sk_2", "Интуиция Искателя", "search", 1, 5, "Чутье на спрятанные предметы и тайные ходы.", "Подсвечивает опасные варианты выбора"),
                CharacterSkill("sk_3", "Боевое Мастерство", "military_tech", 1, 5, "Точный навык самообороны в экстремальных ситуациях.", "+10 к урону и защите")
            )
        }

        return CharacterProfile(
            characterId = "char_${world.id}",
            name = world.primaryCharacterName,
            title = world.primaryCharacterRole,
            role = world.genre.titleRu,
            avatarUrl = null,
            affinityLevel = 80,
            affinityTitle = "Верный Проводник",
            level = 3,
            availableSkillPoints = 2,
            bio = "Древний или современный спутник, связавший свою судьбу с вашей. Их навыки открывают уникальные развилки сюжетных событий.",
            quote = "«В этом мире побеждает тот, кто адаптирует свои навыки раньше, чем сработает ловушка.»",
            skills = skills
        )
    }

    fun upgradeCharacterSkill(skillId: String) {
        val currentProfile = _characterProfile.value ?: return
        if (currentProfile.availableSkillPoints <= 0) return

        val updatedSkills = currentProfile.skills.map { skill ->
            if (skill.id == skillId && skill.level < skill.maxLevel) {
                skill.copy(level = skill.level + 1)
            } else {
                skill
            }
        }

        _characterProfile.value = currentProfile.copy(
            availableSkillPoints = currentProfile.availableSkillPoints - 1,
            skills = updatedSkills
        )
    }

    fun trainCharacterForSkillPoint() {
        val currentProfile = _characterProfile.value ?: return
        _characterProfile.value = currentProfile.copy(
            level = currentProfile.level + 1,
            availableSkillPoints = currentProfile.availableSkillPoints + 1,
            affinityLevel = (currentProfile.affinityLevel + 5).coerceAtMost(100)
        )
    }

    fun resetStory() {
        val world = _selectedWorld.value ?: return
        viewModelScope.launch {
            _isLoading.value = true
            val initMsg = repository.resetAndStartStory(world)
            _messages.value = listOf(initMsg)
            _isLoading.value = false
        }
    }

    fun sendChoice(choiceText: String) {
        val world = _selectedWorld.value ?: return
        if (_isLoading.value) return

        viewModelScope.launch {
            _isLoading.value = true
            try {
                repository.sendUserChoice(
                    storyId = world.id,
                    userMessage = choiceText,
                    world = world,
                    isOfflineMode = _userProfile.value.offlineGemmaMode,
                    useHuggingFace = _userProfile.value.useHuggingFaceApi
                )
            } catch (e: Exception) {
                e.printStackTrace()
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun toggleOfflineMode(enabled: Boolean) {
        _userProfile.value = _userProfile.value.copy(offlineGemmaMode = enabled)
    }

    fun toggleHuggingFaceApi(enabled: Boolean) {
        _userProfile.value = _userProfile.value.copy(useHuggingFaceApi = enabled)
    }

    fun toggleMatureContent(enabled: Boolean) {
        _userProfile.value = _userProfile.value.copy(allowMatureContent = enabled)
    }

    fun toggleAmbientAudio() {
        val newStatus = !_userProfile.value.isAmbientAudioEnabled
        _userProfile.value = _userProfile.value.copy(isAmbientAudioEnabled = newStatus)
        if (newStatus) {
            AmbientAudioEngine.startAmbient()
        } else {
            AmbientAudioEngine.stopAmbient()
        }
    }

    fun setAmbientVolume(vol: Float) {
        _userProfile.value = _userProfile.value.copy(ambientVolume = vol)
        AmbientAudioEngine.volume = vol
    }

    fun setAmbientPreset(preset: AmbientPreset) {
        AmbientAudioEngine.startAmbient(preset)
    }

    fun setImageResolution(res: ImageResolution) {
        _userProfile.value = _userProfile.value.copy(selectedResolution = res)
    }

    fun createCustomWorld(
        title: String,
        genre: WorldGenre,
        description: String,
        lore: String,
        characterName: String,
        role: String,
        initialMsg: String,
        choices: List<String>,
        imagePrompt: String,
        onCreated: (World) -> Unit
    ) {
        viewModelScope.launch {
            val newWorld = repository.createCustomWorld(
                title, genre, description, lore, characterName, role, initialMsg, choices, imagePrompt
            )
            onCreated(newWorld)
        }
    }

    fun createMultiplayerRoom(worldTitle: String) {
        val randomCode = "REALM-" + (1000..9999).random()
        _multiplayerRoomCode.value = randomCode
        _activeRoomPlayers.value = listOf("Вы (Хост)", "Кибер-Странник", "Маг_Зари")
    }

    fun joinMultiplayerRoom(code: String) {
        _multiplayerRoomCode.value = code.uppercase()
        _activeRoomPlayers.value = listOf("Вы (Подключен)", "ХостКомнаты", "Елена_1")
    }

    fun publishPost(storyTitle: String, postText: String, choiceHighlight: String) {
        viewModelScope.launch {
            repository.publishPost(storyTitle, postText, choiceHighlight)
        }
    }

    fun likePost(postId: String) {
        viewModelScope.launch {
            repository.likePost(postId)
        }
    }

    fun exportCurrentStory(context: Context, isHtmlEpub: Boolean) {
        val world = _selectedWorld.value ?: return
        val currentMsgs = _messages.value
        val file = if (isHtmlEpub) {
            StoryExporter.exportStoryToEpubHtml(context, world, currentMsgs)
        } else {
            StoryExporter.exportStoryToTextPdf(context, world, currentMsgs)
        }

        if (file != null) {
            Toast.makeText(context, "Файл успешно сохранен: ${file.name}", Toast.LENGTH_LONG).show()
        } else {
            Toast.makeText(context, "Ошибка экспорта файла", Toast.LENGTH_SHORT).show()
        }
    }
}
