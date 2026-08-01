package com.example.ui.screens

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.components.DarkGlassCard
import com.example.ui.theme.*
import com.example.viewmodel.StoryViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MultiplayerScreen(
    viewModel: StoryViewModel,
    onBackClicked: () -> Unit,
    onStartCoopStory: () -> Unit
) {
    val context = LocalContext.current
    val roomCode by viewModel.multiplayerRoomCode.collectAsState()
    val activePlayers by viewModel.activeRoomPlayers.collectAsState()

    var inputCode by remember { mutableStateOf("") }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Кооперативный Режим", fontWeight = FontWeight.Bold, color = TextPrimary) },
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
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(horizontal = 16.dp)
        ) {
            Spacer(modifier = Modifier.height(12.dp))

            DarkGlassCard(modifier = Modifier.fillMaxWidth()) {
                Text(
                    text = "Совместное Исследование Миров",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = NeonCyan
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "Подключайтесь с друзьями по коду комнаты для совместного голосования и принятия ключевых решений в сюжете.",
                    fontSize = 12.sp,
                    color = TextSecondary
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            if (roomCode == null) {
                // Room creation / join panel
                Text("Создание или Вход в Комнату:", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = TextPrimary)
                Spacer(modifier = Modifier.height(10.dp))

                Button(
                    onClick = { viewModel.createMultiplayerRoom("Совместный Парламент") },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(50.dp)
                        .testTag("create_room_button"),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = NeonPurple)
                ) {
                    Icon(Icons.Default.AddCircle, contentDescription = null, tint = Color.White)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Создать Кооперативную Комнату", fontSize = 14.sp, fontWeight = FontWeight.Bold)
                }

                Spacer(modifier = Modifier.height(16.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Divider(modifier = Modifier.weight(1f), color = DarkSurfaceVariant)
                    Text(" ИЛИ ", fontSize = 11.sp, color = TextSecondary, modifier = Modifier.padding(horizontal = 8.dp))
                    Divider(modifier = Modifier.weight(1f), color = DarkSurfaceVariant)
                }
                Spacer(modifier = Modifier.height(16.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    OutlinedTextField(
                        value = inputCode,
                        onValueChange = { inputCode = it.uppercase() },
                        placeholder = { Text("Введите код (напр. REALM-8892)", fontSize = 12.sp, color = TextSecondary) },
                        modifier = Modifier
                            .weight(1f)
                            .testTag("room_code_input"),
                        shape = RoundedCornerShape(12.dp),
                        singleLine = true,
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = NeonCyan,
                            unfocusedBorderColor = DarkSurfaceVariant,
                            focusedContainerColor = DarkSurface,
                            unfocusedContainerColor = DarkSurface
                        )
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Button(
                        onClick = {
                            if (inputCode.isNotBlank()) {
                                viewModel.joinMultiplayerRoom(inputCode)
                            }
                        },
                        enabled = inputCode.isNotBlank(),
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = NeonCyan)
                    ) {
                        Text("Войти", color = DarkCanvas, fontWeight = FontWeight.Bold)
                    }
                }
            } else {
                // Active Room Lobby
                DarkGlassCard(borderColor = NeonCyan) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text("Активная Комната:", fontSize = 11.sp, color = TextSecondary)
                            Text(roomCode!!, fontSize = 22.sp, fontWeight = FontWeight.Bold, color = NeonCyan)
                        }

                        Surface(
                            shape = RoundedCornerShape(8.dp),
                            color = SuccessGreen.copy(alpha = 0.2f)
                        ) {
                            Text("Синхронизировано", fontSize = 10.sp, color = SuccessGreen, modifier = Modifier.padding(6.dp))
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    Text("Подключенные Игроки (${activePlayers.size}):", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = TextPrimary)
                    Spacer(modifier = Modifier.height(8.dp))

                    activePlayers.forEach { player ->
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.padding(vertical = 4.dp)
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(8.dp)
                                    .clip(CircleShape)
                                    .background(SuccessGreen)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(player, fontSize = 13.sp, color = TextPrimary, fontWeight = FontWeight.Medium)
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    Button(
                        onClick = onStartCoopStory,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(48.dp)
                            .testTag("launch_coop_story_button"),
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = NeonPurple)
                    ) {
                        Icon(Icons.Default.PlayArrow, contentDescription = null, tint = Color.White)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Начать Кооперативную Историю", fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
}
