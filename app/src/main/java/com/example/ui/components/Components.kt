package com.example.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.*
import androidx.compose.animation.fadeIn
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shadow
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.layout.ContentScale
import coil.compose.SubcomposeAsyncImage
import com.example.data.model.World
import com.example.data.model.WorldGenre
import com.example.ui.theme.*
import kotlinx.coroutines.delay

@Composable
fun FantasyHeaderTitle(
    text: String,
    modifier: Modifier = Modifier,
    fontSize: Int = 32
) {
    Box(modifier = modifier, contentAlignment = Alignment.Center) {
        // Shadow text layer
        Text(
            text = text,
            fontSize = fontSize.sp,
            fontFamily = FontFamily.Serif,
            fontWeight = FontWeight.Bold,
            color = Color.Black.copy(alpha = 0.8f),
            textAlign = TextAlign.Center,
            modifier = Modifier.offset(y = 2.dp, x = 1.dp)
        )
        // Foreground styled text layer
        Text(
            text = text,
            fontSize = fontSize.sp,
            fontFamily = FontFamily.Serif,
            fontWeight = FontWeight.Bold,
            color = FantasyGoldLight,
            textAlign = TextAlign.Center,
            style = TextStyle(
                shadow = Shadow(
                    color = Color(0xFF8B5A2B),
                    blurRadius = 8f
                )
            )
        )
    }
}

@Composable
fun FantasyPillButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    badgeText: String? = null
) {
    Surface(
        onClick = onClick,
        modifier = modifier
            .fillMaxWidth()
            .height(48.dp),
        shape = RoundedCornerShape(24.dp),
        color = FantasyWoodBrown,
        border = androidx.compose.foundation.BorderStroke(1.5.dp, FantasyGold)
    ) {
        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 20.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            Text(
                text = text,
                fontSize = 15.sp,
                fontFamily = FontFamily.Serif,
                fontWeight = FontWeight.Bold,
                color = FantasyGoldLight,
                textAlign = TextAlign.Center
            )
            if (badgeText != null) {
                Spacer(modifier = Modifier.width(8.dp))
                Surface(
                    shape = RoundedCornerShape(10.dp),
                    color = FantasyGold
                ) {
                    Text(
                        text = badgeText,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        color = FantasyWoodBrown,
                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                    )
                }
            }
        }
    }
}

