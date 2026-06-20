package com.example.ui

import android.graphics.Paint
import android.graphics.Typeface
import androidx.compose.animation.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.gestures.detectTapGestures
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
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.PathEffect
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
import com.example.data.model.MoodLogEntity
import com.example.data.model.FoodLogEntity
import com.example.data.model.ExerciseLogEntity
import com.example.data.model.WeightLogEntity
import java.text.SimpleDateFormat
import java.util.*

@Composable
fun MoodAnalysisDashboard(
    viewModel: DietPlannerViewModel,
    isBengali: Boolean,
    modifier: Modifier = Modifier
) {
    // Timeframe selector state: 7, 14, or 30 days
    var selectedDaysLimit by remember { mutableStateOf(7) }
    
    // Core series toggles inspired by Recharts interactive legends
    var showMood by remember { mutableStateOf(true) }
    var showCaloriesIn by remember { mutableStateOf(true) }
    var showWorkout by remember { mutableStateOf(true) }
    var showWeight by remember { mutableStateOf(false) }

    // Collect Room database log feeds
    val moodLogs by viewModel.currentMoodLogs.collectAsState()
    val foodLogs by viewModel.allFoodLogs.collectAsState()
    val exerciseLogs by viewModel.allExerciseLogs.collectAsState()
    val weightLogs by viewModel.allWeightLogs.collectAsState()

    // Dynamically calculate past date list based on selected timeframe
    val dateList = remember(selectedDaysLimit) {
        val list = mutableListOf<Date>()
        val cal = Calendar.getInstance()
        cal.add(Calendar.DAY_OF_YEAR, -(selectedDaysLimit - 1))
        for (i in 0 until selectedDaysLimit) {
            list.add(cal.time)
            cal.add(Calendar.DAY_OF_YEAR, 1)
        }
        list
    }

    val ymdFormat = remember { SimpleDateFormat("yyyy-MM-dd", Locale.ROOT) }
    val dayFormatEn = remember { SimpleDateFormat("EEE", Locale.ENGLISH) }
    val dayFormatBn = remember { SimpleDateFormat("EEE", Locale("bn", "BD")) }

    // Map raw data streams to a highly uniform daily layout
    val chartData = remember(moodLogs, foodLogs, exerciseLogs, weightLogs, dateList, selectedDaysLimit, isBengali) {
        dateList.map { date ->
            val dateStr = ymdFormat.format(date)
            
            // Format labels intelligently based on scale limit
            val label = if (selectedDaysLimit > 7) {
                val shortFormat = if (isBengali) {
                    SimpleDateFormat("d MMM", Locale("bn", "BD"))
                } else {
                    SimpleDateFormat("MMM d", Locale.ENGLISH)
                }
                shortFormat.format(date)
            } else {
                if (isBengali) dayFormatBn.format(date) else dayFormatEn.format(date)
            }

            // Filter data entries
            val moodsOnDay = moodLogs.filter { it.date == dateStr }
            val foodsOnDay = foodLogs.filter { it.date == dateStr }
            val exercisesOnDay = exerciseLogs.filter { it.date == dateStr }
            val weightsOnDay = weightLogs.filter { it.date == dateStr }

            val latestMoodLog = moodsOnDay.lastOrNull()
            val moodString = latestMoodLog?.mood ?: ""
            val moodNote = latestMoodLog?.note ?: ""

            // Mood score scale 1 to 5
            val moodScore = if (moodString.isNotBlank()) {
                when (moodString.lowercase(Locale.ROOT)) {
                    "excellent", "happy" -> 5f
                    "good", "energized", "calm" -> 4f
                    "normal", "neutral" -> 3f
                    "anxious", "stressed", "tired" -> 2f
                    "sad", "angry" -> 1f
                    else -> 3f
                }
            } else {
                null
            }

            val hasMeal = foodsOnDay.isNotEmpty()
            val caloriesIn = if (hasMeal) foodsOnDay.sumOf { it.calories }.toFloat() else 0f

            val hasExercise = exercisesOnDay.isNotEmpty()
            val caloriesOut = if (hasExercise) exercisesOnDay.sumOf { it.caloriesBurned }.toFloat() else 0f

            val latestWeight = if (weightsOnDay.isNotEmpty()) weightsOnDay.last().weight.toFloat() else null

            HealthTrendItem(
                dateStr = dateStr,
                label = label,
                moodScore = moodScore,
                moodNote = moodNote,
                hasMeal = hasMeal,
                caloriesIn = caloriesIn,
                hasExercise = hasExercise,
                caloriesOut = caloriesOut,
                weight = latestWeight
            )
        }
    }

    // Selected node index for interactive scrub popover (defaults to the last item)
    var selectedDayIndex by remember(selectedDaysLimit, chartData) { 
        mutableStateOf(chartData.size - 1) 
    }
    
    // Safety check selected index is inside bounds
    val safeSelectedIndex = selectedDayIndex.coerceIn(0, chartData.size - 1)
    val selectedItem = chartData.getOrNull(safeSelectedIndex)

    // Compute dynamic scaling ceiling across the logs to optimize chart density
    val maxCaloriesIn = remember(chartData) {
        val maxVal = chartData.maxOfOrNull { it.caloriesIn } ?: 0f
        if (maxVal < 1500f) 2500f else maxVal * 1.15f
    }
    val maxCaloriesOut = remember(chartData) {
        val maxVal = chartData.maxOfOrNull { it.caloriesOut } ?: 0f
        if (maxVal < 400f) 800f else maxVal * 1.15f
    }
    val minWeight = remember(chartData, weightLogs) {
        val loggedMin = chartData.filter { it.weight != null }.mapNotNull { it.weight }.minOrNull()
        if (loggedMin != null) (loggedMin - 5f).coerceAtLeast(30f) else 55f
    }
    val maxWeight = remember(chartData, weightLogs) {
        val loggedMax = chartData.filter { it.weight != null }.mapNotNull { it.weight }.maxOrNull()
        if (loggedMax != null) loggedMax + 5f else 95f
    }

    Card(
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        modifier = modifier
            .fillMaxWidth()
            .testTag("mood_analysis_dashboard_card")
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Header Section
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
                            .size(40.dp)
                            .background(Color(0xFFF3E5F5), CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Text("📊", fontSize = 20.sp)
                    }
                    Column {
                        Text(
                            text = if (isBengali) "মনোভাব ও স্বাস্থ্য পর্যবেক্ষণ ড্যাশবোর্ড" else "Recharts Health Analytics",
                            fontWeight = FontWeight.Bold,
                            fontSize = 16.sp,
                            color = Color(0xFF4A148C)
                        )
                        Text(
                            text = if (isBengali) "ক্যালরি, ওজন এবং মানসিক উদ্দীপনার দীর্ঘমেয়াদী সামঞ্জস্য" else "Long-term trends of calories, weight & mental velocity",
                            fontSize = 11.sp,
                            color = Color.Gray
                        )
                    }
                }

                Box(
                    modifier = Modifier
                        .background(Color(0xFFFFECEF), RoundedCornerShape(10.dp))
                        .padding(horizontal = 8.dp, vertical = 4.dp)
                ) {
                    Text(
                        text = if (isBengali) "অফলাইন সিঙ্ক" else "Room Analytics",
                        fontWeight = FontWeight.Bold,
                        fontSize = 9.sp,
                        color = Color(0xFFE91E63)
                    )
                }
            }

            // Timeframe Segmented Selection Control
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = if (isBengali) "বিশ্লেষণ সময়সীমা:" else "Timeframe Span:",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.DarkGray
                )
                
                Row(
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    listOf(7, 14, 30).forEach { limit ->
                        val isSelected = selectedDaysLimit == limit
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(20.dp))
                                .background(if (isSelected) Color(0xFFE1BEE7) else Color(0xFFF5F5F5))
                                .clickable { selectedDaysLimit = limit }
                                .padding(horizontal = 12.dp, vertical = 6.dp)
                        ) {
                            Text(
                                text = if (isBengali) "$limit দিন" else "$limit Days",
                                fontSize = 11.sp,
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                                color = if (isSelected) Color(0xFF4A148C) else Color.Gray
                            )
                        }
                    }
                }
            }

            // Series Filters (Recharts Dynamic Legend Toggle)
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color(0xFFFAF9FB), RoundedCornerShape(16.dp))
                    .padding(10.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Text(
                    text = if (isBengali) "গ্রাফে দেখানোর জন্য নির্বাচন করুন:" else "Interactive Series Filter (Toggle on/off):",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = Color.Gray,
                    modifier = Modifier.padding(bottom = 2.dp)
                )
                
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Mood series chip
                    InteractiveSeriesChip(
                        selected = showMood,
                        color = Color(0xFF8E24AA),
                        label = if (isBengali) "মনোভাব" else "Mood",
                        onClick = { showMood = !showMood }
                    )
                    // Calories Intake chip
                    InteractiveSeriesChip(
                        selected = showCaloriesIn,
                        color = Color(0xFF2E7D32),
                        label = if (isBengali) "ক্যালরি গ্রহণ" else "Calories In",
                        onClick = { showCaloriesIn = !showCaloriesIn }
                    )
                }
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Workout Burn chip
                    InteractiveSeriesChip(
                        selected = showWorkout,
                        color = Color(0xFFE65100),
                        label = if (isBengali) "ব্যায়াম ক্ষয়" else "Workout Burn",
                        onClick = { showWorkout = !showWorkout }
                    )
                    // Weight chip
                    InteractiveSeriesChip(
                        selected = showWeight,
                        color = Color(0xFF0288D1),
                        label = if (isBengali) "ওজন" else "Weight",
                        onClick = { showWeight = !showWeight }
                    )
                }
            }

            // Modern Recharts-inspired Multi-Series Canvas Plot
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(220.dp)
                    .background(Color(0xFFFAFAFA), RoundedCornerShape(18.dp))
                    .padding(horizontal = 6.dp, vertical = 10.dp)
            ) {
                Canvas(
                    modifier = Modifier
                        .fillMaxSize()
                        .pointerInput(chartData, selectedDaysLimit) {
                            // Support tap and drag gestures for high-fidelity scrub feedback
                            detectTapGestures { offset ->
                                val graphWidth = size.width - 90.dp.toPx()
                                val leftPadding = 50.dp.toPx()
                                val segmentWidth = graphWidth / (chartData.size - 1).coerceAtLeast(1)
                                val index = ((offset.x - leftPadding + segmentWidth / 2f) / segmentWidth)
                                    .toInt()
                                    .coerceIn(0, chartData.size - 1)
                                selectedDayIndex = index
                            }
                        }
                        .pointerInput(chartData, selectedDaysLimit) {
                            detectDragGestures { change, _ ->
                                change.consume()
                                val graphWidth = size.width - 90.dp.toPx()
                                val leftPadding = 50.dp.toPx()
                                val segmentWidth = graphWidth / (chartData.size - 1).coerceAtLeast(1)
                                val index = ((change.position.x - leftPadding + segmentWidth / 2f) / segmentWidth)
                                    .toInt()
                                    .coerceIn(0, chartData.size - 1)
                                selectedDayIndex = index
                            }
                        }
                ) {
                    val leftPadding = 50.dp.toPx()
                    val rightPadding = 40.dp.toPx()
                    val topPadding = 15.dp.toPx()
                    val bottomPadding = 30.dp.toPx()
                    val graphWidth = size.width - leftPadding - rightPadding
                    val graphHeight = size.height - topPadding - bottomPadding

                    // 1. Draw horizontal grid reference lines
                    val gridLinesCount = 4
                    for (i in 0..gridLinesCount) {
                        val y = topPadding + (i * (graphHeight / gridLinesCount))
                        drawLine(
                            color = Color(0xFFEBEBEB),
                            start = Offset(leftPadding, y),
                            end = Offset(leftPadding + graphWidth, y),
                            strokeWidth = 1.dp.toPx(),
                            pathEffect = PathEffect.dashPathEffect(floatArrayOf(12f, 12f), 0f)
                        )
                    }

                    // 2. Y-Axis Labels
                    val textPaint = Paint().apply {
                        textSize = 9.sp.toPx()
                        color = android.graphics.Color.GRAY
                        isAntiAlias = true
                        typeface = Typeface.DEFAULT
                    }
                    
                    // Left label markers (Y-Axis reference points depending on active view)
                    if (showMood) {
                        val moodSymbols = listOf("😢", "😰", "😐", "🙂", "😊")
                        for (i in 0..4) {
                            val y = topPadding + graphHeight - (i * (graphHeight / 4f))
                            drawContext.canvas.nativeCanvas.drawText(
                                moodSymbols[i],
                                leftPadding - 24.dp.toPx(),
                                y + 4.dp.toPx(),
                                Paint().apply {
                                    textSize = 12.sp.toPx()
                                }
                            )
                        }
                    } else if (showCaloriesIn) {
                        for (i in 0..4) {
                            val y = topPadding + graphHeight - (i * (graphHeight / 4f))
                            val calValue = ((maxCaloriesIn / 4f) * i).toInt()
                            drawContext.canvas.nativeCanvas.drawText(
                                "${calValue}k",
                                leftPadding - 32.dp.toPx(),
                                y + 4.dp.toPx(),
                                textPaint
                            )
                        }
                    } else {
                        // Standard generic percentage reference
                        for (i in 0..4) {
                            val y = topPadding + graphHeight - (i * (graphHeight / 4f))
                            drawContext.canvas.nativeCanvas.drawText(
                                "${i * 25}%",
                                leftPadding - 28.dp.toPx(),
                                y + 4.dp.toPx(),
                                textPaint
                            )
                        }
                    }

                    // segment width calculation
                    val segmentWidth = graphWidth / (chartData.size - 1).coerceAtLeast(1)

                    // 3. Coordinate mapping helper for drawing curves
                    val moodPoints = mutableListOf<Offset>()
                    val calInPoints = mutableListOf<Offset>()
                    val calOutPoints = mutableListOf<Offset>()
                    val weightPoints = mutableListOf<Offset>()

                    chartData.forEachIndexed { idx, item ->
                        val x = leftPadding + (idx * segmentWidth)
                        
                        // Mood mapped 1..5
                        val mScore = item.moodScore ?: 3f // fallback default intermediate
                        val moodY = topPadding + (5f - mScore) / 4f * graphHeight
                        moodPoints.add(Offset(x, moodY))

                        // Calorie In
                        val calInNorm = (item.caloriesIn / maxCaloriesIn).coerceIn(0f, 1f)
                        val calInY = topPadding + (1f - calInNorm) * graphHeight
                        calInPoints.add(Offset(x, calInY))

                        // Calorie Out
                        val calOutNorm = (item.caloriesOut / maxCaloriesOut).coerceIn(0f, 1f)
                        val calOutY = topPadding + (1f - calOutNorm) * graphHeight
                        calOutPoints.add(Offset(x, calOutY))

                        // Weight
                        val loggedWeight = item.weight ?: minWeight // fallback
                        val weightNorm = ((loggedWeight - minWeight) / (maxWeight - minWeight).coerceAtLeast(1f)).coerceIn(0f, 1f)
                        val weightY = topPadding + (1f - weightNorm) * graphHeight
                        weightPoints.add(Offset(x, weightY))
                    }

                    // 4. Draw Glow Area & Lines (Recharts look & feel)
                    
                    // Series A: Calories In (Green smooth gradient area/spline overlay)
                    if (showCaloriesIn) {
                        val greenBrush = Brush.verticalGradient(
                            colors = listOf(Color(0xFF4CAF50).copy(alpha = 0.22f), Color(0xFFE8F5E9).copy(alpha = 0.005f)),
                            startY = topPadding,
                            endY = topPadding + graphHeight
                        )
                        val areaPath = Path().apply {
                            moveTo(leftPadding, topPadding + graphHeight)
                            calInPoints.forEach { pt -> lineTo(pt.x, pt.ptY(topPadding + graphHeight)) }
                            lineTo(leftPadding + graphWidth, topPadding + graphHeight)
                            close()
                        }
                        drawPath(path = areaPath, brush = greenBrush)
                        
                        for (i in 0 until calInPoints.size - 1) {
                            drawLine(
                                color = Color(0xFF2E7D32),
                                start = calInPoints[i],
                                end = calInPoints[i + 1],
                                strokeWidth = 2.dp.toPx(),
                                cap = StrokeCap.Round
                            )
                        }
                    }

                    // Series B: Mood Line (Violet thick smooth spline curve with bottom glow shadow)
                    if (showMood) {
                        val violetBrush = Brush.verticalGradient(
                            colors = listOf(Color(0xFFD1C4E9).copy(alpha = 0.3f), Color(0xFFFAF0FF).copy(alpha = 0.01f)),
                            startY = topPadding,
                            endY = topPadding + graphHeight
                        )
                        val areaPath = Path().apply {
                            moveTo(leftPadding, topPadding + graphHeight)
                            moodPoints.forEach { pt -> lineTo(pt.x, pt.ptY(topPadding + graphHeight)) }
                            lineTo(leftPadding + graphWidth, topPadding + graphHeight)
                            close()
                        }
                        drawPath(path = areaPath, brush = violetBrush)

                        for (i in 0 until moodPoints.size - 1) {
                            // Check if log is authenticated on this date to draw solid vs dotted
                            val currentItem = chartData[i]
                            val isLogged = currentItem.moodScore != null
                            drawLine(
                                color = Color(0xFF8E24AA),
                                start = moodPoints[i],
                                end = moodPoints[i + 1],
                                strokeWidth = 3.5.dp.toPx(),
                                pathEffect = if (isLogged) null else PathEffect.dashPathEffect(floatArrayOf(6f, 6f), 0f),
                                cap = StrokeCap.Round
                            )
                        }
                    }

                    // Series C: Workout Burn (Orange thin dashed lines indicating physical exertion spikes)
                    if (showWorkout) {
                        for (i in 0 until calOutPoints.size - 1) {
                            drawLine(
                                color = Color(0xFFE65100),
                                start = calOutPoints[i],
                                end = calOutPoints[i + 1],
                                strokeWidth = 1.5.dp.toPx(),
                                cap = StrokeCap.Round
                            )
                        }
                    }

                    // Series D: Body Weight (Steel Blue curve)
                    if (showWeight) {
                        for (i in 0 until weightPoints.size - 1) {
                            drawLine(
                                color = Color(0xFF0288D1),
                                start = weightPoints[i],
                                end = weightPoints[i + 1],
                                strokeWidth = 2.dp.toPx(),
                                cap = StrokeCap.Round
                            )
                        }
                    }

                    // 5. Draw Interactive Scrubber popover line
                    val scrubX = leftPadding + safeSelectedIndex * segmentWidth
                    drawLine(
                        color = Color(0xFF7E57C2).copy(alpha = 0.7f),
                        start = Offset(scrubX, topPadding),
                        end = Offset(scrubX, topPadding + graphHeight),
                        strokeWidth = 2.dp.toPx(),
                        pathEffect = PathEffect.dashPathEffect(floatArrayOf(8f, 8f), 0f)
                    )

                    // Node circles for the selected index
                    if (showMood) {
                        drawCircle(color = Color(0xFF8E24AA), radius = 5.dp.toPx(), center = moodPoints[safeSelectedIndex])
                        drawCircle(color = Color.White, radius = 2.5.dp.toPx(), center = moodPoints[safeSelectedIndex])
                    }
                    if (showCaloriesIn) {
                        drawCircle(color = Color(0xFF2E7D32), radius = 5.dp.toPx(), center = calInPoints[safeSelectedIndex])
                        drawCircle(color = Color.White, radius = 2.5.dp.toPx(), center = calInPoints[safeSelectedIndex])
                    }
                    if (showWorkout) {
                        drawCircle(color = Color(0xFFE65100), radius = 5.dp.toPx(), center = calOutPoints[safeSelectedIndex])
                        drawCircle(color = Color.White, radius = 2.5.dp.toPx(), center = calOutPoints[safeSelectedIndex])
                    }
                    if (showWeight) {
                        drawCircle(color = Color(0xFF0288D1), radius = 5.dp.toPx(), center = weightPoints[safeSelectedIndex])
                        drawCircle(color = Color.White, radius = 2.5.dp.toPx(), center = weightPoints[safeSelectedIndex])
                    }

                    // 6. Draw X-Axis Labels (Date day strings)
                    // For longer timeframes, we show selectively to keep aesthetic clarity
                    val labelInterval = when {
                        selectedDaysLimit > 14 -> 5
                        selectedDaysLimit > 7 -> 2
                        else -> 1
                    }

                    chartData.forEachIndexed { index, item ->
                        if (index % labelInterval == 0 || index == chartData.size - 1 || index == safeSelectedIndex) {
                            val xLabel = leftPadding + index * segmentWidth
                            drawContext.canvas.nativeCanvas.drawText(
                                item.label,
                                xLabel,
                                topPadding + graphHeight + 18.dp.toPx(),
                                Paint().apply {
                                    textSize = 8.5.sp.toPx()
                                    color = if (index == safeSelectedIndex) android.graphics.Color.BLACK else android.graphics.Color.GRAY
                                    typeface = if (index == safeSelectedIndex) Typeface.DEFAULT_BOLD else Typeface.DEFAULT
                                    textAlign = Paint.Align.CENTER
                                    isAntiAlias = true
                                }
                            )
                        }
                    }
                }
            }

            // Interactive Tooltip popover statistics card (Directly below the graph)
            selectedItem?.let { item ->
                Card(
                    colors = CardDefaults.cardColors(containerColor = Color(0xFFFAF6FC)),
                    shape = RoundedCornerShape(16.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(
                        modifier = Modifier.padding(14.dp),
                        verticalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "📅  ${if (isBengali) "নির্বাচিত তারিখ:" else "Inspected Date:"} ${item.label} (${item.dateStr})",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF4A148C)
                            )
                            
                            val moodText = when (item.moodScore) {
                                5f -> if (isBengali) "😊 অত্যন্ত চমৎকার" else "😊 Excellent"
                                4f -> if (isBengali) "🙂 বেশ শান্ত / ভালো" else "🙂 Good / Calm"
                                3f -> if (isBengali) "😐 স্বাভাবিক" else "😐 Neutral"
                                2f -> if (isBengali) "😰 উদ্বিগ্ন / ক্লান্ত" else "😰 Stressed / Low"
                                1f -> if (isBengali) "😢 মন খারাপ" else "😢 Sad / Gloomy"
                                else -> if (isBengali) "📝 মেজাজ রেকর্ড নেই" else "📝 Unlogged Date"
                            }
                            Text(
                                text = moodText,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.ExtraBold,
                                color = Color(0xFF6A1B9A)
                            )
                        }

                        Divider(color = Color(0xFFFFE0B2).copy(alpha = 0.5f))

                        // Grid matrix of key series variables showing actual values
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            // Column A: Calorie Intake
                            Row(
                                modifier = Modifier.weight(1f),
                                horizontalArrangement = Arrangement.spacedBy(6.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(24.dp)
                                        .background(Color(0xFFE8F5E9), CircleShape),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text("🍲", fontSize = 11.sp)
                                }
                                Column {
                                    Text(
                                        text = if (isBengali) "ক্যালরি গ্রহণ" else "Calories In",
                                        fontSize = 9.sp,
                                        color = Color.Gray
                                    )
                                    Text(
                                        text = if (item.caloriesIn > 0) "${item.caloriesIn.toInt()} kcal" else (if (isBengali) "নেই" else "0 kcal"),
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = Color(0xFF2E7D32)
                                    )
                                }
                            }

                            // Column B: Calorie Burned
                            Row(
                                modifier = Modifier.weight(1f),
                                horizontalArrangement = Arrangement.spacedBy(6.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(24.dp)
                                        .background(Color(0xFFFFF3E0), CircleShape),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text("🏃", fontSize = 11.sp)
                                }
                                Column {
                                    Text(
                                        text = if (isBengali) "ব্যায়াম ক্ষয়" else "Workout Burn",
                                        fontSize = 9.sp,
                                        color = Color.Gray
                                    )
                                    Text(
                                        text = if (item.caloriesOut > 0) "${item.caloriesOut.toInt()} kcal" else (if (isBengali) "নেই" else "0 kcal"),
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = Color(0xFFE65100)
                                    )
                                }
                            }

                            // Column C: weight
                            Row(
                                modifier = Modifier.weight(1f),
                                horizontalArrangement = Arrangement.spacedBy(6.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(24.dp)
                                        .background(Color(0xFFE1F5FE), CircleShape),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text("⚖️", fontSize = 11.sp)
                                }
                                Column {
                                    Text(
                                        text = if (isBengali) "শরীরের ওজন" else "Body Weight",
                                        fontSize = 9.sp,
                                        color = Color.Gray
                                    )
                                    Text(
                                        text = if (item.weight != null) "${item.weight} kg" else (if (isBengali) "রেকর্ড নেই" else "-- kg"),
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = Color(0xFF0288D1)
                                    )
                                }
                            }
                        }

                        // Daily Memo Annotation (if exists in journal)
                        if (item.moodNote.isNotBlank()) {
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .background(Color.White, RoundedCornerShape(10.dp))
                                    .padding(8.dp)
                            ) {
                                Text(
                                    text = "📝 \"${item.moodNote}\"",
                                    fontSize = 11.sp,
                                    color = Color.DarkGray,
                                    style = androidx.compose.ui.text.TextStyle(fontStyle = androidx.compose.ui.text.font.FontStyle.Italic)
                                )
                            }
                        }
                    }
                }
            }

            // Wisdom diagnostics generation (Long-term health insights)
            val computedWisdom = remember(chartData, selectedDaysLimit, isBengali) {
                // Calculate correlation stats across the loaded timeframe span
                val loggedMoods = chartData.filter { it.moodScore != null }
                val avgMood = if (loggedMoods.isNotEmpty()) loggedMoods.mapNotNull { it.moodScore }.average() else 3.0
                
                val exerciseDays = chartData.filter { it.caloriesOut > 0f }
                val moodOnExerciseDays = exerciseDays.filter { it.moodScore != null }.mapNotNull { it.moodScore }
                val avgMoodOnExercise = if (moodOnExerciseDays.isNotEmpty()) moodOnExerciseDays.average() else null

                val mealLogRatio = (chartData.filter { it.caloriesIn > 0f }.size.toFloat() / chartData.size.toFloat() * 100).toInt()

                when {
                    avgMoodOnExercise != null && avgMoodOnExercise > avgMood + 0.3 -> {
                        if (isBengali) {
                            "🧠 দীর্ঘমেয়াদী মনস্তাত্ত্বিক মিল: আপনার সক্রিয় ব্যায়ামের দিনগুলোতে মনোভাব সূচক গড়ে ${(String.format(Locale.ROOT, "%.1f", avgMoodOnExercise))}। ব্যায়াম আপনার মানসিক অবসাদ হটিয়ে দেয়!"
                        } else {
                            "🧠 Psycho-Habit Synergy: On active workout days, your average mood jumps to ${(String.format(Locale.ROOT, "%.1f", avgMoodOnExercise))}/5 (vs ${(String.format(Locale.ROOT, "%.1f", avgMood))} baseline). Keep moving!"
                        }
                    }
                    mealLogRatio >= 70 -> {
                        if (isBengali) {
                            "🥗 চমৎকার তথ্যশৃঙ্খলা! বিগত ${selectedDaysLimit} দিনে আপনি ${mealLogRatio}% খাবারের হিসাব রেখেছেন। এটি সফল ডায়েট বাস্তবায়নের চাবি।"
                        } else {
                            "🥗 Stellar Tracking Consistency! You recorded food inputs on ${mealLogRatio}% of the days in this ${selectedDaysLimit}-day span. High tracking index is the master key to weight stability."
                        }
                    }
                    avgMood < 2.5 -> {
                        if (isBengali) {
                            "⚠️ নিবিড় সংকেত: বিগত কয়েকদিনের সামগ্রিক মেজাজ সূচক সামান্য নিম্নমুখী। দিনে হালকা ২০ মিনিট গায়ে রোদ লাগান এবং স্বাস্থ্যকর জলপান বাড়ান।"
                        } else {
                            "⚠️ Energy Sink Detected: Overall emotional baseline suggests stress build-up. We recommend a 20-min sunrise walk and brief meditation spacing to reset."
                        }
                    }
                    else -> {
                        if (isBengali) {
                            "⚖️ সুষম ভারসাম্য সূচক বজায় রয়েছে। দীর্ঘমেয়াদে পুষ্টিকর ডায়েট গ্রহণ এবং ক্যালরির সুষম সমন্বয় আপনার আবেগীয় হরমোনের অনুপাতকে সুরক্ষিত রাখে।"
                        } else {
                            "⚖️ Steady wellness index. Consistently balance nutrient intake with active exercise to preserve hormonal balance and stable moods over long horizons."
                        }
                    }
                }
            }

            Card(
                colors = CardDefaults.cardColors(containerColor = Color(0xFFFFECEF).copy(alpha = 0.5f)),
                shape = RoundedCornerShape(16.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier.padding(14.dp),
                    horizontalArrangement = Arrangement.spacedBy(10.dp),
                    verticalAlignment = Alignment.Top
                ) {
                    Text("💡", fontSize = 18.sp)
                    Text(
                        text = computedWisdom,
                        fontSize = 11.5.sp,
                        color = Color(0xFF880E4F),
                        fontWeight = FontWeight.Medium,
                        lineHeight = 16.sp
                    )
                }
            }
        }
    }
}

