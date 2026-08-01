package com.example.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable

private val FantasyDarkColorScheme = darkColorScheme(
    primary = FantasyGold,
    onPrimary = FantasyWoodBrown,
    primaryContainer = FantasyWoodBrown,
    onPrimaryContainer = FantasyGoldLight,
    secondary = NeonCyan,
    onSecondary = FantasyDarkCanvas,
    tertiary = NeonPurple,
    onTertiary = FantasyDarkCanvas,
    background = FantasyDarkCanvas,
    onBackground = TextPrimary,
    surface = FantasySurface,
    onSurface = TextPrimary,
    surfaceVariant = FantasySurfaceVariant,
    onSurfaceVariant = TextSecondary,
    outline = FantasyOutline,
    error = DangerRed
)

@Composable
fun RealmTheme(content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = FantasyDarkColorScheme,
        typography = Typography,
        content = content
    )
}