@Composable
fun FantasyStoryPosterCard(
    world: World,
    statsCount: Int,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val imageUrl = remember(world.id, world.genre) {
        when (world.id) {
            "oasis_1" -> "https://images.unsplash.com/photo-1518709268805-4e9042af9f23?w=600&q=80"
            "world_echo" -> "https://images.unsplash.com/photo-1514933651103-005eec06c04b?w=600&q=80"
            "sovereign_1" -> "https://images.unsplash.com/photo-1509198397868-475647b2a1e5?w=600&q=80"
            "academy_magic" -> "https://images.unsplash.com/photo-1534447677768-be436bb09401?w=600&q=80"
            "dragon_lair" -> "https://images.unsplash.com/photo-1563089145-599997674d42?w=600&q=80"
            "elven_citadel" -> "https://images.unsplash.com/photo-1519681393784-d120267933ba?w=600&q=80"
            "necromancer_curse" -> "https://images.unsplash.com/photo-1509281373149-e957c6296406?w=600&q=80"
            "sunken_atlantis" -> "https://images.unsplash.com/photo-1544551763-46a013bb70d5?w=600&q=80"
            "cyber_1" -> "https://images.unsplash.com/photo-1542751371-adc38448a05e?w=600&q=80"
            "bprd_1" -> "https://images.unsplash.com/photo-1518709268805-4e9042af9f23?w=600&q=80"
            "netrunner_protocol" -> "https://images.unsplash.com/photo-1511512578047-dfb367046420?w=600&q=80"
            "chromed_samurai" -> "https://images.unsplash.com/photo-1607604276583-eef5d076aa5f?w=600&q=80"
            "corporate_heist" -> "https://images.unsplash.com/photo-1486406146926-c627a92ad1ab?w=600&q=80"
            "raven_1" -> "https://images.unsplash.com/photo-1451187580459-43490279c0fa?w=600&q=80"
            "odyssey_1" -> "https://images.unsplash.com/photo-1507525428034-b723cf961d3e?w=600&q=80"
            "mars_colony" -> "https://images.unsplash.com/photo-1614728894747-a83421e2b9c9?w=600&q=80"
            "alien_contact" -> "https://images.unsplash.com/photo-1506703719100-a0f3a48c0f86?w=600&q=80"
            "time_paradox" -> "https://images.unsplash.com/photo-1508739773434-c26b3d09e071?w=600&q=80"
            "deadend_1" -> "https://images.unsplash.com/photo-1509198397868-475647b2a1e5?w=600&q=80"
            "murder_express" -> "https://images.unsplash.com/photo-1515260268569-9271009adfdb?w=600&q=80"
            "foggy_london" -> "https://images.unsplash.com/photo-1513635269975-59663e0ac1ad?w=600&q=80"
            "wasteland_survival" -> "https://images.unsplash.com/photo-1541872703-74c5e44368f9?w=600&q=80"
            "biohazard_zone" -> "https://images.unsplash.com/photo-1518709268805-4e9042af9f23?w=600&q=80"
            "mech_scrappers" -> "https://images.unsplash.com/photo-1581092160607-ee22621dd758?w=600&q=80"
            else -> {
                when (world.genre) {
                    WorldGenre.CYBERPUNK -> "https://images.unsplash.com/photo-1550745165-9bc0b252726f?w=600&q=80"
                    WorldGenre.DARK_FANTASY -> "https://images.unsplash.com/photo-1534447677768-be436bb09401?w=600&q=80"
                    WorldGenre.SCI_FI -> "https://images.unsplash.com/photo-1446776811953-b23d57bd21aa?w=600&q=80"
                    WorldGenre.DETECTIVE -> "https://images.unsplash.com/photo-1453945620805-07530e047714?w=600&q=80"
                    WorldGenre.POST_APOCALYPSE -> "https://images.unsplash.com/photo-1518709268805-4e9042af9f23?w=600&q=80"
                    WorldGenre.ADULT_18 -> "https://images.unsplash.com/photo-1518895949257-7621c3c786d7?w=600&q=80"
                }
            }
        }
    }

    Box(
        modifier = modifier
            .width(145.dp)
            .height(210.dp)
            .clip(RoundedCornerShape(16.dp))
            .clickable(onClick = onClick)
            .border(1.5.dp, FantasyGold.copy(alpha = 0.8f), RoundedCornerShape(16.dp))
            .background(
                Brush.verticalGradient(
                    listOf(
                        Color(0xFF2A1E17),
                        Color(0xFF1E1815),
                        Color(0xFF0F0B09)
                    )
                )
            )
            .testTag("story_poster_${world.id}")
    ) {
        // High quality cover image loaded via Coil
        SubcomposeAsyncImage(
            model = imageUrl,
            contentDescription = world.title,
            contentScale = ContentScale.Crop,
            modifier = Modifier.fillMaxSize(),
            loading = {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(
                            Brush.verticalGradient(
                                listOf(FantasySurfaceVariant, FantasyDarkCanvas)
                            )
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(24.dp),
                        color = FantasyGold,
                        strokeWidth = 2.dp
                    )
                }
            },
            error = {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(
                            Brush.radialGradient(
                                colors = listOf(
                                    when (world.genre) {
                                        WorldGenre.CYBERPUNK -> Color(0xFF381E72)
                                        WorldGenre.DARK_FANTASY -> Color(0xFF1B3B2B)
                                        WorldGenre.SCI_FI -> Color(0xFF1E3A5F)
                                        else -> Color(0xFF4A1A1A)
                                    }.copy(alpha = 0.8f),
                                    Color(0xFF0F0B09)
                                )
                            )
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = when(world.genre) {
                            WorldGenre.CYBERPUNK -> "\ud83c\udf06"
                            WorldGenre.DARK_FANTASY -> "\ud83d\udd6f\ufe0f"
                            WorldGenre.SCI_FI -> "\ud83d\ude80"
                            WorldGenre.DETECTIVE -> "\ud83d\udd0d"
                            WorldGenre.POST_APOCALYPSE -> "\u2623\ufe0f"
                            WorldGenre.ADULT_18 -> "\ud83d\udd25"
                        },
                        fontSize = 32.sp
                    )
                }
            }
        )

        // Top Gradient Shadow Overlay
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(48.dp)
                .background(
                    Brush.verticalGradient(
                        listOf(Color.Black.copy(alpha = 0.65f), Color.Transparent)
                    )
                )
        )

        // Top Left Stat Badge (\ud83d\udcca 10 / 210)
        Surface(
            shape = RoundedCornerShape(bottomEnd = 10.dp, topStart = 14.dp),
            color = Color.Black.copy(alpha = 0.75f),
            modifier = Modifier.align(Alignment.TopStart)
        ) {
            Row(
                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "\ud83d\udcca $statsCount",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
            }
        }

        // Bottom Gradient Overlay & Title
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .align(Alignment.BottomCenter)
                .background(
                    Brush.verticalGradient(
                        listOf(
                            Color.Transparent,
                            Color.Black.copy(alpha = 0.75f),
                            Color.Black.copy(alpha = 0.95f)
                        )
                    )
                )
                .padding(10.dp)
        ) {
            Text(
                text = world.title,
                fontSize = 14.sp,
                fontFamily = FontFamily.Serif,
                fontWeight = FontWeight.Bold,
                color = Color.White,
                textAlign = TextAlign.Center,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis,
                style = TextStyle(
                    shadow = Shadow(color = Color.Black, blurRadius = 6f)
                ),
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
}

@Composable
fun FantasySectionHeader(
    title: String,
    onViewAllClick: (() -> Unit)? = null
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text("\u2618\ufe0f", fontSize = 18.sp)
            Spacer(modifier = Modifier.width(6.dp))
            Text(
                text = title,
                fontSize = 20.sp,
                fontFamily = FontFamily.Serif,
                fontWeight = FontWeight.Bold,
                color = FantasyGoldLight,
                style = TextStyle(
                    shadow = Shadow(color = Color(0xFF3E2723), blurRadius = 4f)
                )
            )
        }

        if (onViewAllClick != null) {
            Text(
                text = "\u041f\u043e\u0441\u043c\u043e\u0442\u0440\u0435\u0442\u044c \u0432\u0441\u0435",
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold,
                color = FantasyGold,
                modifier = Modifier.clickable(onClick = onViewAllClick)
            )
        }
    }
}

