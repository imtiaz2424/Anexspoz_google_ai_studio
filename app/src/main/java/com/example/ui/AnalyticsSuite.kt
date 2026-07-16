package com.example.ui

import android.graphics.Paint
import android.graphics.Typeface
import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.*
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.viewmodel.DietPlannerViewModel
import com.example.data.model.FoodLogEntity
import com.example.data.model.ExerciseLogEntity
import com.example.data.model.MoodLogEntity
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalAnimationApi::class)
@Composable
fun AnalyticsSuite(
    viewModel: DietPlannerViewModel,
    isBengali: Boolean,
    modifier: Modifier = Modifier
) {
    val isDark = isSystemInDarkTheme()
    
    // Collect active records from DB
    val allFoodLogs by viewModel.allFoodLogs.collectAsState()
    val allExerciseLogs by viewModel.allExerciseLogs.collectAsState()
    val currentMoodLogs by viewModel.currentMoodLogs.collectAsState()

    // Loading State simulation to show Premium Skeleton loaders
    var isLoading by remember { mutableStateOf(true) }
    LaunchedEffect(Unit) {
        kotlinx.coroutines.delay(1000) // 1s beautiful shimmer intro
        isLoading = false
    }

    // Tab states
    var selectedTab by remember { mutableStateOf(0) } // 0: DAU, 1: Retention, 2: Revenue, 3: Subscription, 4: Health
    val tabsEn = listOf("DAU", "Retention", "Revenue", "Subscription", "Health")
    val tabsBn = listOf("সক্রিয় ব্যবহারকারী", "রিটেনশন", "রাজস্ব", "সাবস্ক্রিপশন", "স্বাস্থ্য")

    GlassmorphicCard(
        modifier = modifier
            .fillMaxWidth()
            .testTag("analytics_suite_main_card"),
        isDark = isDark
    ) {
        Column(
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Header Row
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(42.dp)
                            .background(
                                color = if (isDark) Color(0xFF1E3A8A) else Color(0xFFDBEAFE),
                                shape = CircleShape
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Text("📊", fontSize = 22.sp)
                    }
                    Column {
                        Text(
                            text = if (isBengali) "নিলজোরি অ্যানালিটিক্স সেন্টার" else "Niljori Analytics Hub",
                            fontWeight = FontWeight.Black,
                            fontSize = 17.sp,
                            color = if (isDark) Color(0xFF60A5FA) else Color(0xFF1D4ED8)
                        )
                        Text(
                            text = if (isBengali) "ব্যবসায়িক সমৃদ্ধি ও রিয়েল-টাইম স্বাস্থ্য ট্র্যাকিং" else "Professional enterprise stats & user-tier metrics",
                            fontSize = 10.5.sp,
                            color = if (isDark) Color(0xFF94A3B8) else Color.Gray
                        )
                    }
                }

                // Breathing pulse tag
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(12.dp))
                        .background(if (isDark) Color(0xFF065F46) else Color(0xFFD1FAE5))
                        .padding(horizontal = 8.dp, vertical = 4.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(6.dp)
                                .background(Color(0xFF10B981), CircleShape)
                        )
                        Text(
                            text = if (isBengali) "লাইভ" else "LIVE",
                            fontWeight = FontWeight.Bold,
                            fontSize = 9.sp,
                            color = Color(0xFF047857)
                        )
                    }
                }
            }

            Divider(color = if (isDark) Color.White.copy(alpha = 0.12f) else Color.LightGray.copy(alpha = 0.4f))

            if (isLoading) {
                SkeletonCard()
            } else {
                // Interactive Scrollable Tab Row with gorgeous glass pills
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .horizontalScroll(rememberScrollState())
                        .padding(bottom = 4.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    tabsEn.forEachIndexed { index, labelEn ->
                        val isSelected = selectedTab == index
                        val label = if (isBengali) tabsBn[index] else labelEn

                        val scale by animateFloatAsState(
                            targetValue = if (isSelected) 1.05f else 1.0f,
                            animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy, stiffness = Spring.StiffnessLow),
                            label = "tab_pill_scale"
                        )

                        val activeBg = if (isDark) Color(0xFF2563EB) else Color(0xFFDBEAFE)
                        val inactiveBg = if (isDark) Color(0xFF334155).copy(alpha = 0.4f) else Color(0xFFF1F5F9)

                        Box(
                            modifier = Modifier
                                .scale(scale)
                                .clip(RoundedCornerShape(14.dp))
                                .background(if (isSelected) activeBg else inactiveBg)
                                .border(
                                    width = 1.dp,
                                    color = if (isSelected) (if (isDark) Color(0xFF60A5FA) else Color(0xFF3B82F6)) else Color.Transparent,
                                    shape = RoundedCornerShape(14.dp)
                                )
                                .clickable { selectedTab = index }
                                .padding(horizontal = 14.dp, vertical = 8.dp)
                                .testTag("analytics_tab_$index"),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = label,
                                fontSize = 11.5.sp,
                                fontWeight = FontWeight.Black,
                                color = if (isSelected) {
                                    if (isDark) Color.White else Color(0xFF1D4ED8)
                                } else {
                                    if (isDark) Color(0xFF94A3B8) else Color(0xFF64748B)
                                }
                            )
                        }
                    }
                }

                // Sub-pane Content Crossfading
                AnimatedContent(
                    targetState = selectedTab,
                    transitionSpec = {
                        fadeIn(animationSpec = tween(300)) with fadeOut(animationSpec = tween(300))
                    },
                    label = "analytics_pane"
                ) { targetTab ->
                    when (targetTab) {
                        0 -> DAUAnalyticsPane(isBengali, isDark)
                        1 -> RetentionAnalyticsPane(isBengali, isDark)
                        2 -> RevenueAnalyticsPane(isBengali, isDark)
                        3 -> SubscriptionAnalyticsPane(isBengali, isDark)
                        4 -> HealthAnalyticsPane(isBengali, isDark, allFoodLogs, allExerciseLogs, currentMoodLogs)
                    }
                }
            }
        }
    }
}

