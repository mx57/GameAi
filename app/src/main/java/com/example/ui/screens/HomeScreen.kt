package com.example.ui.screens

import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
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
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.World
import com.example.data.model.WorldGenre
import com.example.ui.components.*
import com.example.ui.theme.*
import com.example.viewmodel.StoryViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    viewModel: StoryViewModel,
    onWorldSelected: (World) -> Unit,
    onOpenEditor: () -> Unit,
    onOpenMultiplayer: () -> Unit
) {
    val stories by viewModel.allStories.collectAsState()
    var searchQuery by remember { mutableStateOf("") }
    var selectedGenre by remember { mutableStateOf<WorldGenre?>(null) }

    // Filter stories based on query and genre
    val filteredStories = remember(stories, searchQuery, selectedGenre) {
        stories.filter { world ->
            val matchesGenre = selectedGenre == null || world.genre == selectedGenre
            val matchesSearch = searchQuery.isBlank() ||
                    world.title.contains(searchQuery, ignoreCase = true) ||
                    world.description.contains(searchQuery, ignoreCase = true) ||
                    world.primaryCharacterName.contains(searchQuery, ignoreCase = true)
            matchesGenre && matchesSearch
        }
    }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    listOf(
                        Color(0xFF1B1410),
                        FantasyDarkCanvas,
                        Color(0xFF0D0A08)
                    )
                )
            )
            .padding(horizontal = 16.dp),
        contentPadding = PaddingValues(bottom = 90.dp)
    ) {
        // Top Fantasy Header Bar
        item {
            Spacer(modifier = Modifier.height(12.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Menu Icon / Editor
                IconButton(onClick = onOpenEditor) {
                    Icon(Icons.Default.Menu, contentDescription = "Меню", tint = FantasyGold)
                }

                // Fiery Avatar Circle
                Box(
                    modifier = Modifier
                        .size(54.dp)
                        .clip(CircleShape)
                        .background(
                            Brush.sweepGradient(
                                listOf(FantasyGold, Color(0xFFFF6F00), FantasyGoldLight, FantasyGold)
                            )
                        )
                        .padding(3.dp)
                        .clip(CircleShape)
                        .background(FantasySurface)
                        .testTag("fiery_avatar_profile"),
                    contentAlignment = Alignment.Center
                ) {
                    Text("👤", fontSize = 24.sp)
                    // Level Badge Overlay
                    Surface(
                        shape = CircleShape,
                        color = Color(0xFFD84315),
                        modifier = Modifier
                            .align(Alignment.BottomCenter)
                            .offset(y = 4.dp)
                    ) {
                        Text(
                            text = "MAX",
                            fontSize = 9.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White,
                            modifier = Modifier.padding(horizontal = 5.dp, vertical = 1.dp)
                        )
                    }
                }

                // Unlimited AI Indicator
                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = FantasyWoodBrown,
                    border = androidx.compose.foundation.BorderStroke(1.dp, FantasyGold)
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text("⚡", fontSize = 14.sp)
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = "Безлимит ИИ",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = FantasyGoldLight
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Large Styled Title: AI Game Master
            FantasyHeaderTitle(text = "AI Game Master", fontSize = 34)

            Spacer(modifier = Modifier.height(14.dp))
        }

        // Search Bar
        item {
            OutlinedTextField(
                value = searchQuery,
                onValueChange = { searchQuery = it },
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("search_worlds_input"),
                placeholder = {
                    Text("Поиск миров, героев и жанров...", color = TextMuted, fontSize = 14.sp)
                },
                leadingIcon = {
                    Icon(Icons.Default.Search, contentDescription = "Поиск", tint = FantasyGold)
                },
                trailingIcon = {
                    if (searchQuery.isNotEmpty()) {
                        IconButton(onClick = { searchQuery = "" }) {
                            Icon(Icons.Default.Close, contentDescription = "Очистить", tint = TextMuted)
                        }
                    }
                },
                singleLine = true,
                shape = RoundedCornerShape(16.dp),
                colors = TextFieldDefaults.colors(
                    focusedContainerColor = FantasySurface,
                    unfocusedContainerColor = FantasySurface,
                    focusedIndicatorColor = FantasyGold,
                    unfocusedIndicatorColor = FantasyOutline,
                    focusedTextColor = TextPrimary,
                    unfocusedTextColor = TextPrimary
                )
            )

            Spacer(modifier = Modifier.height(12.dp))
        }

        // Genre Filter Chips
        item {
            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                item {
                    FilterChip(
                        selected = selectedGenre == null,
                        onClick = { selectedGenre = null },
                        label = { Text("Все (${stories.size})", fontSize = 12.sp) },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = FantasyGold,
                            selectedLabelColor = Color.Black,
                            containerColor = FantasySurface,
                            labelColor = TextSecondary
                        ),
                        border = FilterChipDefaults.filterChipBorder(
                            enabled = true,
                            selected = selectedGenre == null,
                            borderColor = FantasyOutline,
                            selectedBorderColor = FantasyGold
                        )
                    )
                }

                items(WorldGenre.values()) { genre ->
                    val isSelected = selectedGenre == genre
                    val emojiIcon = when(genre) {
                        WorldGenre.CYBERPUNK -> "🌆"
                        WorldGenre.DARK_FANTASY -> "🕯️"
                        WorldGenre.SCI_FI -> "🚀"
                        WorldGenre.DETECTIVE -> "🔍"
                        WorldGenre.POST_APOCALYPSE -> "☣️"
                        WorldGenre.ADULT_18 -> "🔥"
                    }
                    FilterChip(
                        selected = isSelected,
                        onClick = {
                            selectedGenre = if (isSelected) null else genre
                        },
                        label = {
                            Text("$emojiIcon ${genre.titleRu}", fontSize = 12.sp)
                        },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = FantasyGold,
                            selectedLabelColor = Color.Black,
                            containerColor = FantasySurface,
                            labelColor = TextSecondary
                        ),
                        border = FilterChipDefaults.filterChipBorder(
                            enabled = true,
                            selected = isSelected,
                            borderColor = FantasyOutline,
                            selectedBorderColor = FantasyGold
                        )
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))
        }

        // Action Buttons
        item {
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                FantasyPillButton(
                    text = "Продолжить приключение",
                    onClick = {
                        stories.firstOrNull()?.let { onWorldSelected(it) }
                    },
                    modifier = Modifier.testTag("continue_adventure_button")
                )

                FantasyPillButton(
                    text = "Создать свой сценарий",
                    onClick = onOpenEditor,
                    modifier = Modifier.testTag("start_new_adventure_button")
                )

                FantasyPillButton(
                    text = "Сохраненные приключения",
                    onClick = onOpenMultiplayer,
                    modifier = Modifier.testTag("saved_adventures_button")
                )
            }

            Spacer(modifier = Modifier.height(20.dp))
        }

        // Search or Filter results view
        if (searchQuery.isNotBlank() || selectedGenre != null) {
            item {
                FantasySectionHeader(
                    title = "Результаты поиска (${filteredStories.size})"
                )
                if (filteredStories.isEmpty()) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(24.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "По вашему запросу миры не найдены.",
                            color = TextMuted,
                            fontSize = 14.sp
                        )
                    }
                }
            }

            items(filteredStories.chunked(2)) { pair ->
                Row(
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 12.dp)
                ) {
                    for (world in pair) {
                        Box(modifier = Modifier.weight(1f)) {
                            FantasyStoryPosterCard(
                                world = world,
                                statsCount = (world.title.hashCode() % 150) + 20,
                                onClick = { onWorldSelected(world) }
                            )
                        }
                    }
                    if (pair.size == 1) {
                        Spacer(modifier = Modifier.weight(1f))
                    }
                }
            }
        } else {
            // Default Categorized Horizontal Rows
            // Section 1: Popular & Recommended
            item {
                FantasySectionHeader(title = "Топ Популярных Миров")
                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    items(stories.take(10)) { world ->
                        FantasyStoryPosterCard(
                            world = world,
                            statsCount = (world.title.hashCode() % 200) + 50,
                            onClick = { onWorldSelected(world) }
                        )
                    }
                }
                Spacer(modifier = Modifier.height(20.dp))
            }

            // Section 2: Dark Fantasy & Magic
            item {
                val darkFantasyWorlds = stories.filter { it.genre == WorldGenre.DARK_FANTASY }
                if (darkFantasyWorlds.isNotEmpty()) {
                    FantasySectionHeader(title = "🕯️ Тёмное Фэнтези и Магия")
                    LazyRow(
                        horizontalArrangement = Arrangement.spacedBy(12.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        items(darkFantasyWorlds) { world ->
                            FantasyStoryPosterCard(
                                world = world,
                                statsCount = (world.title.hashCode() % 150) + 30,
                                onClick = { onWorldSelected(world) }
                            )
                        }
                    }
                    Spacer(modifier = Modifier.height(20.dp))
                }
            }

            // Section 3: Cyberpunk & Neon
            item {
                val cyberpunkWorlds = stories.filter { it.genre == WorldGenre.CYBERPUNK }
                if (cyberpunkWorlds.isNotEmpty()) {
                    FantasySectionHeader(title = "🌆 Киберпанк и Неон")
                    LazyRow(
                        horizontalArrangement = Arrangement.spacedBy(12.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        items(cyberpunkWorlds) { world ->
                            FantasyStoryPosterCard(
                                world = world,
                                statsCount = (world.title.hashCode() % 180) + 40,
                                onClick = { onWorldSelected(world) }
                            )
                        }
                    }
                    Spacer(modifier = Modifier.height(20.dp))
                }
            }

            // Section 4: Sci-Fi & Cosmic Horizons
            item {
                val scifiWorlds = stories.filter { it.genre == WorldGenre.SCI_FI }
                if (scifiWorlds.isNotEmpty()) {
                    FantasySectionHeader(title = "🚀 Космическая Одиссея и Sci-Fi")
                    LazyRow(
                        horizontalArrangement = Arrangement.spacedBy(12.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        items(scifiWorlds) { world ->
                            FantasyStoryPosterCard(
                                world = world,
                                statsCount = (world.title.hashCode() % 160) + 25,
                                onClick = { onWorldSelected(world) }
                            )
                        }
                    }
                    Spacer(modifier = Modifier.height(20.dp))
                }
            }

            // Section 5: Detective & Post-Apocalypse
            item {
                val otherWorlds = stories.filter { it.genre == WorldGenre.DETECTIVE || it.genre == WorldGenre.POST_APOCALYPSE }
                if (otherWorlds.isNotEmpty()) {
                    FantasySectionHeader(title = "🔍 Детектив и Выживание в Пустоши")
                    LazyRow(
                        horizontalArrangement = Arrangement.spacedBy(12.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        items(otherWorlds) { world ->
                            FantasyStoryPosterCard(
                                world = world,
                                statsCount = (world.title.hashCode() % 140) + 20,
                                onClick = { onWorldSelected(world) }
                            )
                        }
                    }
                }
            }
        }
    }
}