// Custom Interactive Legend filter Chip
@Composable
fun InteractiveSeriesChip(
    selected: Boolean,
    color: Color,
    label: String,
    onClick: () -> Unit
) {
    Surface(
        onClick = onClick,
        shape = RoundedCornerShape(12.dp),
        color = if (selected) color.copy(alpha = 0.12f) else Color.Transparent,
        border = androidx.compose.foundation.BorderStroke(
            width = 1.dp,
            color = if (selected) color.copy(alpha = 0.4f) else Color.LightGray.copy(alpha = 0.5f)
        ),
        modifier = Modifier.testTag("series_filter_${label.lowercase(Locale.ROOT)}")
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(10.dp)
                    .clip(CircleShape)
                    .background(color),
                contentAlignment = Alignment.Center
            ) {
                if (selected) {
                    Icon(
                        imageVector = Icons.Default.Check,
                        contentDescription = null,
                        tint = Color.White,
                        modifier = Modifier.size(6.dp)
                    )
                }
            }
            Text(
                text = label,
                fontSize = 10.sp,
                fontWeight = if (selected) FontWeight.Bold else FontWeight.Medium,
                color = if (selected) Color.Black else Color.Gray
            )
        }
    }
}

// Offset safety extensions inside graphs 
private fun Offset.ptY(fallback: Float): Float {
    return if (this.y.isNaN() || this.y.isInfinite()) fallback else this.y
}

data class HealthTrendItem(
    val dateStr: String,
    val label: String,
    val moodScore: Float?,
    val moodString: String = "",
    val moodNote: String = "",
    val hasMeal: Boolean,
    val caloriesIn: Float,
    val hasExercise: Boolean,
    val caloriesOut: Float,
    val weight: Float?
)
