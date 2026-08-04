package com.example.ui.screens

import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.layout.ContentScale
import coil.compose.SubcomposeAsyncImage
import com.example.data.model.*
import com.example.ui.components.AnimatedTypewriterText
import com.example.ui.components.ChoiceChip
import com.example.ui.components.DarkGlassCard
import com.example.ui.components.FantasyHeaderTitle
import com.example.ui.components.RpgStatBar
import com.example.ui.theme.*
import com.example.util.AmbientAudioEngine
import com.example.util.AmbientPreset
import com.example.viewmodel.StoryViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StoryChatScreen(
    viewModel: StoryViewModel,
    onBackClicked: () -> Unit
) {
    val world by viewModel.selectedWorld.collectAsState()
    val messages by viewModel.messages.collectAsState()
    val rpgStats by viewModel.rpgStats.collectAsState()
    val characterProfile by viewModel.characterProfile.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    val userProfile by viewModel.userProfile.collectAsState()

    val context = LocalContext.current
    val listState = rememberLazyListState()

    var showRpgDrawer by remember { mutableStateOf(false) }
    var showCharacterSheet by remember { mutableStateOf(false) }
    var showAmbientSheet by remember { mutableStateOf(false) }
    var showExportDialog by remember { mutableStateOf(false) }
    var customResponseText by remember { mutableStateOf("") }
    var areChoicesExpanded by remember { mutableStateOf(false) }

    // Auto scroll to last message
    LaunchedEffect(messages.size) {
        if (messages.isNotEmpty()) {
            listState.animateScrollToItem(messages.size - 1)
        }
    }

    if (world == null) {
        Box(modifier = Modifier.fillMaxSize().background(FantasyDarkCanvas))
        return
    }

    Scaffold(
        topBar = {
            Column(modifier = Modifier.background(FantasySurface)) {
                // Top Action Bar
                TopAppBar(
                    title = {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.clickable { showCharacterSheet = true }
                        ) {
                            // Avatar Badge
                            Box(
                                modifier = Modifier
                                    .size(38.dp)
                                    .clip(CircleShape)
                                    .background(
                                        Brush.sweepGradient(listOf(FantasyGold, Color(0xFFFF6F00), FantasyGold))
                                    )
                                    .padding(2.dp)
                                    .clip(CircleShape)
                                    .background(FantasySurfaceVariant)
                                    .testTag("character_avatar_header"),
                                contentAlignment = Alignment.Center
                            ) {
                                Text("👤", fontSize = 18.sp)
                                Surface(
                                    shape = CircleShape,
                                    color = Color(0xFFD84315),
                                    modifier = Modifier
                                        .align(Alignment.BottomCenter)
                                        .offset(y = 2.dp)
                                ) {
                                    Text(
                                        text = "150",
                                        fontSize = 8.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = Color.White,
                                        modifier = Modifier.padding(horizontal = 2.dp)
                                    )
                                }
                            }

                            Spacer(modifier = Modifier.width(10.dp))

                            Column {
                                FantasyHeaderTitle(text = "AI Game Master", fontSize = 18)
                                Text(
                                    text = "${world!!.primaryCharacterName} • Спутник",
                                    fontSize = 10.sp,
                                    color = FantasyGold
                                )
                            }
                        }
                    },
                    navigationIcon = {
                        IconButton(onClick = onBackClicked) {
                            Icon(Icons.Default.ArrowBack, contentDescription = "Назад", tint = FantasyGold)
                        }
                    },
                    actions = {
                        // Ambient Sound Control Button
                        IconButton(
                            onClick = { showAmbientSheet = true },
                            modifier = Modifier.testTag("ambient_audio_button")
                        ) {
                            Icon(
                                imageVector = if (userProfile.isAmbientAudioEnabled) Icons.Default.VolumeUp else Icons.Default.VolumeOff,
                                contentDescription = "Фоновый Эмбиент",
                                tint = if (userProfile.isAmbientAudioEnabled) FantasyGold else TextSecondary
                            )
                        }

                        // Character RPG Inventory Sheet button
                        IconButton(
                            onClick = { showRpgDrawer = true },
                            modifier = Modifier.testTag("rpg_sheet_button")
                        ) {
                            Icon(Icons.Default.Shield, contentDescription = "Статы", tint = FantasyGold)
                        }
                    },
                    colors = TopAppBarDefaults.topAppBarColors(containerColor = FantasySurface)
                )

                // Top Shortcut Inventory Bar (Matching Screenshot 2)
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(
                            Brush.verticalGradient(
                                listOf(FantasySurface, Color(0xFF140F0D))
                            )
                        )
                        .padding(horizontal = 16.dp, vertical = 6.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Left Scroll Badge
                    Surface(
                        shape = RoundedCornerShape(20.dp),
                        color = FantasySurfaceVariant,
                        border = androidx.compose.foundation.BorderStroke(1.dp, FantasyGold)
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text("📜", fontSize = 14.sp)
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("x0", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = FantasyGoldLight)
                        }
                    }

                    // Center Scene Preview Card with Neon Border (Screenshot 2)
                    Box(
                        modifier = Modifier
                            .size(54.dp, 40.dp)
                            .clip(RoundedCornerShape(8.dp))
                            .background(
                                Brush.linearGradient(
                                    listOf(Color(0xFFE91E63), Color(0xFF00E5FF))
                                )
                            )
                            .padding(2.dp)
                            .clip(RoundedCornerShape(6.dp))
                            .background(DarkCanvas)
                            .clickable { showCharacterSheet = true },
                        contentAlignment = Alignment.Center
                    ) {
                        Text("🌃", fontSize = 20.sp)
                    }

                    // Right Satchel & Gold Icons (Screenshot 2)
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Surface(
                            onClick = { showRpgDrawer = true },
                            shape = CircleShape,
                            color = FantasyWoodBrown,
                            border = androidx.compose.foundation.BorderStroke(1.dp, FantasyGold)
                        ) {
                            Text("🎒", fontSize = 16.sp, modifier = Modifier.padding(6.dp))
                        }
                        Spacer(modifier = Modifier.width(8.dp))
                        Surface(
                            onClick = { showRpgDrawer = true },
                            shape = CircleShape,
                            color = FantasyWoodBrown,
                            border = androidx.compose.foundation.BorderStroke(1.dp, FantasyGold)
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text("💰", fontSize = 14.sp)
                                Spacer(modifier = Modifier.width(2.dp))
                                Text("250", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = FantasyGoldLight)
                            }
                        }
                    }
                }
            }
        },
        containerColor = FantasyDarkCanvas
    ) { padding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 12.dp)
            ) {
                Spacer(modifier = Modifier.height(8.dp))

                // Chat Messages Thread with Turn Indicators (Screenshot 2)
                LazyColumn(
                    state = listState,
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(10.dp),
                    contentPadding = PaddingValues(bottom = 16.dp)
                ) {
                    items(messages, key = { it.timestamp.toString() + it.sender + it.text.hashCode() }) { msg ->
                        StoryTurnMessageRow(
                            message = msg,
                            turnIndex = messages.indexOf(msg) + 1,
                            onAvatarClick = { showCharacterSheet = true }
                        )
                    }

                    if (isLoading) {
                        item {
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 12.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    CircularProgressIndicator(
                                        modifier = Modifier.size(20.dp),
                                        color = FantasyGold,
                                        strokeWidth = 2.dp
                                    )
                                    Spacer(modifier = Modifier.width(10.dp))
                                    Text(
                                        text = if (userProfile.offlineGemmaMode) "Gemma моделирует результат..." else "Генерация ответа ИИ...",
                                        fontSize = 12.sp,
                                        color = TextSecondary,
                                        fontWeight = FontWeight.Medium
                                    )
                                }
                            }
                        }
                    }
                }

                // Collapsible Choice Panel / Custom Dialogue Input
                val activeChoices = messages.lastOrNull { it.choices.isNotEmpty() }?.choices ?: emptyList()
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 4.dp)
                ) {
                    if (activeChoices.isNotEmpty() && !isLoading) {
                        Surface(
                            onClick = { areChoicesExpanded = !areChoicesExpanded },
                            shape = RoundedCornerShape(10.dp),
                            color = FantasySurface,
                            border = androidx.compose.foundation.BorderStroke(1.dp, FantasyOutline),
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("toggle_choices_header")
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(horizontal = 12.dp, vertical = 8.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Text(
                                        text = "Варианты действий (${activeChoices.size})",
                                        fontSize = 12.sp,
                                        fontFamily = FontFamily.Serif,
                                        fontWeight = FontWeight.Bold,
                                        color = FantasyGoldLight
                                    )
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Surface(
                                        shape = RoundedCornerShape(10.dp),
                                        color = FantasyWoodBrown,
                                        border = androidx.compose.foundation.BorderStroke(1.dp, FantasyGold)
                                    ) {
                                        Text(
                                            text = if (areChoicesExpanded) "Свернуть ▲" else "Развернуть ▼",
                                            fontSize = 10.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = FantasyGold,
                                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp)
                                        )
                                    }
                                }

                                Icon(
                                    imageVector = if (areChoicesExpanded) Icons.Default.KeyboardArrowUp else Icons.Default.KeyboardArrowDown,
                                    contentDescription = "Переключить видимость вариантов",
                                    tint = FantasyGold,
                                    modifier = Modifier.size(20.dp)
                                )
                            }
                        }

                        AnimatedVisibility(
                            visible = areChoicesExpanded,
                            enter = expandVertically() + fadeIn(),
                            exit = shrinkVertically() + fadeOut()
                        ) {
                            Column(modifier = Modifier.padding(top = 6.dp)) {
                                activeChoices.forEachIndexed { index, choice ->
                                    ChoiceChip(
                                        text = choice.text,
                                        statCheck = choice.statCheck,
                                        riskLevel = choice.riskLevel,
                                        onClick = { 
                                            areChoicesExpanded = false
                                            viewModel.sendChoice(choice.text) 
                                        },
                                        testTagId = "choice_option_$index"
                                    )
                                }
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(6.dp))

                    // Custom response field
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        OutlinedTextField(
                            value = customResponseText,
                            onValueChange = { customResponseText = it },
                            placeholder = { Text("💬 Свой диалог или свободное действие...", fontSize = 12.sp, color = TextMuted) },
                            modifier = Modifier
                                .weight(1f)
                                .testTag("custom_choice_input"),
                            shape = RoundedCornerShape(12.dp),
                            singleLine = true,
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = FantasyGold,
                                unfocusedBorderColor = FantasyOutline,
                                focusedContainerColor = FantasySurface,
                                unfocusedContainerColor = FantasySurface
                            )
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        IconButton(
                            onClick = {
                                if (customResponseText.isNotBlank()) {
                                    val text = customResponseText
                                    customResponseText = ""
                                    areChoicesExpanded = false
                                    viewModel.sendChoice(text)
                                }
                            },
                            enabled = customResponseText.isNotBlank() && !isLoading,
                            modifier = Modifier
                                .size(48.dp)
                                .clip(CircleShape)
                                .background(if (customResponseText.isNotBlank() && !isLoading) FantasyWoodBrown else FantasySurfaceVariant)
                                .testTag("send_custom_choice_button")
                        ) {
                            Icon(Icons.Default.Send, contentDescription = "Отправить", tint = FantasyGold)
                        }
                    }
                }
            }

            // Character Profile & Skills Sheet Modal
            if (showCharacterSheet && characterProfile != null) {
                CharacterProfileModalSheet(
                    profile = characterProfile!!,
                    onUpgradeSkill = { skillId -> viewModel.upgradeCharacterSkill(skillId) },
                    onTrainCharacter = { viewModel.trainCharacterForSkillPoint() },
                    onDismiss = { showCharacterSheet = false }
                )
            }

            // Ambient Sound Controls Modal Sheet
            if (showAmbientSheet) {
                AmbientAudioModalSheet(
                    userProfile = userProfile,
                    onToggleAmbient = { viewModel.toggleAmbientAudio() },
                    onVolumeChange = { viewModel.setAmbientVolume(it) },
                    onPresetSelect = { viewModel.setAmbientPreset(it) },
                    onDismiss = { showAmbientSheet = false }
                )
            }

            // RPG Character Sheet Modal Sheet
            if (showRpgDrawer) {
                RpgCharacterSheetModal(
                    rpgStats = rpgStats,
                    characterName = world!!.primaryCharacterName,
                    role = world!!.primaryCharacterRole,
                    onDismiss = { showRpgDrawer = false }
                )
            }
        }
    }
}