@Composable
fun DarkGlassCard(
    modifier: Modifier = Modifier,
    borderColor: Color = FantasyGold.copy(alpha = 0.5f),
    content: @Composable ColumnScope.() -> Unit
) {
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(16.dp))
            .background(
                Brush.verticalGradient(
                    colors = listOf(
                        FantasySurfaceVariant.copy(alpha = 0.95f),
                        FantasySurface.copy(alpha = 0.98f)
                    )
                )
            )
            .border(
                width = 1.dp,
                brush = Brush.linearGradient(
                    colors = listOf(borderColor, Color.Transparent, borderColor.copy(alpha = 0.3f))
                ),
                shape = RoundedCornerShape(16.dp)
            )
            .padding(16.dp)
    ) {
        Column {
            content()
        }
    }
}

@Composable
fun RpgStatBar(
    label: String,
    currentValue: Int,
    maxValue: Int,
    barColor: Color,
    modifier: Modifier = Modifier
) {
    val progress = (currentValue.toFloat() / maxValue.toFloat()).coerceIn(0f, 1f)
    val animatedProgress by animateFloatAsState(
        targetValue = progress,
        animationSpec = tween(durationMillis = 600, easing = FastOutSlowInEasing),
        label = "stat_anim"
    )

    Column(modifier = modifier) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = label,
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold,
                color = TextSecondary
            )
            Text(
                text = "$currentValue / $maxValue",
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold,
                color = TextPrimary
            )
        }
        Spacer(modifier = Modifier.height(4.dp))
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(8.dp)
                .clip(RoundedCornerShape(4.dp))
                .background(Color.White.copy(alpha = 0.1f))
        ) {
            Box(
                modifier = Modifier
                    .fillMaxHeight()
                    .fillMaxWidth(animatedProgress)
                    .clip(RoundedCornerShape(4.dp))
                    .background(barColor)
            )
        }
    }
}

