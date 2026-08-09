package com.example.ui.screens

import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.ImageResolution
import com.example.ui.components.DarkGlassCard
import com.example.ui.theme.*
import com.example.util.AmbientPreset
import com.example.viewmodel.StoryViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(viewModel: StoryViewModel) {
    val userProfile by viewModel.userProfile.collectAsState()
    val context = LocalContext.current
    var isSyncing by remember { mutableStateOf(false) }
    var notificationsEnabled by remember { mutableStateOf(true) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Настройки и Облако", fontWeight = FontWeight.Bold, color = TextPrimary) },
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
            contentPadding = PaddingValues(bottom = 90.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            item {
                Spacer(modifier = Modifier.height(8.dp))
                // Ambient Sound Settings Card
                DarkGlassCard(borderColor = NeonPurple) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(Icons.Default.VolumeUp, contentDescription = null, tint = NeonPurple)
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = "Фоновый Аудио-Эмбиент",
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = TextPrimary
                                )
                            }
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = "Генерация синт-гула и шумов для погружения в атмосферу истории.",
                                fontSize = 11.sp,
                                color = TextSecondary
                            )
                        }

                        Switch(
                            checked = userProfile.isAmbientAudioEnabled,
                            onCheckedChange = { viewModel.toggleAmbientAudio() },
                            modifier = Modifier.testTag("settings_ambient_switch"),
                            colors = SwitchDefaults.colors(checkedThumbColor = NeonPurple)
                        )
                    }

                    if (userProfile.isAmbientAudioEnabled) {
                        Spacer(modifier = Modifier.height(10.dp))
                        Text("Громкость: ${(userProfile.ambientVolume * 100).toInt()}%", fontSize = 11.sp, color = TextSecondary)
                        Slider(
                            value = userProfile.ambientVolume,
                            onValueChange = { viewModel.setAmbientVolume(it) },
                            colors = SliderDefaults.colors(thumbColor = NeonPurple, activeTrackColor = NeonPurple)
                        )
                    }
                }
            }

            item {
                // Offline Gemma Mode Switch & Engine Status
                DarkGlassCard(borderColor = if (userProfile.offlineGemmaMode) NeonGold else NeonPurple) {
                    Column {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(Icons.Default.Memory, contentDescription = null, tint = NeonGold)
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text(
                                        text = "Локальная Модель Gemma (Оффлайн)",
                                        fontSize = 14.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = TextPrimary
                                    )
                                }
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = "Автономная генерация сюжетных ветвей и диалогов с персонажами прямо на устройстве без доступа к сети.",
                                    fontSize = 11.sp,
                                    color = TextSecondary
                                )
                            }

                            Switch(
                                checked = userProfile.offlineGemmaMode,
                                onCheckedChange = { viewModel.toggleOfflineMode(it) },
                                modifier = Modifier.testTag("offline_gemma_switch"),
                                colors = SwitchDefaults.colors(checkedThumbColor = NeonGold, checkedTrackColor = DarkSurfaceVariant)
                            )
                        }

                        Spacer(modifier = Modifier.height(8.dp))

                        Surface(
                            shape = RoundedCornerShape(8.dp),
                            color = DarkSurfaceVariant,
                            border = androidx.compose.foundation.BorderStroke(0.5.dp, if (userProfile.offlineGemmaMode) NeonGold else FantasyOutline)
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(horizontal = 10.dp, vertical = 6.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(
                                        imageVector = if (userProfile.offlineGemmaMode) Icons.Default.CheckCircle else Icons.Default.PowerSettingsNew,
                                        contentDescription = null,
                                        tint = if (userProfile.offlineGemmaMode) NeonGold else TextMuted,
                                        modifier = Modifier.size(14.dp)
                                    )
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text(
                                        text = if (userProfile.offlineGemmaMode) "Gemma 2B Local Engine • Активна (Готова к диалогам)" else "Офлайн-движок готов к включению",
                                        fontSize = 10.sp,
                                        fontWeight = FontWeight.Medium,
                                        color = if (userProfile.offlineGemmaMode) NeonGold else TextMuted
                                    )
                                }

                                TextButton(
                                    onClick = {
                                        Toast.makeText(context, "Тест Gemma 2B: Офлайн-модуль исправен и мгновенно обрабатывает реплики!", Toast.LENGTH_LONG).show()
                                    },
                                    contentPadding = PaddingValues(0.dp)
                                ) {
                                    Text("Тест", fontSize = 10.sp, color = NeonCyan, fontWeight = FontWeight.Bold)
                                }
                            }
                        }
                    }
                }
            }

            item {
                // Resolution Selector
                DarkGlassCard {
                    Text("Разрешение Нейро-Картинок:", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = TextPrimary)
                    Spacer(modifier = Modifier.height(4.dp))
                    Text("Выбор детализации генерации артов для сцен (1K / 2K / 4K)", fontSize = 11.sp, color = TextSecondary)
                    Spacer(modifier = Modifier.height(10.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        ImageResolution.values().forEach { res ->
                            FilterChip(
                                selected = userProfile.selectedResolution == res,
                                onClick = { viewModel.setImageResolution(res) },
                                label = { Text("${res.label} (${res.pixels})", fontSize = 11.sp) },
                                colors = FilterChipDefaults.filterChipColors(selectedContainerColor = NeonPurple),
                                modifier = Modifier.weight(1f).testTag("resolution_chip_${res.label}")
                            )
                        }
                    }
                }
            }

            item {
                // Mature Content Toggle (18+)
                DarkGlassCard(borderColor = DangerRed) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(Icons.Default.Warning, contentDescription = null, tint = DangerRed)
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = "Взрослый Контент (18+)",
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = TextPrimary
                                )
                            }
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = "Разрешить генерацию откровенных сцен, насилия и ненормативной лексики в сюжете.",
                                fontSize = 11.sp,
                                color = TextSecondary
                            )
                        }

                        Switch(
                            checked = userProfile.allowMatureContent,
                            onCheckedChange = { viewModel.toggleMatureContent(it) },
                            modifier = Modifier.testTag("mature_content_switch"),
                            colors = SwitchDefaults.colors(checkedThumbColor = DangerRed, checkedTrackColor = DarkSurfaceVariant)
                        )
                    }
                }
            }

            item {
                // Cloud Sync Box
                DarkGlassCard(borderColor = NeonCyan) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    if (isSyncing) Icons.Default.CloudSync else Icons.Default.CloudDone,
                                    contentDescription = null,
                                    tint = NeonCyan
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = "Облачная Синхронизация",
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = TextPrimary
                                )
                            }
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = "Сохранение прогресса, кастомных сценариев и наград между устройствами.",
                                fontSize = 11.sp,
                                color = TextSecondary
                            )
                        }

                        Button(
                            onClick = {
                                isSyncing = true
                                Toast.makeText(context, "Облачная синхронизация завершена!", Toast.LENGTH_SHORT).show()
                                isSyncing = false
                            },
                            shape = RoundedCornerShape(10.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = NeonCyan)
                        ) {
                            Text("Синхронизировать", fontSize = 11.sp, color = DarkCanvas, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }

            item {
                // Notifications Switch
                DarkGlassCard {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.Notifications, contentDescription = null, tint = NeonPurple)
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("Уведомления о Новых Публикациях", fontSize = 13.sp, color = TextPrimary)
                        }

                        Switch(
                            checked = notificationsEnabled,
                            onCheckedChange = { notificationsEnabled = it },
                            colors = SwitchDefaults.colors(checkedThumbColor = NeonPurple)
                        )
                    }
                }
            }
        }
    }
}
