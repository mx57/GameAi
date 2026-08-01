package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.ui.screens.*
import com.example.ui.theme.FantasyDarkCanvas
import com.example.ui.theme.FantasyGold
import com.example.ui.theme.FantasyGoldLight
import com.example.ui.theme.FantasySurface
import com.example.ui.theme.RealmTheme
import com.example.viewmodel.StoryViewModel

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            RealmTheme {
                val viewModel: StoryViewModel = viewModel()
                val navController = rememberNavController()
                val navBackStackEntry by navController.currentBackStackEntryAsState()
                val currentRoute = navBackStackEntry?.destination?.route ?: "home"

                Scaffold(
                    modifier = Modifier.fillMaxSize(),
                    containerColor = FantasyDarkCanvas,
                    bottomBar = {
                        if (currentRoute != "chat") {
                            NavigationBar(
                                containerColor = FantasySurface,
                                tonalElevation = 8.dp
                            ) {
                                NavigationBarItem(
                                    selected = currentRoute == "home",
                                    onClick = { navController.navigate("home") { popUpTo("home") { inclusive = true } } },
                                    icon = { Icon(Icons.Default.Explore, contentDescription = "Миры") },
                                    label = { Text("Миры", fontSize = 10.sp) },
                                    modifier = Modifier.testTag("nav_home"),
                                    colors = NavigationBarItemDefaults.colors(
                                        selectedIconColor = FantasyGoldLight,
                                        selectedTextColor = FantasyGoldLight,
                                        unselectedIconColor = Color.White.copy(alpha = 0.5f),
                                        unselectedTextColor = Color.White.copy(alpha = 0.5f),
                                        indicatorColor = FantasyGold.copy(alpha = 0.25f)
                                    )
                                )

                                NavigationBarItem(
                                    selected = currentRoute == "editor",
                                    onClick = { navController.navigate("editor") },
                                    icon = { Icon(Icons.Default.EditNote, contentDescription = "Редактор") },
                                    label = { Text("Редактор", fontSize = 10.sp) },
                                    modifier = Modifier.testTag("nav_editor"),
                                    colors = NavigationBarItemDefaults.colors(
                                        selectedIconColor = FantasyGoldLight,
                                        selectedTextColor = FantasyGoldLight,
                                        unselectedIconColor = Color.White.copy(alpha = 0.5f),
                                        unselectedTextColor = Color.White.copy(alpha = 0.5f),
                                        indicatorColor = FantasyGold.copy(alpha = 0.25f)
                                    )
                                )

                                NavigationBarItem(
                                    selected = currentRoute == "social",
                                    onClick = { navController.navigate("social") },
                                    icon = { Icon(Icons.Default.Forum, contentDescription = "Лента") },
                                    label = { Text("Лента", fontSize = 10.sp) },
                                    modifier = Modifier.testTag("nav_social"),
                                    colors = NavigationBarItemDefaults.colors(
                                        selectedIconColor = FantasyGoldLight,
                                        selectedTextColor = FantasyGoldLight,
                                        unselectedIconColor = Color.White.copy(alpha = 0.5f),
                                        unselectedTextColor = Color.White.copy(alpha = 0.5f),
                                        indicatorColor = FantasyGold.copy(alpha = 0.25f)
                                    )
                                )

                                NavigationBarItem(
                                    selected = currentRoute == "achievements",
                                    onClick = { navController.navigate("achievements") },
                                    icon = { Icon(Icons.Default.EmojiEvents, contentDescription = "Награды") },
                                    label = { Text("Награды", fontSize = 10.sp) },
                                    modifier = Modifier.testTag("nav_achievements"),
                                    colors = NavigationBarItemDefaults.colors(
                                        selectedIconColor = FantasyGoldLight,
                                        selectedTextColor = FantasyGoldLight,
                                        unselectedIconColor = Color.White.copy(alpha = 0.5f),
                                        unselectedTextColor = Color.White.copy(alpha = 0.5f),
                                        indicatorColor = FantasyGold.copy(alpha = 0.25f)
                                    )
                                )

                                NavigationBarItem(
                                    selected = currentRoute == "settings",
                                    onClick = { navController.navigate("settings") },
                                    icon = { Icon(Icons.Default.Settings, contentDescription = "Настройки") },
                                    label = { Text("Настройки", fontSize = 10.sp) },
                                    modifier = Modifier.testTag("nav_settings"),
                                    colors = NavigationBarItemDefaults.colors(
                                        selectedIconColor = FantasyGoldLight,
                                        selectedTextColor = FantasyGoldLight,
                                        unselectedIconColor = Color.White.copy(alpha = 0.5f),
                                        unselectedTextColor = Color.White.copy(alpha = 0.5f),
                                        indicatorColor = FantasyGold.copy(alpha = 0.25f)
                                    )
                                )
                            }
                        }
                    }
                ) { innerPadding ->
                    NavHost(
                        navController = navController,
                        startDestination = "home",
                        modifier = Modifier.padding(innerPadding)
                    ) {
                        composable("home") {
                            HomeScreen(
                                viewModel = viewModel,
                                onWorldSelected = { world ->
                                    viewModel.selectWorld(world)
                                    navController.navigate("chat")
                                },
                                onOpenEditor = { navController.navigate("editor") },
                                onOpenMultiplayer = { navController.navigate("multiplayer") }
                            )
                        }

                        composable("chat") {
                            StoryChatScreen(
                                viewModel = viewModel,
                                onBackClicked = { navController.popBackStack() }
                            )
                        }

                        composable("editor") {
                            EditorScreen(
                                viewModel = viewModel,
                                onBackClicked = { navController.popBackStack() },
                                onWorldCreated = { world ->
                                    viewModel.selectWorld(world)
                                    navController.navigate("chat")
                                }
                            )
                        }

                        composable("social") {
                            SocialFeedScreen(viewModel = viewModel)
                        }

                        composable("achievements") {
                            AchievementsScreen(viewModel = viewModel)
                        }

                        composable("settings") {
                            SettingsScreen(viewModel = viewModel)
                        }

                        composable("multiplayer") {
                            MultiplayerScreen(
                                viewModel = viewModel,
                                onBackClicked = { navController.popBackStack() },
                                onStartCoopStory = {
                                    viewModel.allStories.value.firstOrNull()?.let {
                                        viewModel.selectWorld(it)
                                        navController.navigate("chat")
                                    }
                                }
                            )
                        }
                    }
                }
            }
        }
    }
}