// Turn Message Row with Left Margin Indicators (Screenshot 2)
@Composable
fun StoryTurnMessageRow(
    message: StoryMessage,
    turnIndex: Int,
    onAvatarClick: () -> Unit
) {
    val isUser = message.sender == "USER"

    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.Top
    ) {
        // Left Margin Turn Indicator (Screenshot 2: "👤 Ход 20", "📥 Ход 20")
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.width(52.dp)
        ) {
            Icon(
                imageVector = if (isUser) Icons.Default.Person else Icons.Default.Inbox,
                contentDescription = null,
                tint = TextMuted,
                modifier = Modifier.size(14.dp)
            )
            Text(
                text = "Ход $turnIndex",
                fontSize = 10.sp,
                fontWeight = FontWeight.Bold,
                color = TextMuted
            )
            Icon(
                imageVector = Icons.Default.ArrowDropDown,
                contentDescription = null,
                tint = TextMuted,
                modifier = Modifier.size(16.dp)
            )
        }

        Spacer(modifier = Modifier.width(6.dp))

        // Message Content Bubble
        Column(modifier = Modifier.weight(1f)) {
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                if (!isUser) {
                    Box(
                        modifier = Modifier
                            .size(24.dp)
                            .clip(CircleShape)
                            .background(FantasyWoodBrown)
                            .border(1.dp, FantasyGold, CircleShape)
                            .clickable(onClick = onAvatarClick)
                            .testTag("avatar_chat_bubble"),
                        contentAlignment = Alignment.Center
                    ) {
                        Text("👤", fontSize = 12.sp)
                    }
                    Spacer(modifier = Modifier.width(6.dp))
                }

                Text(
                    text = if (isUser) "ВЫ" else message.senderName,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    color = if (isUser) NeonCyan else FantasyGoldLight,
                    modifier = if (!isUser) Modifier.clickable(onClick = onAvatarClick) else Modifier
                )
            }

            Spacer(modifier = Modifier.height(4.dp))

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(14.dp))
                    .background(
                        if (isUser) FantasySurfaceVariant
                        else FantasySurface
                    )
                    .border(
                        width = 1.dp,
                        color = if (isUser) NeonCyan.copy(alpha = 0.3f) else FantasyOutline,
                        shape = RoundedCornerShape(14.dp)
                    )
                    .padding(12.dp)
            ) {
                Column {
                    if (!isUser && !message.imageUrl.isNullOrBlank()) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(160.dp)
                                .clip(RoundedCornerShape(10.dp))
                                .border(1.dp, FantasyGold.copy(alpha = 0.6f), RoundedCornerShape(10.dp))
                        ) {
                            SubcomposeAsyncImage(
                                model = message.imageUrl,
                                contentDescription = "Иллюстрация события",
                                contentScale = ContentScale.Crop,
                                modifier = Modifier.fillMaxSize(),
                                loading = {
                                    Box(
                                        modifier = Modifier.fillMaxSize().background(DarkCanvas),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        CircularProgressIndicator(
                                            modifier = Modifier.size(24.dp),
                                            color = FantasyGold,
                                            strokeWidth = 2.dp
                                        )
                                    }
                                }
                            )
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(40.dp)
                                    .align(Alignment.BottomCenter)
                                    .background(
                                        Brush.verticalGradient(
                                            listOf(Color.Transparent, Color.Black.copy(alpha = 0.7f))
                                        )
                                    )
                            )
                            Text(
                                text = "🖼️ Кадр События • ИИ Иллюстрация",
                                fontSize = 9.sp,
                                fontWeight = FontWeight.Bold,
                                color = FantasyGoldLight,
                                modifier = Modifier
                                    .align(Alignment.BottomStart)
                                    .padding(8.dp)
                            )
                        }
                        Spacer(modifier = Modifier.height(10.dp))
                    }

                    if (isUser) {
                        Text(
                            text = message.text,
                            fontSize = 14.sp,
                            color = TextPrimary,
                            fontWeight = FontWeight.Medium
                        )
                    } else {
                        AnimatedTypewriterText(
                            fullText = message.text,
                            textColor = TextPrimary,
                            fontSize = 14f
                        )
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CharacterProfileModalSheet(
    profile: CharacterProfile,
    onUpgradeSkill: (String) -> Unit,
    onTrainCharacter: () -> Unit,
    onDismiss: () -> Unit
) {
    ModalBottomSheet(
        onDismissRequest = onDismiss,
        containerColor = FantasySurface
    ) {
        LazyColumn(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp),
            contentPadding = PaddingValues(bottom = 28.dp)
        ) {
            item {
                // Header Profile Info
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(64.dp)
                            .clip(CircleShape)
                            .background(
                                Brush.sweepGradient(listOf(FantasyGold, Color(0xFFFF6F00), FantasyGold))
                            )
                            .padding(3.dp)
                            .clip(CircleShape)
                            .background(FantasySurfaceVariant),
                        contentAlignment = Alignment.Center
                    ) {
                        Text("👤", fontSize = 32.sp)
                    }

                    Spacer(modifier = Modifier.width(14.dp))

                    Column(modifier = Modifier.weight(1f)) {
                        Text(profile.name, fontSize = 20.sp, fontFamily = FontFamily.Serif, fontWeight = FontWeight.Bold, color = TextPrimary)
                        Text(profile.title, fontSize = 12.sp, color = FantasyGold)
                        Text(profile.quote, fontSize = 11.sp, color = TextSecondary, fontWeight = FontWeight.Medium)
                    }

                    Surface(
                        shape = CircleShape,
                        color = FantasyWoodBrown,
                        border = androidx.compose.foundation.BorderStroke(1.dp, FantasyGold)
                    ) {
                        Text(
                            text = "Ур. ${profile.level}",
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold,
                            color = FantasyGoldLight,
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Affinity Bar
                DarkGlassCard(borderColor = FantasyGold) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text("Уровень Доверия:", fontSize = 12.sp, color = TextSecondary)
                        Text("${profile.affinityTitle} (${profile.affinityLevel}%)", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = FantasyGoldLight)
                    }
                    Spacer(modifier = Modifier.height(6.dp))
                    LinearProgressIndicator(
                        progress = profile.affinityLevel / 100f,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(6.dp)
                            .clip(RoundedCornerShape(3.dp)),
                        color = FantasyGold,
                        trackColor = FantasySurfaceVariant
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Skill Points Header Banner
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("Навыки и Способности:", fontSize = 16.sp, fontFamily = FontFamily.Serif, fontWeight = FontWeight.Bold, color = TextPrimary)

                    Surface(
                        shape = RoundedCornerShape(8.dp),
                        color = if (profile.availableSkillPoints > 0) FantasyWoodBrown else FantasySurfaceVariant,
                        border = androidx.compose.foundation.BorderStroke(1.dp, FantasyGold)
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(Icons.Default.Bolt, contentDescription = null, tint = FantasyGold, modifier = Modifier.size(14.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = "Очки: ${profile.availableSkillPoints}",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                color = FantasyGoldLight
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))
            }

            // Skills List Items
            items(profile.skills) { skill ->
                SkillCardItem(
                    skill = skill,
                    canUpgrade = profile.availableSkillPoints > 0 && skill.level < skill.maxLevel,
                    onUpgrade = { onUpgradeSkill(skill.id) }
                )
                Spacer(modifier = Modifier.height(10.dp))
            }

            item {
                Spacer(modifier = Modifier.height(12.dp))
                // Train Character Button
                Button(
                    onClick = onTrainCharacter,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(48.dp)
                        .testTag("train_companion_button"),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = FantasyWoodBrown)
                ) {
                    Icon(Icons.Default.AutoAwesome, contentDescription = null, tint = FantasyGold)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Тренировать Спутника (+1 Очко Навыков)", color = FantasyGoldLight, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                }
            }
        }
    }
}

@Composable
fun SkillCardItem(
    skill: CharacterSkill,
    canUpgrade: Boolean,
    onUpgrade: () -> Unit
) {
    DarkGlassCard(borderColor = FantasyOutline) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .clip(CircleShape)
                    .background(FantasyWoodBrown),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = when (skill.iconName) {
                        "memory" -> Icons.Default.Memory
                        "visibility_off" -> Icons.Default.VisibilityOff
                        "bolt" -> Icons.Default.Bolt
                        "shield" -> Icons.Default.Shield
                        "auto_awesome" -> Icons.Default.AutoAwesome
                        "flare" -> Icons.Default.Flare
                        "rocket_launch" -> Icons.Default.RocketLaunch
                        "psychology" -> Icons.Default.Psychology
                        "search" -> Icons.Default.Search
                        else -> Icons.Default.Star
                    },
                    contentDescription = null,
                    tint = FantasyGold
                )
            }

            Spacer(modifier = Modifier.width(12.dp))

            Column(modifier = Modifier.weight(1f)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(skill.name, fontSize = 14.sp, fontWeight = FontWeight.Bold, color = TextPrimary)
                    Text("Ур. ${skill.level} / ${skill.maxLevel}", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = FantasyGold)
                }

                Text(skill.description, fontSize = 11.sp, color = TextSecondary)
                Text("Эффект: ${skill.effectPerLevel}", fontSize = 10.sp, color = FantasyGoldLight)
            }

            if (canUpgrade) {
                Spacer(modifier = Modifier.width(8.dp))
                IconButton(
                    onClick = onUpgrade,
                    modifier = Modifier
                        .size(36.dp)
                        .clip(CircleShape)
                        .background(FantasyWoodBrown)
                        .testTag("upgrade_skill_${skill.id}")
                ) {
                    Icon(Icons.Default.Add, contentDescription = "Прокачать", tint = FantasyGold)
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AmbientAudioModalSheet(
    userProfile: UserProfile,
    onToggleAmbient: () -> Unit,
    onVolumeChange: (Float) -> Unit,
    onPresetSelect: (AmbientPreset) -> Unit,
    onDismiss: () -> Unit
) {
    ModalBottomSheet(
        onDismissRequest = onDismiss,
        containerColor = FantasySurface
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp)
        ) {
            FantasyHeaderTitle(text = "Аудио Эмбиент", fontSize = 22)
            Spacer(modifier = Modifier.height(12.dp))

            // Audio Toggle Switch
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.VolumeUp, contentDescription = null, tint = FantasyGold)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Включить Эмбиент Звук", fontSize = 14.sp, color = TextPrimary, fontWeight = FontWeight.Medium)
                }

                Switch(
                    checked = userProfile.isAmbientAudioEnabled,
                    onCheckedChange = { onToggleAmbient() },
                    colors = SwitchDefaults.colors(checkedThumbColor = FantasyGold)
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Volume Slider
            Text("Громкость Звука:", fontSize = 12.sp, color = TextSecondary)
            Slider(
                value = userProfile.ambientVolume,
                onValueChange = onVolumeChange,
                enabled = userProfile.isAmbientAudioEnabled,
                colors = SliderDefaults.colors(thumbColor = FantasyGold, activeTrackColor = FantasyGold)
            )

            Spacer(modifier = Modifier.height(12.dp))

            Text("Аудио-Режимы и Пресеты:", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = TextPrimary)
            Spacer(modifier = Modifier.height(8.dp))

            AmbientPreset.values().forEach { preset ->
                Surface(
                    onClick = { onPresetSelect(preset) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 4.dp),
                    shape = RoundedCornerShape(10.dp),
                    color = FantasySurfaceVariant,
                    border = if (AmbientAudioEngine.currentPreset == preset && userProfile.isAmbientAudioEnabled) {
                        androidx.compose.foundation.BorderStroke(1.dp, FantasyGold)
                    } else null
                ) {
                    Row(
                        modifier = Modifier.padding(12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = if (AmbientAudioEngine.currentPreset == preset && userProfile.isAmbientAudioEnabled) Icons.Default.GraphicEq else Icons.Default.MusicNote,
                            contentDescription = null,
                            tint = if (AmbientAudioEngine.currentPreset == preset && userProfile.isAmbientAudioEnabled) FantasyGold else TextSecondary
                        )
                        Spacer(modifier = Modifier.width(10.dp))
                        Column {
                            Text(preset.titleRu, fontSize = 13.sp, fontWeight = FontWeight.Bold, color = TextPrimary)
                            Text(preset.description, fontSize = 11.sp, color = TextSecondary)
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(20.dp))
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RpgCharacterSheetModal(
    rpgStats: RpgStats,
    characterName: String,
    role: String,
    onDismiss: () -> Unit
) {
    ModalBottomSheet(
        onDismissRequest = onDismiss,
        containerColor = FantasySurface
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "Карточка Развития Игрока",
                        fontSize = 18.sp,
                        fontFamily = FontFamily.Serif,
                        fontWeight = FontWeight.Bold,
                        color = TextPrimary
                    )
                    Text(
                        text = "Спутник: $characterName ($role)",
                        fontSize = 12.sp,
                        color = FantasyGold
                    )
                }

                Surface(
                    shape = CircleShape,
                    color = FantasyWoodBrown,
                    border = androidx.compose.foundation.BorderStroke(1.dp, FantasyGold)
                ) {
                    Text(
                        text = "Ур. ${rpgStats.level}",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        color = FantasyGoldLight,
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            RpgStatBar("Здоровье (HP)", rpgStats.hp, rpgStats.maxHp, DangerRed)
            Spacer(modifier = Modifier.height(8.dp))
            RpgStatBar("Энергия / Мана", rpgStats.manaOrEnergy, rpgStats.maxManaOrEnergy, NeonCyan)
            Spacer(modifier = Modifier.height(8.dp))
            RpgStatBar("Опыт (XP)", rpgStats.xp % 100, 100, FantasyGold)

            Spacer(modifier = Modifier.height(16.dp))

            Text("Атрибуты Персонажа:", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = TextPrimary)
            Spacer(modifier = Modifier.height(8.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                AttributeChip("🧠 Интеллект", rpgStats.intelligence.toString())
                AttributeChip("💬 Харизма", rpgStats.charisma.toString())
                AttributeChip("⚔️ Бой", rpgStats.combat.toString())
                AttributeChip("🍀 Удача", rpgStats.luck.toString())
            }

            Spacer(modifier = Modifier.height(16.dp))

            Text("Инвентарь & Награды:", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = TextPrimary)
            Spacer(modifier = Modifier.height(8.dp))

            rpgStats.inventory.forEach { item ->
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.padding(vertical = 2.dp)
                ) {
                    Icon(Icons.Default.Check, contentDescription = null, tint = FantasyGold, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(item, fontSize = 12.sp, color = TextSecondary)
                }
            }

            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}

@Composable
fun AttributeChip(label: String, value: String) {
    Surface(
        shape = RoundedCornerShape(8.dp),
        color = FantasySurfaceVariant
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.padding(horizontal = 10.dp, vertical = 8.dp)
        ) {
            Text(label, fontSize = 10.sp, color = TextSecondary)
            Text(value, fontSize = 14.sp, fontWeight = FontWeight.Bold, color = TextPrimary)
        }
    }
}
