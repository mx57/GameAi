package com.example.ui.screens

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Save
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.World
import com.example.data.model.WorldGenre
import com.example.ui.theme.*
import com.example.viewmodel.StoryViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditorScreen(
    viewModel: StoryViewModel,
    onBackClicked: () -> Unit,
    onWorldCreated: (World) -> Unit
) {
    val context = LocalContext.current

    var title by remember { mutableStateOf("") }
    var selectedGenre by remember { mutableStateOf(WorldGenre.CYBERPUNK) }
    var description by remember { mutableStateOf("") }
    var loreSummary by remember { mutableStateOf("") }
    var characterName by remember { mutableStateOf("") }
    var characterRole by remember { mutableStateOf("") }
    var initialMessage by remember { mutableStateOf("") }
    var choice1 by remember { mutableStateOf("") }
    var choice2 by remember { mutableStateOf("") }
    var imagePrompt by remember { mutableStateOf("") }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Редактор Сценариев", fontWeight = FontWeight.Bold, color = TextPrimary) },
                navigationIcon = {
                    IconButton(onClick = onBackClicked) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Назад", tint = TextPrimary)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = DarkSurface)
            )
        },
        containerColor = DarkCanvas
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(horizontal = 16.dp),
            contentPadding = PaddingValues(bottom = 32.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            item {
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "Создайте Собственный Мир",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = NeonPurple
                )
                Text(
                    text = "Задайте лор, персонажей и стартовые ветки выбора для игроков.",
                    fontSize = 12.sp,
                    color = TextSecondary
                )
            }

            item {
                EditorTextField(
                    label = "Название Мира",
                    value = title,
                    onValueChange = { title = it },
                    placeholder = "например: Кибер-Токио 2090",
                    testTagId = "editor_title_input"
                )
            }

            item {
                Text("Выберите Жанр:", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = TextPrimary)
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    WorldGenre.values().take(3).forEach { genre ->
                        FilterChip(
                            selected = selectedGenre == genre,
                            onClick = { selectedGenre = genre },
                            label = { Text(genre.titleRu, fontSize = 11.sp) },
                            colors = FilterChipDefaults.filterChipColors(selectedContainerColor = NeonPurple)
                        )
                    }
                }
            }

            item {
                EditorTextField(
                    label = "Краткое Описание",
                    value = description,
                    onValueChange = { description = it },
                    placeholder = "Описание для каталога миров...",
                    testTagId = "editor_desc_input"
                )
            }

            item {
                EditorTextField(
                    label = "Лор и Предыстория (Глубокий сюжет)",
                    value = loreSummary,
                    onValueChange = { loreSummary = it },
                    placeholder = "Расскажите про мир, конфликты и тайны...",
                    isSingleLine = false,
                    testTagId = "editor_lore_input"
                )
            }

            item {
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    Box(modifier = Modifier.weight(1f)) {
                        EditorTextField(
                            label = "Имя Спутника",
                            value = characterName,
                            onValueChange = { characterName = it },
                            placeholder = "Ева / Кеэлен",
                            testTagId = "editor_char_name_input"
                        )
                    }
                    Box(modifier = Modifier.weight(1f)) {
                        EditorTextField(
                            label = "Роль Персонажа",
                            value = characterRole,
                            onValueChange = { characterRole = it },
                            placeholder = "Хакер / Маг",
                            testTagId = "editor_char_role_input"
                        )
                    }
                }
            }

            item {
                EditorTextField(
                    label = "Стартовая Сообщение ИИ Персонажа",
                    value = initialMessage,
                    onValueChange = { initialMessage = it },
                    placeholder = "— Приветствую, Искатель! Наш корабль атакован...",
                    isSingleLine = false,
                    testTagId = "editor_init_msg_input"
                )
            }

            item {
                Text("Стартовые Врианты Выбора:", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = TextPrimary)
                EditorTextField(
                    label = "Вариант Выбора 1",
                    value = choice1,
                    onValueChange = { choice1 = it },
                    placeholder = "Приготовить оружие к бою",
                    testTagId = "editor_choice1_input"
                )
                Spacer(modifier = Modifier.height(6.dp))
                EditorTextField(
                    label = "Вариант Выбора 2",
                    value = choice2,
                    onValueChange = { choice2 = it },
                    placeholder = "Попытаться вступить в переговоры",
                    testTagId = "editor_choice2_input"
                )
            }

            item {
                EditorTextField(
                    label = "Промпт для Нейросетевой Арт-Генерации (на англ.)",
                    value = imagePrompt,
                    onValueChange = { imagePrompt = it },
                    placeholder = "Cyberpunk futuristic city dark aesthetic digital art",
                    testTagId = "editor_img_prompt_input"
                )
            }

            item {
                Spacer(modifier = Modifier.height(12.dp))
                Button(
                    onClick = {
                        if (title.isBlank() || initialMessage.isBlank()) {
                            Toast.makeText(context, "Заполните название и первое сообщение!", Toast.LENGTH_SHORT).show()
                            return@Button
                        }

                        val choicesList = listOf(
                            choice1.ifBlank { "Продолжить путь" },
                            choice2.ifBlank { "Осмотреться по сторонам" }
                        )

                        viewModel.createCustomWorld(
                            title = title,
                            genre = selectedGenre,
                            description = description.ifBlank { "Пользовательский мир от автора." },
                            lore = loreSummary.ifBlank { "Древний мир со своими законами." },
                            characterName = characterName.ifBlank { "Спутник" },
                            role = characterRole.ifBlank { "Проводник" },
                            initialMsg = initialMessage,
                            choices = choicesList,
                            imagePrompt = imagePrompt,
                            onCreated = { newWorld ->
                                Toast.makeText(context, "Сценарий создан и сохранен!", Toast.LENGTH_SHORT).show()
                                onWorldCreated(newWorld)
                            }
                        )
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(52.dp)
                        .testTag("save_scenario_button"),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = NeonPurple)
                ) {
                    Icon(Icons.Default.Save, contentDescription = null, tint = Color.White)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Сохранить и Опубликовать Сценарий", fontSize = 14.sp, fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}

@Composable
fun EditorTextField(
    label: String,
    value: String,
    onValueChange: (String) -> Unit,
    placeholder: String,
    isSingleLine: Boolean = true,
    testTagId: String
) {
    Column {
        Text(label, fontSize = 12.sp, fontWeight = FontWeight.Bold, color = TextSecondary)
        Spacer(modifier = Modifier.height(4.dp))
        OutlinedTextField(
            value = value,
            onValueChange = onValueChange,
            placeholder = { Text(placeholder, fontSize = 12.sp, color = TextSecondary) },
            modifier = Modifier
                .fillMaxWidth()
                .testTag(testTagId),
            shape = RoundedCornerShape(10.dp),
            singleLine = isSingleLine,
            maxLines = if (isSingleLine) 1 else 4,
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = NeonPurple,
                unfocusedBorderColor = DarkSurfaceVariant,
                focusedContainerColor = DarkSurface,
                unfocusedContainerColor = DarkSurface
            )
        )
    }
}