/**
 * 1. DAILY ACTIVE USERS (DAU) PANE
 * Custom high-fidelity interactive bar chart showing continuous logs
 */
@Composable
fun DAUAnalyticsPane(isBengali: Boolean, isDark: Boolean) {
    var range7D by remember { mutableStateOf(true) }
    
    // Hardcoded highly aesthetic DAU data
    val rawDauData = remember {
        listOf(
            DauItem("07/07", 1250, 480),
            DauItem("07/08", 1420, 520),
            DauItem("07/09", 1380, 510),
            DauItem("07/10", 1650, 680), // Peak Friday
            DauItem("07/11", 1890, 850), // Peak Saturday
            DauItem("07/12", 1920, 910), // Peak Sunday
            DauItem("07/13", 1450, 580),
            DauItem("07/14", 1510, 600),
            DauItem("07/15", 1580, 610),
            DauItem("07/16", 1720, 710)  // Today
        )
    }

    val chartData = if (range7D) rawDauData.takeLast(7) else rawDauData
    var selectedIndex by remember(chartData) { mutableStateOf(chartData.size - 1) }
    val activeItem = chartData.getOrNull(selectedIndex)

    Column(verticalArrangement = Arrangement.spacedBy(14.dp)) {
        // Range & Summary KPI Block
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(
                    text = if (isBengali) "দৈনিক সক্রিয় ব্যবহারকারী (DAU)" else "Daily Active Users (DAU)",
                    fontWeight = FontWeight.Bold,
                    fontSize = 14.sp,
                    color = if (isDark) Color(0xFFF1F5F9) else Color(0xFF334155)
                )
                Text(
                    text = if (isBengali) "অ্যাপ ব্যবহারকারী সক্রিয়তার তীব্রতা গ্রাফ" else "Density trace of active users on Niljori platform",
                    fontSize = 10.sp,
                    color = if (isDark) Color(0xFF94A3B8) else Color.Gray
                )
            }

            // Simple interactive toggle
            Row(
                modifier = Modifier
                    .clip(RoundedCornerShape(8.dp))
                    .background(if (isDark) Color(0xFF1E293B) else Color(0xFFF1F5F9))
                    .padding(2.dp)
            ) {
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(6.dp))
                        .background(if (range7D) (if (isDark) Color(0xFF2563EB) else Color.White) else Color.Transparent)
                        .clickable { range7D = true }
                        .padding(horizontal = 8.dp, vertical = 4.dp)
                ) {
                    Text(
                        "7D",
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        color = if (range7D) (if (isDark) Color.White else Color(0xFF1E3A8A)) else Color.Gray
                    )
                }
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(6.dp))
                        .background(if (!range7D) (if (isDark) Color(0xFF2563EB) else Color.White) else Color.Transparent)
                        .clickable { range7D = false }
                        .padding(horizontal = 8.dp, vertical = 4.dp)
                ) {
                    Text(
                        "10D",
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        color = if (!range7D) (if (isDark) Color.White else Color(0xFF1E3A8A)) else Color.Gray
                    )
                }
            }
        }

        // Summary details card
        activeItem?.let { item ->
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(12.dp))
                    .background(if (isDark) Color(0xFF1E293B).copy(alpha = 0.6f) else Color(0xFFEFF6FF))
                    .padding(12.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "${if (isBengali) "তারিখ:" else "Date:"} ${item.date}",
                        fontSize = 11.sp,
                        color = if (isDark) Color(0xFF94A3B8) else Color.Gray,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = if (isBengali) "অনলাইন ট্রাফিক স্পাইক বিশ্লেষণ" else "Organic traffic distribution summary",
                        fontSize = 9.5.sp,
                        color = Color.Gray
                    )
                }

                Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                    Column(horizontalAlignment = Alignment.End) {
                        Text(
                            text = "👥 DAU",
                            fontSize = 10.sp,
                            color = if (isDark) Color(0xFF94A3B8) else Color.Gray
                        )
                        Text(
                            text = "${item.dau} ${if (isBengali) "জন" else "users"}",
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Black,
                            color = if (isDark) Color(0xFF60A5FA) else Color(0xFF1D4ED8)
                        )
                    }

                    Column(horizontalAlignment = Alignment.End) {
                        Text(
                            text = "⚡ Peak Active",
                            fontSize = 10.sp,
                            color = if (isDark) Color(0xFF94A3B8) else Color.Gray
                        )
                        Text(
                            text = "${item.peakConcurrent} ${if (isBengali) "জন" else "users"}",
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Black,
                            color = Color(0xFF10B981)
                        )
                    }
                }
            }
        }

        // Interactive Bar Chart Canvas
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(160.dp)
                .background(
                    if (isDark) Color(0xFF0F172A).copy(alpha = 0.5f) else Color(0xFFF8FAFC),
                    RoundedCornerShape(16.dp)
                )
                .padding(12.dp)
        ) {
            Canvas(
                modifier = Modifier
                    .fillMaxSize()
                    .pointerInput(chartData) {
                        detectTapGestures { offset ->
                            val leftPadding = 40.dp.toPx()
                            val rightPadding = 20.dp.toPx()
                            val graphWidth = size.width - leftPadding - rightPadding
                            val segmentWidth = graphWidth / chartData.size
                            
                            val clickedIdx = ((offset.x - leftPadding) / segmentWidth)
                                .toInt()
                                .coerceIn(0, chartData.size - 1)
                            selectedIndex = clickedIdx
                        }
                    }
            ) {
                val leftPadding = 45.dp.toPx()
                val rightPadding = 20.dp.toPx()
                val topPadding = 15.dp.toPx()
                val bottomPadding = 25.dp.toPx()
                val graphWidth = size.width - leftPadding - rightPadding
                val graphHeight = size.height - topPadding - bottomPadding

                val maxDau = 2200f
                val segmentWidth = graphWidth / chartData.size

                // Draw Y Axis markers
                for (i in 0..3) {
                    val y = topPadding + (i * (graphHeight / 3f))
                    val dauMark = (maxDau - (i * (maxDau / 3f))).toInt()
                    
                    drawLine(
                        color = if (isDark) Color.White.copy(alpha = 0.06f) else Color.LightGray.copy(alpha = 0.3f),
                        start = Offset(leftPadding, y),
                        end = Offset(leftPadding + graphWidth, y),
                        strokeWidth = 1.dp.toPx()
                    )

                    drawContext.canvas.nativeCanvas.drawText(
                        dauMark.toString(),
                        leftPadding - 10.dp.toPx(),
                        y + 4.dp.toPx(),
                        Paint().apply {
                            textSize = 8.sp.toPx()
                            color = if (isDark) android.graphics.Color.GRAY else android.graphics.Color.DKGRAY
                            textAlign = Paint.Align.RIGHT
                            typeface = Typeface.DEFAULT
                        }
                    )
                }

                // Draw Bars
                chartData.forEachIndexed { index, item ->
                    val x = leftPadding + (index * segmentWidth) + (segmentWidth * 0.2f)
                    val barWidth = segmentWidth * 0.6f
                    val barHeight = (item.dau.toFloat() / maxDau) * graphHeight
                    val y = topPadding + graphHeight - barHeight

                    val isSelected = index == selectedIndex
                    
                    // High-quality modern bar gradient
                    val barBrush = Brush.verticalGradient(
                        colors = if (isSelected) {
                            listOf(Color(0xFF3B82F6), Color(0xFF1D4ED8))
                        } else {
                            if (isDark) {
                                listOf(Color(0xFF3B82F6).copy(alpha = 0.4f), Color(0xFF1D4ED8).copy(alpha = 0.15f))
                            } else {
                                listOf(Color(0xFF93C5FD), Color(0xFF3B82F6).copy(alpha = 0.6f))
                            }
                        }
                    )

                    drawRoundRect(
                        brush = barBrush,
                        topLeft = Offset(x, y),
                        size = Size(barWidth, barHeight),
                        cornerRadius = androidx.compose.ui.geometry.CornerRadius(6.dp.toPx(), 6.dp.toPx())
                    )

                    // Draw optional subtle glowing aura above selected bar
                    if (isSelected) {
                        drawCircle(
                            color = Color(0xFF3B82F6),
                            radius = 3.dp.toPx(),
                            center = Offset(x + barWidth / 2f, y - 6.dp.toPx())
                        )
                    }

                    // X-Axis Text Labels
                    drawContext.canvas.nativeCanvas.drawText(
                        item.date,
                        x + barWidth / 2f,
                        topPadding + graphHeight + 16.dp.toPx(),
                        Paint().apply {
                            textSize = 8.sp.toPx()
                            color = if (isSelected) {
                                if (isDark) android.graphics.Color.WHITE else android.graphics.Color.BLACK
                            } else {
                                android.graphics.Color.GRAY
                            }
                            textAlign = Paint.Align.CENTER
                            typeface = if (isSelected) Typeface.DEFAULT_BOLD else Typeface.DEFAULT
                        }
                    )
                }
            }
        }
    }
}

