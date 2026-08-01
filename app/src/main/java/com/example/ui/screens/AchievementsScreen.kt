package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.db.AchievementEntity
import com.example.ui.components.DarkGlassCard
import com.example.ui.components.RpgStatBar
import com.example.ui.theme.*
import com.example.viewmodel.StoryViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AchievementsScreen(viewModel: StoryViewModel) {
    val userProfile by viewModel.userProfile.collectAsState()
    val achievements by viewModel.achievements.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Достижения и Профиль", fontWeight = FontWeight.Bold, color = TextPrimary) },
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
                // User Level Banner
                DarkGlassCard(borderColor = NeonGold) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(userProfile.name, fontSize = 20.sp, fontWeight = FontWeight.Bold, color = TextPrimary)
                            Text(userProfile.title, fontSize = 12.sp, color = NeonGold)
                        }

                        Surface(
                            shape = CircleShape,
                            color = NeonGold.copy(alpha = 0.2f)
                        ) {
                            Text(
                                text = "Уровень ${userProfile.level}",
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Bold,
                                color = NeonGold,
                                modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))
                    RpgStatBar("Общий Опыт Мастеринга (XP)", userProfile.currentXp, userProfile.nextLevelXp, NeonGold)
                }
            }

            item {
                Text(
                    text = "Список Достижений (${achievements.count { it.isUnlocked }}/${achievements.size})",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = TextPrimary
                )
            }

            items(achievements) { ach ->
                AchievementCardItem(achievement = ach)
            }
        }
    }
}

@Composable
fun AchievementCardItem(achievement: AchievementEntity) {
    DarkGlassCard(
        borderColor = if (achievement.isUnlocked) NeonGold.copy(alpha = 0.6f) else DarkSurfaceVariant
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(44.dp)
                    .clip(CircleShape)
                    .background(if (achievement.isUnlocked) NeonGold.copy(alpha = 0.2f) else DarkSurfaceVariant),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = if (achievement.isUnlocked) Icons.Default.EmojiEvents else Icons.Default.Lock,
                    contentDescription = null,
                    tint = if (achievement.isUnlocked) NeonGold else TextSecondary
                )
            }

            Spacer(modifier = Modifier.width(12.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = achievement.titleRu,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    color = if (achievement.isUnlocked) TextPrimary else TextSecondary
                )
                Text(
                    text = achievement.descriptionRu,
                    fontSize = 12.sp,
                    color = TextSecondary
                )
            }

            Spacer(modifier = Modifier.width(8.dp))

            Surface(
                shape = RoundedCornerShape(6.dp),
                color = NeonPurple.copy(alpha = 0.2f)
            ) {
                Text(
                    text = "+${achievement.xpReward} XP",
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Bold,
                    color = NeonPurple,
                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                )
            }
        }
    }
}
