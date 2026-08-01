package com.example.ui.screens

import androidx.compose.foundation.background
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
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.db.SocialPostEntity
import com.example.ui.components.DarkGlassCard
import com.example.ui.theme.*
import com.example.viewmodel.StoryViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SocialFeedScreen(viewModel: StoryViewModel) {
    val posts by viewModel.socialPosts.collectAsState()
    var showNewPostDialog by remember { mutableStateOf(false) }
    var newPostContent by remember { mutableStateOf("") }
    var newPostStoryTitle by remember { mutableStateOf("Киберпанк 2088") }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Социальная Лента", fontWeight = FontWeight.Bold, color = TextPrimary) },
                actions = {
                    IconButton(
                        onClick = { showNewPostDialog = true },
                        modifier = Modifier.testTag("create_post_button")
                    ) {
                        Icon(Icons.Default.Add, contentDescription = "Создать пост", tint = NeonPurple)
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
            contentPadding = PaddingValues(bottom = 90.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            item {
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "Сообщество Искателей Судеб",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = NeonCyan
                )
                Text(
                    text = "Делитесь впечатлениями, сюжетными поворотами и кастомными сценариями с друзьями.",
                    fontSize = 12.sp,
                    color = TextSecondary
                )
            }

            items(posts) { post ->
                SocialPostCard(post = post, onLikeClicked = { viewModel.likePost(post.id) })
            }
        }

        if (showNewPostDialog) {
            AlertDialog(
                onDismissRequest = { showNewPostDialog = false },
                title = { Text("Опубликовать Пост", color = TextPrimary) },
                text = {
                    Column {
                        OutlinedTextField(
                            value = newPostStoryTitle,
                            onValueChange = { newPostStoryTitle = it },
                            label = { Text("Вселенная / История") },
                            modifier = Modifier.fillMaxWidth()
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        OutlinedTextField(
                            value = newPostContent,
                            onValueChange = { newPostContent = it },
                            label = { Text("Ваш отзыв или спойлер...") },
                            modifier = Modifier.fillMaxWidth(),
                            maxLines = 4
                        )
                    }
                },
                confirmButton = {
                    Button(
                        onClick = {
                            if (newPostContent.isNotBlank()) {
                                viewModel.publishPost(newPostStoryTitle, newPostContent, "Выбор: Взлом Системы")
                                showNewPostDialog = false
                            }
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = NeonPurple)
                    ) {
                        Text("Опубликовать")
                    }
                },
                dismissButton = {
                    TextButton(onClick = { showNewPostDialog = false }) {
                        Text("Отмена")
                    }
                },
                containerColor = DarkSurface
            )
        }
    }
}

@Composable
fun SocialPostCard(post: SocialPostEntity, onLikeClicked: () -> Unit) {
    DarkGlassCard(modifier = Modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(36.dp)
                        .clip(CircleShape)
                        .background(NeonPurple.copy(alpha = 0.3f)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(Icons.Default.Person, contentDescription = null, tint = NeonPurple)
                }
                Spacer(modifier = Modifier.width(10.dp))
                Column {
                    Text(post.authorName, fontSize = 13.sp, fontWeight = FontWeight.Bold, color = TextPrimary)
                    Text("Вселенная: ${post.storyTitle}", fontSize = 11.sp, color = NeonCyan)
                }
            }

            if (post.choiceHighlight != null) {
                Surface(
                    shape = RoundedCornerShape(6.dp),
                    color = DarkSurfaceVariant
                ) {
                    Text(
                        text = post.choiceHighlight,
                        fontSize = 10.sp,
                        color = NeonGold,
                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(10.dp))

        Text(post.postContent, fontSize = 13.sp, color = TextPrimary, lineHeight = 18.sp)

        Spacer(modifier = Modifier.height(12.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.testTag("like_post_${post.id}")
            ) {
                IconButton(onClick = onLikeClicked) {
                    Icon(
                        Icons.Default.Favorite,
                        contentDescription = "Лайк",
                        tint = if (post.isLikedByMe) DangerRed else TextSecondary,
                        modifier = Modifier.size(18.dp)
                    )
                }
                Text("${post.likesCount}", fontSize = 12.sp, color = TextSecondary)
            }

            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Default.Comment, contentDescription = null, tint = TextSecondary, modifier = Modifier.size(16.dp))
                Spacer(modifier = Modifier.width(4.dp))
                Text("${post.commentsCount}", fontSize = 12.sp, color = TextSecondary)
            }
        }
    }
}