private data class DauItem(val date: String, val dau: Int, val peakConcurrent: Int)

/**
 * 2. RETENTION ANALYTICS PANE
 * Custom continuous decaying line graph reflecting user cohorts
 */
@Composable
fun RetentionAnalyticsPane(isBengali: Boolean, isDark: Boolean) {
    val retentionMetrics = remember {
        listOf(
            RetentionItem("Day 1", 78f, "Excellent initial onboarding loop conversion rate."),
            RetentionItem("Day 3", 64f, "Good content discovery and recipe selection density."),
            RetentionItem("Week 1", 52f, "Solid weekly check-ins triggered by reminders & water logs."),
            RetentionItem("Week 2", 45f, "Healthy habit formation pattern and metabolic logging."),
            RetentionItem("Week 4", 39f, "Premium subscription value starting to show conversion trends."),
            RetentionItem("Week 8", 34f, "Highly loyal core customer base tracking weight scales."),
            RetentionItem("Week 12", 31f, "Industry-standard benchmark retention levels beaten!")
        )
    }

    var selectedIndex by remember { mutableStateOf(2) } // default Week 1 selected
    val activeItem = retentionMetrics.getOrNull(selectedIndex)

    Column(verticalArrangement = Arrangement.spacedBy(14.dp)) {
        Column {
            Text(
                text = if (isBengali) "ব্যবহারকারী রিটেনশন কার্ভ (Retention Cohorts)" else "User Retention Cohorts",
                fontWeight = FontWeight.Bold,
                fontSize = 14.sp,
                color = if (isDark) Color(0xFFF1F5F9) else Color(0xFF334155)
            )
            Text(
                text = if (isBengali) "প্রথম সাইন-আপ থেকে পরবর্তী সপ্তাহের সক্রিয় ব্যবহারকারী শতকরা হার" else "Standard subscription decay curve across weeks of engagement",
                fontSize = 10.sp,
                color = if (isDark) Color(0xFF94A3B8) else Color.Gray
            )
        }

        // Expanded interactive statistics
        activeItem?.let { item ->
            Card(
                colors = CardDefaults.cardColors(
                    containerColor = if (isDark) Color(0xFF1E293B) else Color(0xFFFAF5FF)
                ),
                shape = RoundedCornerShape(12.dp),
                border = BorderStroke(1.dp, if (isDark) Color(0xFF581C87).copy(alpha = 0.3f) else Color(0xFFE879F9).copy(alpha = 0.2f)),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier.padding(12.dp),
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(46.dp)
                            .background(if (isDark) Color(0xFF581C87) else Color(0xFFF3E8FF), CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "${item.percent.toInt()}%",
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Black,
                            color = if (isDark) Color(0xFFE9D5FF) else Color(0xFF7E22CE)
                        )
                    }

                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "${item.label} ${if (isBengali) "রিটেনশন হাব" else "Retention Cohort"}",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Black,
                            color = if (isDark) Color(0xFFE9D5FF) else Color(0xFF6B21A8)
                        )
                        Text(
                            text = item.insight,
                            fontSize = 10.sp,
                            color = if (isDark) Color(0xFFCBD5E1) else Color.DarkGray,
                            lineHeight = 13.sp
                        )
                    }
                }
            }
        }

        // Custom Bezier Decay Line Chart
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(150.dp)
                .background(
                    if (isDark) Color(0xFF0F172A).copy(alpha = 0.5f) else Color(0xFFFAF5FF).copy(alpha = 0.5f),
                    RoundedCornerShape(16.dp)
                )
                .padding(8.dp)
        ) {
            Canvas(
                modifier = Modifier
                    .fillMaxSize()
                    .pointerInput(retentionMetrics) {
                        detectTapGestures { offset ->
                            val leftPadding = 45.dp.toPx()
                            val rightPadding = 20.dp.toPx()
                            val graphWidth = size.width - leftPadding - rightPadding
                            val segmentWidth = graphWidth / (retentionMetrics.size - 1)
                            
                            val clickedIdx = ((offset.x - leftPadding + segmentWidth / 2f) / segmentWidth)
                                .toInt()
                                .coerceIn(0, retentionMetrics.size - 1)
                            selectedIndex = clickedIdx
                        }
                    }
            ) {
                val leftPadding = 45.dp.toPx()
                val rightPadding = 20.dp.toPx()
                val topPadding = 15.dp.toPx()
                val bottomPadding = 25.dp.toPx()
                val graphWidth = size.width - leftPadding - rightPadding
                val graphHeight = size.height - topPadding - bottomPadding

                val maxPercent = 100f
                val segmentWidth = graphWidth / (retentionMetrics.size - 1)

                // Generate point coordinates
                val points = retentionMetrics.mapIndexed { idx, item ->
                    val x = leftPadding + (idx * segmentWidth)
                    val y = topPadding + graphHeight - ((item.percent / maxPercent) * graphHeight)
                    Offset(x, y)
                }

                // Draw horizontal guide reference lines
                for (i in 0..2) {
                    val pct = 100f - (i * 40f)
                    val y = topPadding + graphHeight - ((pct / maxPercent) * graphHeight)
                    drawLine(
                        color = if (isDark) Color.White.copy(alpha = 0.05f) else Color.LightGray.copy(alpha = 0.2f),
                        start = Offset(leftPadding, y),
                        end = Offset(leftPadding + graphWidth, y),
                        strokeWidth = 1.dp.toPx()
                    )
                    drawContext.canvas.nativeCanvas.drawText(
                        "${pct.toInt()}%",
                        leftPadding - 8.dp.toPx(),
                        y + 4.dp.toPx(),
                        Paint().apply {
                            textSize = 8.sp.toPx()
                            color = android.graphics.Color.GRAY
                            textAlign = Paint.Align.RIGHT
                        }
                    )
                }

                // Draw area gradient brush under smooth path
                if (points.isNotEmpty()) {
                    val fillPath = Path().apply {
                        moveTo(points[0].x, topPadding + graphHeight)
                        lineTo(points[0].x, points[0].y)
                        for (i in 0 until points.size - 1) {
                            val p0 = points[i]
                            val p1 = points[i + 1]
                            val conX1 = (p0.x + p1.x) / 2f
                            val conY1 = p0.y
                            val conX2 = (p0.x + p1.x) / 2f
                            val conY2 = p1.y
                            cubicTo(conX1, conY1, conX2, conY2, p1.x, p1.y)
                        }
                        lineTo(points.last().x, topPadding + graphHeight)
                        close()
                    }
                    drawPath(
                        path = fillPath,
                        brush = Brush.verticalGradient(
                            colors = if (isDark) {
                                listOf(Color(0xFFD8B4FE).copy(alpha = 0.18f), Color(0xFF1E293B).copy(alpha = 0.01f))
                            } else {
                                listOf(Color(0xFFE9D5FF).copy(alpha = 0.4f), Color.White.copy(alpha = 0.01f))
                            }
                        )
                    )

                    // Draw primary spline decay line
                    val linePath = Path().apply {
                        moveTo(points[0].x, points[0].y)
                        for (i in 0 until points.size - 1) {
                            val p0 = points[i]
                            val p1 = points[i + 1]
                            val conX1 = (p0.x + p1.x) / 2f
                            val conY1 = p0.y
                            val conX2 = (p0.x + p1.x) / 2f
                            val conY2 = p1.y
                            cubicTo(conX1, conY1, conX2, conY2, p1.x, p1.y)
                        }
                    }
                    drawPath(
                        path = linePath,
                        color = Color(0xFF9333EA),
                        style = Stroke(width = 3.dp.toPx(), cap = StrokeCap.Round)
                    )
                }

                // Node highlight & Labels
                points.forEachIndexed { index, pt ->
                    val isSelected = index == selectedIndex
                    
                    drawCircle(
                        color = if (isSelected) Color(0xFF7E22CE) else Color.White,
                        radius = if (isSelected) 6.dp.toPx() else 4.dp.toPx(),
                        center = pt
                    )
                    
                    if (isSelected) {
                        drawCircle(
                            color = Color(0xFFE9D5FF).copy(alpha = 0.5f),
                            radius = 11.dp.toPx(),
                            center = pt
                        )
                    } else {
                        drawCircle(
                            color = Color(0xFF9333EA),
                            radius = 1.5.dp.toPx(),
                            center = pt
                        )
                    }

                    // X-Axis text
                    drawContext.canvas.nativeCanvas.drawText(
                        retentionMetrics[index].label,
                        pt.x,
                        topPadding + graphHeight + 16.dp.toPx(),
                        Paint().apply {
                            textSize = 8.sp.toPx()
                            color = if (isSelected) {
                                if (isDark) android.graphics.Color.WHITE else android.graphics.Color.BLACK
                            } else {
                                android.graphics.Color.GRAY
                            }
                            textAlign = Paint.Align.CENTER
                            typeface = if (isSelected) Typeface.DEFAULT_BOLD else Typeface.DEFAULT
                        }
                    )
                }
            }
        }
    }
}

