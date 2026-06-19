package com.example.ui

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.viewmodel.DietPlannerViewModel

data class BadgeItem(
    val id: String,
    val titleEn: String,
    val titleBn: String,
    val descEn: String,
    val descBn: String,
    val points: Int,
    val icon: String,
    val color: Color,
    val checkCondition: (foodCount: Int, exercisCount: Int, moodCount: Int, waterIntakeMl: Int) -> Boolean
)

@Composable
fun GamificationBadges(
    viewModel: DietPlannerViewModel,
    isBengali: Boolean,
    modifier: Modifier = Modifier
) {
    val foodLogs by viewModel.allFoodLogs.collectAsState()
    val exerciseLogs by viewModel.allExerciseLogs.collectAsState()
    val moodLogs by viewModel.currentMoodLogs.collectAsState()
    val waterLog by viewModel.waterLog.collectAsState()

    val foodCount = foodLogs.size
    val exerciseCount = exerciseLogs.size
    val moodCount = moodLogs.size
    val waterIntakeMl = waterLog?.amountMl ?: 0

    // Badge list definition
    val badges = remember {
        listOf(
            BadgeItem(
                id = "first_log",
                titleEn = "Pioneer Logger",
                titleBn = "পথিকৃৎ ট্র্যাকার",
                descEn = "Log your first meal or task of the day",
                descBn = "দিনের প্রথম খাবার বা কাজ ট্র্যাকিং সম্পন্ন করুন",
                points = 50,
                icon = "🥑",
                color = Color(0xFF4CAF50),
                checkCondition = { f, e, m, w -> f > 0 || e > 0 || m > 0 || w > 0 }
            ),
            BadgeItem(
                id = "green_chef",
                titleEn = "Green Chef",
                titleBn = "সবুজ শেফ",
                descEn = "Log 3 or more healthy food items",
                descBn = "৩টি বা তার বেশি পুষ্টিকর খাবার ট্র্যাকিং করুন",
                points = 150,
                icon = "🥗",
                color = Color(0xFF2E7D32),
                checkCondition = { f, _, _, _ -> f >= 3 }
            ),
            BadgeItem(
                id = "hydration_hero",
                titleEn = "Hydration Hero",
                titleBn = "হাইড্রেশন হিরো",
                descEn = "Drink 2,000 ml or more water",
                descBn = "২,০০০ মিলি অথবা তার বেশি পানি পান করুন",
                points = 100,
                icon = "💧",
                color = Color(0xFF0288D1),
                checkCondition = { _, _, _, w -> w >= 2000 }
            ),
            BadgeItem(
                id = "sweat_equity",
                titleEn = "Sweat Master",
                titleBn = "ঘাম ঝরানো ওস্তাদ",
                descEn = "Log at least one physical exercise workout",
                descBn = "কমপক্ষে একটি কায়িক পরিশ্রম বা ব্যায়াম ট্র্যাক করুন",
                points = 200,
                icon = "🏃",
                color = Color(0xFFE65100),
                checkCondition = { _, e, _, _ -> e > 0 }
            ),
            BadgeItem(
                id = "mindful_monk",
                titleEn = "Mindful Monk",
                titleBn = "মনোযোগী সাধক",
                descEn = "Log 3 or more days of emotional mood ratings",
                descBn = "৩টি বা তার বেশি আবেগ ও মুড রেটিং লগ করুন",
                points = 150,
                icon = "🧘",
                color = Color(0xFF673AB7),
                checkCondition = { _, _, m, _ -> m >= 3 }
            ),
            BadgeItem(
                id = "consistency_king",
                titleEn = "Consistency King",
                titleBn = "ধারাবাহিকতার রাজা",
                descEn = "Complete 5 logs across food, exercise, or mood",
                descBn = "খাবার, ব্যায়াম এবং মেজাজ মিলিয়ে মোট ৫টি লুপ সাবমিট করুন",
                points = 300,
                icon = "👑",
                color = Color(0xFFD84315),
                checkCondition = { f, e, m, _ -> (f + e + m) >= 5 }
            )
        )
    }

    // Calculating total reward points dynamically
    val unlockedBadges = badges.filter { it.checkCondition(foodCount, exerciseCount, moodCount, waterIntakeMl) }
    val totalPoints = unlockedBadges.sumOf { it.points }

    Card(
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.5f)),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        modifier = modifier
            .fillMaxWidth()
            .testTag("gamification_badge_dashboard")
    ) {
        Column(
            modifier = Modifier.padding(18.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Header Section with Animated Score Counter
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(42.dp)
                            .background(
                                color = MaterialTheme.colorScheme.primaryContainer,
                                shape = CircleShape
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Star,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(24.dp)
                        )
                    }
                    Column {
                        Text(
                            text = if (isBengali) "লাইফস্টাইল মেডেল ও স্কোর" else "Wellness Badges & Status",
                            fontWeight = FontWeight.Bold,
                            fontSize = 16.sp,
                            color = MaterialTheme.colorScheme.primary
                        )
                        Text(
                            text = if (isBengali)
                                "সুস্থ অভ্যাসের মাধ্যমে অর্জন করুন অনন্য সম্মাননা"
                            else
                                "Unlock prestigious titles by tracking your daily life",
                            fontSize = 11.sp,
                            color = Color.Gray
                        )
                    }
                }

                // Points Counter Pill
                Box(
                    modifier = Modifier
                        .background(
                            brush = Brush.horizontalGradient(
                                colors = listOf(MaterialTheme.colorScheme.primary, MaterialTheme.colorScheme.secondary)
                            ),
                            shape = RoundedCornerShape(16.dp)
                        )
                        .padding(horizontal = 12.dp, vertical = 6.dp)
                ) {
                    Text(
                        text = if (isBengali) "$totalPoints পয়েন্ট" else "$totalPoints PTS",
                        fontWeight = FontWeight.ExtraBold,
                        color = Color.White,
                        fontSize = 12.sp
                    )
                }
            }

            // High priority progress status indicator
            val progressRatio = unlockedBadges.size.toFloat() / badges.size.toFloat()
            Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = if (isBengali) "পদক আনলকিং অগ্রগতি" else "Badge Completion Progress",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = Color.Gray
                    )
                    Text(
                        text = "${unlockedBadges.size} / ${badges.size}",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary
                    )
                }
                LinearProgressIndicator(
                    progress = progressRatio,
                    trackColor = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.3f),
                    color = MaterialTheme.colorScheme.primary,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(8.dp)
                        .clip(RoundedCornerShape(4.dp))
                )
            }

            // Beautiful Responsive Grid of Badges
            val columns = GridCells.Adaptive(minSize = 140.dp)
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .heightIn(max = 280.dp)
            ) {
                LazyVerticalGrid(
                    columns = columns,
                    horizontalArrangement = Arrangement.spacedBy(10.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    items(badges) { badge ->
                        val isUnlocked = badge.checkCondition(foodCount, exerciseCount, moodCount, waterIntakeMl)
                        AnimatedBadgeCard(
                            badge = badge,
                            isUnlocked = isUnlocked,
                            isBengali = isBengali
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun AnimatedBadgeCard(
    badge: BadgeItem,
    isUnlocked: Boolean,
    isBengali: Boolean
) {
    // Beautiful subtle scale animation of badges when unlocked
    val scaleBy by animateFloatAsState(
        targetValue = if (isUnlocked) 1.02f else 0.98f,
        animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy, stiffness = Spring.StiffnessLow),
        label = "BadgeScale"
    )

    Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (isUnlocked) badge.color.copy(alpha = 0.08f) else Color(0xFFF5F7F8)
        ),
        border = BorderStroke(
            width = 1.dp,
            color = if (isUnlocked) badge.color.copy(alpha = 0.3f) else Color(0xFFE0E0E0).copy(alpha = 0.5f)
        ),
        modifier = Modifier
            .scale(scaleBy)
            .fillMaxWidth()
            .testTag("badge_card_${badge.id}")
    ) {
        Column(
            modifier = Modifier
                .padding(12.dp)
                .fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            // Badge Symbol Container (Emoji / Icon)
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .background(
                        color = if (isUnlocked) badge.color.copy(alpha = 0.15f) else Color(0xFFE0E0E0),
                        shape = CircleShape
                    ),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = badge.icon,
                    fontSize = 24.sp,
                    modifier = Modifier.scale(if (isUnlocked) 1.1f else 0.85f)
                )

                if (!isUnlocked) {
                    // Small lock icon overlay
                    Box(
                        modifier = Modifier
                            .fillMaxSize(),
                        contentAlignment = Alignment.BottomEnd
                    ) {
                        Surface(
                            color = Color.DarkGray,
                            shape = CircleShape,
                            modifier = Modifier.size(16.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Lock,
                                contentDescription = null,
                                tint = Color.White,
                                modifier = Modifier.size(10.dp).padding(2.dp)
                            )
                        }
                    }
                } else {
                    // Sparkly success check badge
                    Box(
                        modifier = Modifier
                            .fillMaxSize(),
                        contentAlignment = Alignment.BottomEnd
                    ) {
                        Icon(
                            imageVector = Icons.Default.CheckCircle,
                            contentDescription = null,
                            tint = Color(0xFF4CAF50),
                            modifier = Modifier
                                .size(18.dp)
                                .background(Color.White, CircleShape)
                        )
                    }
                }
            }

            // Title
            Text(
                text = if (isBengali) badge.titleBn else badge.titleEn,
                fontWeight = FontWeight.Bold,
                fontSize = 12.sp,
                textAlign = TextAlign.Center,
                color = if (isUnlocked) badge.color else Color.Gray,
                modifier = Modifier.fillMaxWidth()
            )

            // Description
            Text(
                text = if (isBengali) badge.descBn else badge.descEn,
                fontSize = 9.sp,
                textAlign = TextAlign.Center,
                color = if (isUnlocked) Color.DarkGray else Color.LightGray,
                lineHeight = 12.sp,
                modifier = Modifier.weight(1f, fill = false)
            )

            // Reward Points Display
            Text(
                text = "+${badge.points} PTS",
                fontSize = 9.sp,
                fontWeight = FontWeight.ExtraBold,
                color = if (isUnlocked) badge.color else Color.LightGray
            )
        }
    }
}
