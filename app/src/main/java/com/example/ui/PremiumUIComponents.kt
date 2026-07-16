package com.example.ui

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

/**
 * Highly polished Linear Gradient Shimmer Brush adapting automatically
 * to light and dark theme backgrounds to provide clean skeleton load states.
 */
@Composable
fun shimmerBrush(
    targetValue: Float = 1200f,
    durationMillis: Int = 1500
): Brush {
    val isDark = isSystemInDarkTheme()
    val baseColor = if (isDark) Color(0xFF1E293B) else Color(0xFFE2E8F0)
    val highlightColor = if (isDark) Color(0xFF334155) else Color(0xFFF1F5F9)

    val transition = rememberInfiniteTransition(label = "premium_shimmer")
    val translateAnim = transition.animateFloat(
        initialValue = 0f,
        targetValue = targetValue,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = durationMillis, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "shimmer_translate"
    )

    return Brush.linearGradient(
        colors = listOf(
            baseColor,
            highlightColor,
            baseColor
        ),
        start = Offset.Zero,
        end = Offset(x = translateAnim.value, y = translateAnim.value)
    )
}

/**
 * Premium glassmorphic card wrapping any layout with transparent frosted glass texture,
 * sharp glowing highlights on borders, and high-fidelity depth scaling.
 */
@Composable
fun GlassmorphicCard(
    modifier: Modifier = Modifier,
    isDark: Boolean = isSystemInDarkTheme(),
    shape: Shape = RoundedCornerShape(24.dp),
    borderBrush: Brush? = null,
    backgroundColor: Color? = null,
    testTag: String = "glass_card",
    content: @Composable ColumnScope.() -> Unit
) {
    val defaultGlassBg = if (isDark) {
        Color(0xFF1C2541).copy(alpha = 0.55f) // Deep Navy Ocean
    } else {
        Color.White.copy(alpha = 0.72f) // Cool Foaming White
    }

    val defaultBorderBrush = Brush.linearGradient(
        colors = if (isDark) {
            listOf(
                Color.White.copy(alpha = 0.20f),
                Color.White.copy(alpha = 0.03f),
                Color.White.copy(alpha = 0.12f)
            )
        } else {
            listOf(
                Color.White.copy(alpha = 0.85f),
                Color.White.copy(alpha = 0.12f),
                Color.White.copy(alpha = 0.45f)
            )
        }
    )

    Card(
        modifier = modifier
            .testTag(testTag)
            .border(
                width = 1.2.dp,
                brush = borderBrush ?: defaultBorderBrush,
                shape = shape
            ),
        shape = shape,
        colors = CardDefaults.cardColors(containerColor = backgroundColor ?: defaultGlassBg),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Column(
            modifier = Modifier.padding(18.dp)
        ) {
            content()
        }
    }
}

/**
 * Elegant Skeleton Loader displaying shimmering shapes to indicate network or computational loading.
 */
@Composable
fun SkeletonCard(
    modifier: Modifier = Modifier,
    isDark: Boolean = isSystemInDarkTheme()
) {
    val brush = shimmerBrush()
    val cardBg = if (isDark) Color(0xFF1E293B).copy(alpha = 0.4f) else Color(0xFFF1F5F9).copy(alpha = 0.6f)

    Card(
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = cardBg),
        border = BorderStroke(1.dp, if (isDark) Color(0xFF334155).copy(alpha = 0.3f) else Color(0xFFE2E8F0).copy(alpha = 0.5f)),
        modifier = modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // Header Row Skeleton
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Box(
                    modifier = Modifier
                        .size(40.dp)
                        .clip(CircleShape)
                        .background(brush)
                )
                Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                    Box(
                        modifier = Modifier
                            .size(width = 120.dp, height = 16.dp)
                            .clip(RoundedCornerShape(4.dp))
                            .background(brush)
                    )
                    Box(
                        modifier = Modifier
                            .size(width = 80.dp, height = 11.dp)
                            .clip(RoundedCornerShape(4.dp))
                            .background(brush)
                    )
                }
            }

            // Body Skeletons
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(80.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(brush)
            )

            // Double Action Skeleton Row
            Row(
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .height(36.dp)
                        .clip(RoundedCornerShape(8.dp))
                        .background(brush)
                )
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .height(36.dp)
                        .clip(RoundedCornerShape(8.dp))
                        .background(brush)
                )
            }
        }
    }
}

/**
 * Highly immersive Success/Celebration vector pulse animation drawing
 * expanding rings to replicate rich Lottie transitions in pure Compose.
 */
@Composable
fun PremiumLottiePulse(
    modifier: Modifier = Modifier,
    color: Color = MaterialTheme.colorScheme.primary,
    emoji: String = "✨"
) {
    val infiniteTransition = rememberInfiniteTransition(label = "lottie_pulse")
    
    // Scale pulsation of outer aura
    val scale1 by infiniteTransition.animateFloat(
        initialValue = 0.5f,
        targetValue = 1.35f,
        animationSpec = infiniteRepeatable(
            animation = tween(1800, easing = EaseOutQuad),
            repeatMode = RepeatMode.Restart
        ),
        label = "halo_scale"
    )
    val alpha1 by infiniteTransition.animateFloat(
        initialValue = 0.8f,
        targetValue = 0f,
        animationSpec = infiniteRepeatable(
            animation = tween(1800, easing = EaseOutQuad),
            repeatMode = RepeatMode.Restart
        ),
        label = "halo_alpha"
    )

    // Inner orbiting rotation
    val rotation by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(
            animation = tween(4000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "core_rotation"
    )

    Box(
        modifier = modifier.size(100.dp),
        contentAlignment = Alignment.Center
    ) {
        // Outer shimmering waves
        Box(
            modifier = Modifier
                .fillMaxSize()
                .scale(scale1)
                .background(color.copy(alpha = alpha1), CircleShape)
        )

        Box(
            modifier = Modifier
                .fillMaxSize()
                .scale(scale1 * 0.7f)
                .background(color.copy(alpha = alpha1 * 0.6f), CircleShape)
        )

        // Solid animated inner core
        Box(
            modifier = Modifier
                .size(52.dp)
                .background(
                    brush = Brush.sweepGradient(
                        colors = listOf(color, color.copy(alpha = 0.6f), color)
                    ),
                    shape = CircleShape
                )
                .border(2.dp, Color.White.copy(alpha = 0.8f), CircleShape),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = emoji,
                fontSize = 24.sp,
                modifier = Modifier.scale(if (scale1 > 1.0f) 1.0f else scale1)
            )
        }
    }
}