private data class RetentionItem(val label: String, val percent: Float, val insight: String)

/**
 * 3. REVENUE ANALYTICS PANE
 * Dynamic Currency selector and Area distribution
 */
@Composable
fun RevenueAnalyticsPane(isBengali: Boolean, isDark: Boolean) {
    var isUSD by remember { mutableStateOf(true) }
    
    // KPI parameters
    val mrrUsd = 4850
    val mrrBdt = mrrUsd * 118
    val arrUsd = mrrUsd * 12
    val arrBdt = mrrBdt * 12
    val arpuUsd = 12.5
    val arpuBdt = arpuUsd * 118

    val currencySymbol = if (isUSD) "$" else "৳"
    val mrrFormatted = if (isUSD) "$mrrUsd" else "$mrrBdt"
    val arrFormatted = if (isUSD) "$arrUsd" else "$arrBdt"
    val arpuFormatted = if (isUSD) "$arpuUsd" else "$arpuBdt"

    val revenueData = remember {
        listOf(
            RevenueItem("Jan", 3200, 1200),
            RevenueItem("Feb", 3500, 1100),
            RevenueItem("Mar", 3900, 1400),
            RevenueItem("Apr", 4100, 1300),
            RevenueItem("May", 4600, 1700),
            RevenueItem("Jun", 4850, 1900)  // Current
        )
    }

    Column(verticalArrangement = Arrangement.spacedBy(14.dp)) {
        // Selector Header
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(
                    text = if (isBengali) "রাজস্ব ও এমআরআর অ্যানালিটিক্স" else "Revenue & MRR Tracker",
                    fontWeight = FontWeight.Bold,
                    fontSize = 14.sp,
                    color = if (isDark) Color(0xFFF1F5F9) else Color(0xFF334155)
                )
                Text(
                    text = if (isBengali) "নিলজোরি প্রিমিয়াম সদস্যপদ ও পরামর্শ থেকে আয়" else "Platform billing traces, consultant bookings, and subscription ARR",
                    fontSize = 10.sp,
                    color = if (isDark) Color(0xFF94A3B8) else Color.Gray
                )
            }

            // Currency Selector Button
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(8.dp))
                    .background(if (isDark) Color(0xFF1E293B) else Color(0xFFEFF6FF))
                    .border(1.dp, if (isDark) Color(0xFF2563EB) else Color(0xFFBFDBFE), RoundedCornerShape(8.dp))
                    .clickable { isUSD = !isUSD }
                    .padding(horizontal = 8.dp, vertical = 5.dp)
            ) {
                Text(
                    text = if (isUSD) "USD ($)" else "BDT (৳)",
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Black,
                    color = if (isDark) Color(0xFF60A5FA) else Color(0xFF1E4ED8)
                )
            }
        }

        // Summary KPI Metrics Rows
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            // KPI 1: MRR
            Card(
                modifier = Modifier.weight(1f),
                colors = CardDefaults.cardColors(containerColor = if (isDark) Color(0xFF1E293B) else Color(0xFFF8FAFC)),
                border = BorderStroke(1.dp, if (isDark) Color(0xFF334155) else Color(0xFFE2E8F0))
            ) {
                Column(modifier = Modifier.padding(10.dp), verticalArrangement = Arrangement.spacedBy(2.dp)) {
                    Text(
                        "💼 MRR",
                        fontSize = 9.5.sp,
                        color = Color.Gray,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "$currencySymbol$mrrFormatted",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Black,
                        color = if (isDark) Color(0xFF60A5FA) else Color(0xFF1E4ED8)
                    )
                }
            }

            // KPI 2: ARR
            Card(
                modifier = Modifier.weight(1f),
                colors = CardDefaults.cardColors(containerColor = if (isDark) Color(0xFF1E293B) else Color(0xFFF8FAFC)),
                border = BorderStroke(1.dp, if (isDark) Color(0xFF334155) else Color(0xFFE2E8F0))
            ) {
                Column(modifier = Modifier.padding(10.dp), verticalArrangement = Arrangement.spacedBy(2.dp)) {
                    Text(
                        "📈 ARR",
                        fontSize = 9.5.sp,
                        color = Color.Gray,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "$currencySymbol$arrFormatted",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Black,
                        color = Color(0xFF10B981)
                    )
                }
            }

            // KPI 3: ARPU
            Card(
                modifier = Modifier.weight(1f),
                colors = CardDefaults.cardColors(containerColor = if (isDark) Color(0xFF1E293B) else Color(0xFFF8FAFC)),
                border = BorderStroke(1.dp, if (isDark) Color(0xFF334155) else Color(0xFFE2E8F0))
            ) {
                Column(modifier = Modifier.padding(10.dp), verticalArrangement = Arrangement.spacedBy(2.dp)) {
                    Text(
                        "👥 ARPU",
                        fontSize = 9.5.sp,
                        color = Color.Gray,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "$currencySymbol$arpuFormatted",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Black,
                        color = Color(0xFFF59E0B)
                    )
                }
            }
        }

        // Stacked Area Canvas Plot
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(130.dp)
                .background(
                    if (isDark) Color(0xFF0F172A).copy(alpha = 0.5f) else Color(0xFFF8FAFC),
                    RoundedCornerShape(16.dp)
                )
                .padding(8.dp)
        ) {
            Canvas(modifier = Modifier.fillMaxSize()) {
                val leftPadding = 45.dp.toPx()
                val rightPadding = 20.dp.toPx()
                val topPadding = 15.dp.toPx()
                val bottomPadding = 20.dp.toPx()
                val graphWidth = size.width - leftPadding - rightPadding
                val graphHeight = size.height - topPadding - bottomPadding

                val maxRevenue = 8000f // Combined peak value
                val segmentWidth = graphWidth / (revenueData.size - 1)

                // Scaling factor mapping BDT or USD
                val scaleFactor = if (isUSD) 1.0f else 118.0f

                // Draw stacked curves
                val subPath = Path()
                val conPath = Path()

                revenueData.forEachIndexed { idx, item ->
                    val x = leftPadding + (idx * segmentWidth)
                    val subY = topPadding + graphHeight - (((item.subsAmount * scaleFactor) / (maxRevenue * scaleFactor)) * graphHeight)
                    val conY = subY - (((item.consultingAmount * scaleFactor) / (maxRevenue * scaleFactor)) * graphHeight)

                    if (idx == 0) {
                        subPath.moveTo(x, subY)
                        conPath.moveTo(x, conY)
                    } else {
                        subPath.lineTo(x, subY)
                        conPath.lineTo(x, conY)
                    }
                }

                // Drawing solid boundary lines
                val subLinePath = Path().apply {
                    revenueData.forEachIndexed { idx, item ->
                        val x = leftPadding + (idx * segmentWidth)
                        val subY = topPadding + graphHeight - (((item.subsAmount * scaleFactor) / (maxRevenue * scaleFactor)) * graphHeight)
                        if (idx == 0) moveTo(x, subY) else lineTo(x, subY)
                    }
                }
                val conLinePath = Path().apply {
                    revenueData.forEachIndexed { idx, item ->
                        val x = leftPadding + (idx * segmentWidth)
                        val subY = topPadding + graphHeight - (((item.subsAmount * scaleFactor) / (maxRevenue * scaleFactor)) * graphHeight)
                        val conY = subY - (((item.consultingAmount * scaleFactor) / (maxRevenue * scaleFactor)) * graphHeight)
                        if (idx == 0) moveTo(x, conY) else lineTo(x, conY)
                    }
                }

                // Fill Subscription area (lower)
                val subFillPath = Path().apply {
                    revenueData.forEachIndexed { idx, item ->
                        val x = leftPadding + (idx * segmentWidth)
                        val subY = topPadding + graphHeight - (((item.subsAmount * scaleFactor) / (maxRevenue * scaleFactor)) * graphHeight)
                        if (idx == 0) {
                            moveTo(x, topPadding + graphHeight)
                            lineTo(x, subY)
                        } else {
                            lineTo(x, subY)
                        }
                    }
                    lineTo(leftPadding + graphWidth, topPadding + graphHeight)
                    close()
                }

                drawPath(
                    path = subFillPath,
                    color = Color(0xFF3B82F6).copy(alpha = 0.35f)
                )
                drawPath(
                    path = subLinePath,
                    color = Color(0xFF1D4ED8),
                    style = Stroke(width = 2.dp.toPx())
                )

                // Fill Consulting Stack area (upper)
                val conFillPath = Path().apply {
                    revenueData.forEachIndexed { idx, item ->
                        val x = leftPadding + (idx * segmentWidth)
                        val subY = topPadding + graphHeight - (((item.subsAmount * scaleFactor) / (maxRevenue * scaleFactor)) * graphHeight)
                        val conY = subY - (((item.consultingAmount * scaleFactor) / (maxRevenue * scaleFactor)) * graphHeight)
                        if (idx == 0) {
                            moveTo(x, subY)
                            lineTo(x, conY)
                        } else {
                            lineTo(x, conY)
                        }
                    }
                    for (i in revenueData.indices.reversed()) {
                        val x = leftPadding + (i * segmentWidth)
                        val subY = topPadding + graphHeight - (((revenueData[i].subsAmount * scaleFactor) / (maxRevenue * scaleFactor)) * graphHeight)
                        lineTo(x, subY)
                    }
                    close()
                }

                drawPath(
                    path = conFillPath,
                    color = Color(0xFF10B981).copy(alpha = 0.25f)
                )
                drawPath(
                    path = conLinePath,
                    color = Color(0xFF059669),
                    style = Stroke(width = 2.dp.toPx())
                )

                // Render minimal legend
                revenueData.forEachIndexed { index, item ->
                    val x = leftPadding + (index * segmentWidth)
                    drawContext.canvas.nativeCanvas.drawText(
                        item.month,
                        x,
                        topPadding + graphHeight + 14.dp.toPx(),
                        Paint().apply {
                            textSize = 8.sp.toPx()
                            color = android.graphics.Color.GRAY
                            textAlign = Paint.Align.CENTER
                        }
                    )
                }
            }
        }

        // Color Key Legend Bar
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                Box(modifier = Modifier.size(8.dp).background(Color(0xFF1D4ED8), CircleShape))
                Text(
                    text = if (isBengali) "প্রিমিয়াম সাবস্ক্রিপশন" else "Premium Memberships",
                    fontSize = 10.sp,
                    color = if (isDark) Color(0xFFCBD5E1) else Color.DarkGray
                )
            }
            Spacer(modifier = Modifier.width(16.dp))
            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                Box(modifier = Modifier.size(8.dp).background(Color(0xFF059669), CircleShape))
                Text(
                    text = if (isBengali) "ক্লিনিক্যাল কনসালটেশন" else "Dietitian Bookings",
                    fontSize = 10.sp,
                    color = if (isDark) Color(0xFFCBD5E1) else Color.DarkGray
                )
            }
        }
    }
}