@Composable
fun ChoiceChip(
    text: String,
    statCheck: String?,
    riskLevel: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    testTagId: String = "choice_chip"
) {
    val riskColor = when (riskLevel) {
        "\u0412\u044b\u0441\u043e\u043a\u0438\u0439" -> DangerRed
        "\u0421\u043c\u0435\u0440\u0442\u0435\u043b\u044c\u043d\u044b\u0439" -> DangerRed
        "\u041d\u0438\u0437\u043a\u0438\u0439" -> SuccessGreen
        else -> FantasyGold
    }

    Surface(
        onClick = onClick,
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp)
            .testTag(testTagId),
        shape = RoundedCornerShape(12.dp),
        color = FantasySurfaceVariant,
        border = androidx.compose.foundation.BorderStroke(
            1.dp,
            Brush.horizontalGradient(listOf(riskColor.copy(alpha = 0.8f), FantasyGold.copy(alpha = 0.4f)))
        )
    ) {
        Row(
            modifier = Modifier
                .padding(horizontal = 16.dp, vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = text,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Medium,
                    color = TextPrimary
                )
                if (statCheck != null) {
                    Text(
                        text = "\u26a1 \u0422\u0440\u0435\u0431\u043e\u0432\u0430\u043d\u0438\u0435: $statCheck",
                        fontSize = 11.sp,
                        color = FantasyGoldLight,
                        fontWeight = FontWeight.SemiBold
                    )
                }
            }
            Spacer(modifier = Modifier.width(8.dp))
            Surface(
                shape = RoundedCornerShape(6.dp),
                color = riskColor.copy(alpha = 0.2f)
            ) {
                Text(
                    text = riskLevel,
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Bold,
                    color = riskColor,
                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                )
            }
        }
    }
}

fun parseMarkdown(text: String, highlightColor: Color = FantasyGoldLight): AnnotatedString {
    return buildAnnotatedString {
        var currentIndex = 0
        while (currentIndex < text.length) {
            val boldStart = text.indexOf("**", currentIndex)
            val italicStart = text.indexOf("*", currentIndex)
            val nextTokenIndex = listOf(
                if (boldStart != -1) boldStart else Int.MAX_VALUE,
                if (italicStart != -1 && italicStart != boldStart && (boldStart == -1 || italicStart < boldStart)) italicStart else Int.MAX_VALUE
            ).minOrNull() ?: Int.MAX_VALUE
            if (nextTokenIndex == Int.MAX_VALUE) {
                append(text.substring(currentIndex))
                break
            }
            append(text.substring(currentIndex, nextTokenIndex))
            currentIndex = nextTokenIndex
            if (currentIndex == boldStart) {
                val boldEnd = text.indexOf("**", currentIndex + 2)
                if (boldEnd != -1) {
                    withStyle(style = SpanStyle(fontWeight = FontWeight.Bold, color = highlightColor)) {
                        append(text.substring(currentIndex + 2, boldEnd))
                    }
                    currentIndex = boldEnd + 2
                } else {
                    append("**")
                    currentIndex += 2
                }
            } else if (currentIndex == italicStart) {
                val italicEnd = text.indexOf("*", currentIndex + 1)
                if (italicEnd != -1) {
                    withStyle(style = SpanStyle(fontStyle = FontStyle.Italic)) {
                        append(text.substring(currentIndex + 1, italicEnd))
                    }
                    currentIndex = italicEnd + 1
                } else {
                    append("*")
                    currentIndex += 1
                }
            }
        }
    }
}

@Composable
fun AnimatedTypewriterText(
    fullText: String,
    modifier: Modifier = Modifier,
    textColor: Color = TextPrimary,
    fontSize: Float = 15f,
    isAnimated: Boolean = true
) {
    val annotatedString = remember(fullText) { parseMarkdown(fullText) }
    var displayedLength by remember(annotatedString, isAnimated) {
        mutableStateOf(if (isAnimated) 0 else annotatedString.length)
    }

    LaunchedEffect(annotatedString, isAnimated) {
        if (isAnimated && displayedLength < annotatedString.length) {
            for (i in (displayedLength + 1)..annotatedString.length) {
                displayedLength = i
                delay(12)
            }
        }
    }

    Text(
        text = annotatedString.subSequence(0, displayedLength),
        modifier = modifier,
        color = textColor,
        fontSize = fontSize.sp,
        lineHeight = (fontSize * 1.4f).sp
    )
}