/**
 * Floating glassmorphic bottom navigation bar displaying icons,
 * text labels, and smooth spring-animated active indicator pill.
 */
@Composable
fun FloatingGlassNavigationBar(
    currentTab: Int,
    onTabSelected: (Int) -> Unit,
    isBengali: Boolean,
    isDark: Boolean = isSystemInDarkTheme(),
    modifier: Modifier = Modifier
) {
    val items = remember {
        listOf(
            NavigationItemData(0, if (isBengali) "হোম" else "Home", Icons.Default.Check, "home_tab"), // dummy icons replaced on fly
            NavigationItemData(1, if (isBengali) "খাবার" else "Meals", Icons.Default.Check, "meals_tab"),
            NavigationItemData(2, if (isBengali) "এক্সপ্লোর" else "Explore", Icons.Default.Check, "explore_tab"),
            NavigationItemData(3, if (isBengali) "ট্র্যাকার" else "Tracker", Icons.Default.Check, "tracker_tab"),
            NavigationItemData(4, if (isBengali) "অ্যাকাউন্ট" else "Account", Icons.Default.Check, "profile_tab")
        )
    }

    val glassBg = if (isDark) {
        Color(0xFF131B2F).copy(alpha = 0.85f)
    } else {
        Color.White.copy(alpha = 0.88f)
    }

    val borderBrush = Brush.linearGradient(
        colors = if (isDark) {
            listOf(
                Color.White.copy(alpha = 0.18f),
                Color.White.copy(alpha = 0.04f),
                Color.White.copy(alpha = 0.10f)
            )
        } else {
            listOf(
                Color.White.copy(alpha = 0.80f),
                Color.White.copy(alpha = 0.10f),
                Color.White.copy(alpha = 0.45f)
            )
        }
    )

    Card(
        shape = RoundedCornerShape(28.dp),
        colors = CardDefaults.cardColors(containerColor = glassBg),
        border = BorderStroke(1.2.dp, borderBrush),
        elevation = CardDefaults.cardElevation(defaultElevation = 8.dp),
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 14.dp, vertical = 8.dp)
            .testTag("floating_glass_nav_bar")
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp, horizontal = 4.dp),
            horizontalArrangement = Arrangement.SpaceEvenly,
            verticalAlignment = Alignment.CenterVertically
        ) {
            items.forEach { item ->
                val isSelected = currentTab == item.index

                val scale by animateFloatAsState(
                    targetValue = if (isSelected) 1.08f else 1.0f,
                    animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy, stiffness = Spring.StiffnessLow),
                    label = "tab_scale"
                )

                val activeColor = MaterialTheme.colorScheme.primary
                val inactiveColor = if (isDark) Color(0xFF94A3B8) else Color(0xFF64748B)

                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center,
                    modifier = Modifier
                        .weight(1f)
                        .scale(scale)
                        .clip(RoundedCornerShape(16.dp))
                        .clickable(
                            interactionSource = remember { MutableInteractionSource() },
                            indication = null
                        ) {
                            onTabSelected(item.index)
                        }
                        .padding(vertical = 6.dp)
                        .testTag(item.testTag)
                ) {
                    val icon = when (item.index) {
                        0 -> Icons.Default.Check // Home icon is dummy, we will draw custom inside or map standard
                        1 -> Icons.Default.Check
                        2 -> Icons.Default.Check
                        3 -> Icons.Default.Check
                        4 -> Icons.Default.Check
                        else -> Icons.Default.Check
                    }

                    // Render custom emojis/icons based on tabs for extreme distinct look
                    val tabSymbol = when (item.index) {
                        0 -> "🏠"
                        1 -> "🍲"
                        2 -> "🧭"
                        3 -> "📈"
                        4 -> "👤"
                        else -> "⚙️"
                    }

                    Box(
                        contentAlignment = Alignment.Center,
                        modifier = Modifier
                            .size(42.dp)
                            .background(
                                color = if (isSelected) activeColor.copy(alpha = 0.15f) else Color.Transparent,
                                shape = CircleShape
                            )
                    ) {
                        Text(
                            text = tabSymbol,
                            fontSize = if (isSelected) 22.sp else 18.sp
                        )
                    }

                    Spacer(modifier = Modifier.height(2.dp))

                    Text(
                        text = item.label,
                        fontSize = 10.5.sp,
                        fontWeight = if (isSelected) FontWeight.Black else FontWeight.Bold,
                        color = if (isSelected) activeColor else inactiveColor
                    )
                }
            }
        }
    }
}

private data class NavigationItemData(
    val index: Int,
    val label: String,
    val icon: ImageVector,
    val testTag: String
)