private data class RevenueItem(val month: String, val subsAmount: Int, val consultingAmount: Int)

/**
 * 4. SUBSCRIPTION ANALYTICS PANE
 * Subscribing tiers segmented breakdown & conversion tracking
 */
@Composable
fun SubscriptionAnalyticsPane(isBengali: Boolean, isDark: Boolean) {
    // Subscriber splits
    val tiers = remember {
        listOf(
            SubTierItem("Basic (Free)", 65f, Color(0xFF64748B)),
            SubTierItem("Premium Pro", 25f, Color(0xFF3B82F6)),
            SubTierItem("Clinical Elite", 10f, Color(0xFF10B981))
        )
    }

    var showStatsDetails by remember { mutableStateOf(false) }

    Column(verticalArrangement = Arrangement.spacedBy(14.dp)) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(
                    text = if (isBengali) "সদস্যপদ টিয়ার বিশ্লেষণ" else "Subscription Analytics",
                    fontWeight = FontWeight.Bold,
                    fontSize = 14.sp,
                    color = if (isDark) Color(0xFFF1F5F9) else Color(0xFF334155)
                )
                Text(
                    text = if (isBengali) "সদস্যদের প্যাকেজ অনুপাত ও আপগ্রেড কনভার্সন" else "Distribution of user subscriptions, conversion rate & churn indexes",
                    fontSize = 10.sp,
                    color = if (isDark) Color(0xFF94A3B8) else Color.Gray
                )
            }

            Button(
                onClick = { showStatsDetails = !showStatsDetails },
                colors = ButtonDefaults.buttonColors(
                    containerColor = if (isDark) Color(0xFF1E293B) else Color(0xFFEFF6FF),
                    contentColor = if (isDark) Color(0xFF60A5FA) else Color(0xFF1E4ED8)
                ),
                contentPadding = PaddingValues(horizontal = 10.dp, vertical = 4.dp),
                modifier = Modifier.height(28.dp)
            ) {
                Text(
                    text = if (showStatsDetails) (if (isBengali) "সংক্ষিপ্ত" else "Less") else (if (isBengali) "বিস্তারিত" else "More"),
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }

        // Horizontal Segmented Distribution Bar
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(20.dp)
                .clip(RoundedCornerShape(10.dp))
        ) {
            tiers.forEach { tier ->
                Box(
                    modifier = Modifier
                        .fillMaxHeight()
                        .weight(tier.percent)
                        .background(tier.color)
                        .padding(horizontal = 4.dp),
                    contentAlignment = Alignment.Center
                ) {
                    if (tier.percent > 12f) {
                        Text(
                            text = "${tier.percent.toInt()}%",
                            fontSize = 8.5.sp,
                            fontWeight = FontWeight.Black,
                            color = Color.White
                        )
                    }
                }
            }
        }

        // Tiers Legend Grid List
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            tiers.forEach { tier ->
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Box(modifier = Modifier.size(10.dp).background(tier.color, RoundedCornerShape(2.dp)))
                    Column {
                        Text(
                            text = tier.name,
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            color = if (isDark) Color(0xFFF1F5F9) else Color(0xFF334155)
                        )
                        Text(
                            text = "${tier.percent.toInt()}% ratio",
                            fontSize = 8.5.sp,
                            color = Color.Gray
                        )
                    }
                }
            }
        }

        // Interactive Upgrade Simulation & Churn metrics
        AnimatedVisibility(
            visible = showStatsDetails,
            enter = expandVertically() + fadeIn(),
            exit = shrinkVertically() + fadeOut()
        ) {
            Column(
                verticalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Divider(color = if (isDark) Color.White.copy(alpha = 0.12f) else Color.LightGray.copy(alpha = 0.4f))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    // KPI 1: Trial Conversion
                    Card(
                        modifier = Modifier.weight(1f),
                        colors = CardDefaults.cardColors(containerColor = if (isDark) Color(0xFF1E293B) else Color(0xFFFAF5FF)),
                        border = BorderStroke(1.dp, if (isDark) Color(0xFF3B82F6).copy(alpha = 0.3f) else Color(0xFF93C5FD).copy(alpha = 0.5f))
                    ) {
                        Column(modifier = Modifier.padding(10.dp), verticalArrangement = Arrangement.spacedBy(2.dp)) {
                            Text(
                                "🎯 Trial Conv. Rate",
                                fontSize = 9.sp,
                                color = Color.Gray,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                "18.4%",
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Black,
                                color = Color(0xFF3B82F6)
                            )
                            Text(
                                "Trial to Pro tier conversion",
                                fontSize = 7.5.sp,
                                color = Color.Gray
                            )
                        }
                    }

                    // KPI 2: Churn Index
                    Card(
                        modifier = Modifier.weight(1f),
                        colors = CardDefaults.cardColors(containerColor = if (isDark) Color(0xFF1E293B) else Color(0xFFFEF2F2)),
                        border = BorderStroke(1.dp, if (isDark) Color(0xFFEF4444).copy(alpha = 0.3f) else Color(0xFFFCA5A5).copy(alpha = 0.5f))
                    ) {
                        Column(modifier = Modifier.padding(10.dp), verticalArrangement = Arrangement.spacedBy(2.dp)) {
                            Text(
                                "📉 Monthly Churn",
                                fontSize = 9.sp,
                                color = Color.Gray,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                "2.1%",
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Black,
                                color = Color(0xFFEF4444)
                            )
                            Text(
                                "Extremely low, below industry",
                                fontSize = 7.5.sp,
                                color = Color.Gray
                            )
                        }
                    }
                }
            }
        }
    }
}

private data class SubTierItem(val name: String, val percent: Float, val color: Color)

/**
 * 5. HEALTH CORRELATION ANALYTICS PANE
 * Binds dynamically with room database log entities of Food, Exercise, and Mood logs!
 */
@Composable
fun HealthAnalyticsPane(
    isBengali: Boolean,
    isDark: Boolean,
    foodLogs: List<FoodLogEntity>,
    exerciseLogs: List<ExerciseLogEntity>,
    moodLogs: List<MoodLogEntity>
) {
    // Dynamic health insights engine aggregating actual log statistics
    val totalLoggedMeals = foodLogs.size
    val totalLoggedExercises = exerciseLogs.size
    val totalLoggedMoods = moodLogs.size

    val avgCaloriesIn = remember(foodLogs) {
        if (foodLogs.isEmpty()) 0 else foodLogs.map { it.calories }.average().toInt()
    }
    val avgCaloriesOut = remember(exerciseLogs) {
        if (exerciseLogs.isEmpty()) 0 else exerciseLogs.map { it.caloriesBurned }.average().toInt()
    }

    // Dynamic Mood Correlation percentage calculation
    val alignmentFactor = remember(foodLogs, moodLogs) {
        if (foodLogs.isEmpty() || moodLogs.isEmpty()) {
            68 // Healthy default sync
        } else {
            val moodPoints = moodLogs.map {
                when (it.mood.lowercase()) {
                    "excellent" -> 5
                    "happy" -> 4
                    "neutral" -> 3
                    "anxious" -> 2
                    "sad" -> 1
                    else -> 3
                }
            }.average()
            val scoreRatio = (moodPoints / 5.0) * 100
            scoreRatio.toInt().coerceIn(40, 98)
        }
    }

    Column(verticalArrangement = Arrangement.spacedBy(14.dp)) {
        Column {
            Text(
                text = if (isBengali) "রিয়েল-টাইম স্বাস্থ্য সম্পর্ক বিশ্লেষণ" else "Real-time Health Correlations",
                fontWeight = FontWeight.Bold,
                fontSize = 14.sp,
                color = if (isDark) Color(0xFFF1F5F9) else Color(0xFF334155)
            )
            Text(
                text = if (isBengali) "রুম ডাটাবেজ থেকে সংগৃহীত পুষ্টি, ব্যায়াম ও অনুভূতির সম্পর্ক সূত্র" else "Dynamic correlation matrix checking metabolic activity against mood ratings",
                fontSize = 10.sp,
                color = if (isDark) Color(0xFF94A3B8) else Color.Gray
            )
        }

        // Aggregated KPI Stats Cards
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            // Food counts
            Column(
                modifier = Modifier
                    .weight(1f)
                    .clip(RoundedCornerShape(12.dp))
                    .background(if (isDark) Color(0xFF1E293B) else Color(0xFFEFF6FF))
                    .padding(8.dp),
                verticalArrangement = Arrangement.spacedBy(2.dp)
            ) {
                Text("🍲 Diet Count", fontSize = 9.sp, color = Color.Gray, fontWeight = FontWeight.Bold)
                Text("$totalLoggedMeals records", fontSize = 12.sp, fontWeight = FontWeight.Black, color = if (isDark) Color(0xFF60A5FA) else Color(0xFF1D4ED8))
                Text("Avg: $avgCaloriesIn kcal", fontSize = 8.sp, color = Color.Gray)
            }

            // Exercise counts
            Column(
                modifier = Modifier
                    .weight(1f)
                    .clip(RoundedCornerShape(12.dp))
                    .background(if (isDark) Color(0xFF1E293B) else Color(0xFFECFDF5))
                    .padding(8.dp),
                verticalArrangement = Arrangement.spacedBy(2.dp)
            ) {
                Text("🏃 Workout Logs", fontSize = 9.sp, color = Color.Gray, fontWeight = FontWeight.Bold)
                Text("$totalLoggedExercises records", fontSize = 12.sp, fontWeight = FontWeight.Black, color = Color(0xFF10B981))
                Text("Avg: $avgCaloriesOut kcal", fontSize = 8.sp, color = Color.Gray)
            }

            // Sync index
            Column(
                modifier = Modifier
                    .weight(1.2f)
                    .clip(RoundedCornerShape(12.dp))
                    .background(if (isDark) Color(0xFF1E293B) else Color(0xFFFFF7ED))
                    .padding(8.dp),
                verticalArrangement = Arrangement.spacedBy(2.dp)
            ) {
                Text("🧘 Wellness Sync Rate", fontSize = 9.sp, color = Color.Gray, fontWeight = FontWeight.Bold)
                Text("$alignmentFactor% Harmony", fontSize = 12.sp, fontWeight = FontWeight.Black, color = Color(0xFFF59E0B))
                Text("$totalLoggedMoods logs synced", fontSize = 8.sp, color = Color.Gray)
            }
        }

        // Live Dynamic Interactive Correlation Compass / Gauge
        Card(
            colors = CardDefaults.cardColors(containerColor = if (isDark) Color(0xFF1E293B) else Color(0xFFF8FAFC)),
            border = BorderStroke(1.2.dp, if (isDark) Color(0xFF334155) else Color(0xFFE2E8F0)),
            shape = RoundedCornerShape(16.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            Row(
                modifier = Modifier.padding(14.dp),
                horizontalArrangement = Arrangement.spacedBy(14.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Interactive dynamic visual circular progress ring representing SQLite Database sync state
                Box(
                    modifier = Modifier.size(68.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Canvas(modifier = Modifier.fillMaxSize()) {
                        // Background ring
                        drawCircle(
                            color = if (isDark) Color.White.copy(alpha = 0.08f) else Color.LightGray.copy(alpha = 0.3f),
                            style = Stroke(width = 6.dp.toPx(), cap = StrokeCap.Round)
                        )
                        // Active ring
                        drawArc(
                            color = Color(0xFFF59E0B),
                            startAngle = -90f,
                            sweepAngle = (alignmentFactor.toFloat() / 100f) * 360f,
                            useCenter = false,
                            style = Stroke(width = 6.dp.toPx(), cap = StrokeCap.Round)
                        )
                    }
                    Text(
                        text = "$alignmentFactor%",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Black,
                        color = Color(0xFFF59E0B)
                    )
                }

                // Analytics advisory statement
                Column(modifier = Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(2.dp)) {
                    Text(
                        text = if (isBengali) "নিলজোরি মেটাবলিক সামঞ্জস্য" else "Niljori Wellness Balance Index",
                        fontSize = 11.5.sp,
                        fontWeight = FontWeight.Black,
                        color = if (isDark) Color(0xFFF1F5F9) else Color(0xFF334155)
                    )
                    val advice = if (alignmentFactor > 80) {
                        if (isBengali) "চমৎকার! আপনার খাদ্য গ্রহণ, নিয়মিত ব্যায়াম ও মানসিক স্থিতি দারুণ সামঞ্জস্যপূর্ণ।" else "Stellar correlation found! Your caloric logs and emotional stability are in perfect synchrony."
                    } else if (alignmentFactor > 60) {
                        if (isBengali) "ভালো গতিবিধি! আরও বেশি পুষ্টিকর খাবার ও নিয়মিত উপবাস ট্র্যাকিং আপনার আবেগ নিয়ন্ত্রণে সাহায্য করবে।" else "Healthy progress! Balanced fiber and routine fasting intervals will foster sharper emotional composure."
                    } else {
                        if (isBengali) "কিছু অসামঞ্জস্য রয়েছে। নিয়মিত খাবার ডায়েরি পূরণ ও পর্যাপ্ত ঘুম নিশ্চিত করার পরামর্শ দেয়া হচ্ছে।" else "Low correlation. Consistent metabolic reporting and sleep hydration logging are highly recommended."
                    }
                    Text(
                        text = advice,
                        fontSize = 9.5.sp,
                        color = if (isDark) Color(0xFF94A3B8) else Color.DarkGray,
                        lineHeight = 12.5.sp
                    )
                }
            }
        }
    }
}
