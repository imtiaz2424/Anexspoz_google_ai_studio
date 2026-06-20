package com.example.ui

import android.content.Context
import android.widget.Toast
import androidx.compose.animation.*
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.ExerciseLogEntity
import com.example.viewmodel.DietPlannerViewModel
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

@Composable
fun WorkoutLogger(
    viewModel: DietPlannerViewModel,
    isBengali: Boolean,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val focusManager = LocalFocusManager.current

    val currentExerciseLogs by viewModel.currentExerciseLogs.collectAsState()

    var isAddingWorkout by remember { mutableStateOf(false) }
    var selectedActivityType by remember { mutableStateOf("Walking") }
    var customActivityName by remember { mutableStateOf("") }
    var durationText by remember { mutableStateOf("30") }
    var caloriesText by remember { mutableStateOf("150") }

    val totalDuration = currentExerciseLogs.sumOf { it.durationMin }
    val totalCalories = currentExerciseLogs.sumOf { it.caloriesBurned }

    val presetActivities = listOf(
        Triple("Walking", if (isBengali) "হাঁটা (Walking)" else "Walking", "🚶"),
        Triple("Running", if (isBengali) "দৌড়ানো (Running)" else "Running", "🏃"),
        Triple("Cycling", if (isBengali) "সাইক্লিং (Cycling)" else "Cycling", "🚴"),
        Triple("Gym Workout", if (isBengali) "ব্যায়ামাগার (Gym Workout)" else "Gym Workout", "🏋️"),
        Triple("Yoga", if (isBengali) "যোগব্যায়াম (Yoga)" else "Yoga", "🧘"),
        Triple("Swimming", if (isBengali) "সাতার কাটা (Swimming)" else "Swimming", "🏊"),
        Triple("Others", if (isBengali) "অন্যান্য (Others)" else "Others", "⚙️")
    )

    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        Card(
            shape = RoundedCornerShape(24.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            border = BorderStroke(1.dp, Color(0xFFFFCC80).copy(alpha = 0.6f)),
            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
            modifier = Modifier
                .fillMaxWidth()
                .testTag("workout_logger_card_dashboard")
        ) {
        Column(
            modifier = Modifier.padding(18.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            // Header
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
                            .background(Color(0xFFFFF3E0), CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Text("🏃", fontSize = 22.sp)
                    }
                    Column {
                        Text(
                            text = if (isBengali) "ব্যায়াম ও ওয়ার্কআউট লগ" else "Workout & Activity Log",
                            fontWeight = FontWeight.Bold,
                            fontSize = 16.sp,
                            color = Color(0xFFE65100)
                        )
                        Text(
                            text = if (isBengali) "ক্যালোরি ক্ষয় ও পরিশ্রমের বিবরণ" else "Track active calorie expenditure & stamina duration",
                            fontSize = 11.sp,
                            color = Color.Gray
                        )
                    }
                }

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    IconButton(
                        onClick = {
                            val shareMessage = if (isBengali) {
                                "আজ আমার সুভেছা (Suvecha Wellness) এ করা ব্যায়াম সেশন সম্পন্ন হয়েছে! সর্বমোট $totalDuration মিনিট পরিশ্রম করে $totalCalories কিলো-ক্যালোরি পুড়িয়েছি! 🔥 শরীর ফিট রাখুন!"
                            } else {
                                "I completed my workout logs on Suvecha Wellness today! Total active duration: $totalDuration mins, burning $totalCalories kcal! 🔥 Stay active!"
                            }
                            SharingUtils.shareText(context, shareMessage, "Share Workout Log")
                        },
                        modifier = Modifier.size(36.dp).testTag("share_workout_log_details_btn")
                    ) {
                        Icon(
                            imageVector = Icons.Default.Share,
                            contentDescription = "Share",
                            tint = Color(0xFFE65100),
                            modifier = Modifier.size(20.dp)
                        )
                    }

                    IconButton(
                        onClick = { isAddingWorkout = !isAddingWorkout },
                        modifier = Modifier.size(36.dp).testTag("register_workout_plus_btn")
                    ) {
                        Icon(
                            imageVector = if (isAddingWorkout) Icons.Default.Close else Icons.Default.AddCircle,
                            contentDescription = "Add Workout",
                            tint = Color(0xFFE65100),
                            modifier = Modifier.size(24.dp)
                        )
                    }
                }
            }

            Divider(color = Color(0xFFFFF3E0))

            // Calories & Minutes Statistics Ring
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color(0xFFFFF3E0).copy(alpha = 0.4f), RoundedCornerShape(16.dp))
                    .border(1.dp, Color(0xFFFFE0B2), RoundedCornerShape(16.dp))
                    .padding(14.dp),
                horizontalArrangement = Arrangement.SpaceEvenly,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text = if (isBengali) "মোট সময়" else "Total Active",
                        fontSize = 11.sp,
                        color = Color.Gray
                    )
                    Text(
                        text = "$totalDuration " + (if (isBengali) "মিনিট" else "mins"),
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Black,
                        color = Color(0xFFE65100)
                    )
                }

                Box(
                    modifier = Modifier
                        .width(1.dp)
                        .height(30.dp)
                        .background(Color(0xFFFFE0B2))
                )

                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text = if (isBengali) "ক্যালোরি পোড়ানো" else "Burned Calories",
                        fontSize = 11.sp,
                        color = Color.Gray
                    )
                    Text(
                        text = "$totalCalories kcal",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Black,
                        color = Color(0xFFD84315)
                    )
                }
            }

            // Expandable Add Workout Pane
            AnimatedVisibility(
                visible = isAddingWorkout,
                enter = expandVertically() + fadeIn(),
                exit = shrinkVertically() + fadeOut()
            ) {
                Card(
                    colors = CardDefaults.cardColors(containerColor = Color(0xFFFFF3E0).copy(alpha = 0.15f)),
                    border = BorderStroke(1.dp, Color(0xFFFFCC80)),
                    shape = RoundedCornerShape(16.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(
                        modifier = Modifier.padding(14.dp),
                        verticalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Text(
                            text = if (isBengali) "নতুন অ্যাক্টিভিটি নির্বাচন করুন" else "Log Active Workout Session",
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFFE65100)
                        )

                        // Dropdown preset capsules
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .horizontalScroll(rememberScrollState()),
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            presetActivities.forEach { item ->
                                val isSelected = selectedActivityType == item.first
                                FilterChip(
                                    selected = isSelected,
                                    onClick = {
                                        selectedActivityType = item.first
                                        // Auto update default estimation
                                        when (item.first) {
                                            "Walking" -> { durationText = "30"; caloriesText = "120" }
                                            "Running" -> { durationText = "20"; caloriesText = "240" }
                                            "Cycling" -> { durationText = "30"; caloriesText = "180" }
                                            "Gym Workout" -> { durationText = "45"; caloriesText = "300" }
                                            "Yoga" -> { durationText = "40"; caloriesText = "100" }
                                            "Swimming" -> { durationText = "30"; caloriesText = "250" }
                                        }
                                    },
                                    label = { Text(text = "${item.third} ${item.second}", fontSize = 11.sp) },
                                    colors = FilterChipDefaults.filterChipColors(
                                        selectedContainerColor = Color(0xFFFFB74D),
                                        selectedLabelColor = Color.White
                                    )
                                )
                            }
                        }

                        // Custom activity entry for "Others"
                        if (selectedActivityType == "Others") {
                            OutlinedTextField(
                                value = customActivityName,
                                onValueChange = { customActivityName = it },
                                label = { Text(if (isBengali) "কাস্টম ব্যায়ামের নাম" else "Custom workout title", fontSize = 11.sp) },
                                singleLine = true,
                                modifier = Modifier.fillMaxWidth(),
                                shape = RoundedCornerShape(12.dp)
                            )
                        }

                        // Input fields
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            OutlinedTextField(
                                value = durationText,
                                onValueChange = { durationText = it.filter { c -> c.isDigit() } },
                                label = { Text(if (isBengali) "সময় (মিনিট)" else "Duration (mins)", fontSize = 11.sp) },
                                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number, imeAction = ImeAction.Next),
                                singleLine = true,
                                modifier = Modifier.weight(1f).testTag("workout_duration_input"),
                                shape = RoundedCornerShape(12.dp)
                            )

                            OutlinedTextField(
                                value = caloriesText,
                                onValueChange = { caloriesText = it.filter { c -> c.isDigit() } },
                                label = { Text(if (isBengali) "ক্যালোরি (কিলোক্যালো)" else "Calories (kcal)", fontSize = 11.sp) },
                                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number, imeAction = ImeAction.Done),
                                keyboardActions = KeyboardActions(onDone = { focusManager.clearFocus() }),
                                singleLine = true,
                                modifier = Modifier.weight(1f).testTag("workout_calories_input"),
                                shape = RoundedCornerShape(12.dp)
                            )
                        }

                        // Save action
                        Button(
                            onClick = {
                                val label = if (selectedActivityType == "Others") {
                                    if (customActivityName.isNotBlank()) customActivityName else "Custom Gym"
                                } else {
                                    selectedActivityType
                                }
                                val duration = durationText.toIntOrNull() ?: 30
                                val calories = caloriesText.toIntOrNull() ?: 150

                                viewModel.addExerciseLog(label, duration, calories)
                                isAddingWorkout = false
                                Toast.makeText(context, if (isBengali) "ব্যায়াম সফলভাবে যোগ করা হয়েছে!" else "Workout successfully registered!", Toast.LENGTH_SHORT).show()
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFE65100)),
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(42.dp)
                                .testTag("save_workout_log_action_btn")
                        ) {
                            Text(
                                text = if (isBengali) "অ্যাক্টিভিটি যুক্ত করুন" else "Log Fitness Session",
                                fontWeight = FontWeight.Bold,
                                color = Color.White,
                                fontSize = 12.sp
                            )
                        }
                    }
                }
            }

            // List of exercises logged
            if (currentExerciseLogs.isEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 12.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = if (isBengali) "আজ কোনো ব্যায়াম রেকর্ড করা হয়নি।" else "No active training logs for today.",
                        fontSize = 11.sp,
                        color = Color.Gray
                    )
                }
            } else {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    currentExerciseLogs.forEach { log ->
                        val matchingEmoji = when (log.activity.lowercase()) {
                            "walking" -> "🚶"
                            "running" -> "🏃"
                            "cycling" -> "🚴"
                            "yoga", "mindful breathing", "meditation" -> "🧘"
                            "swimming" -> "🏊"
                            "gym workout", "strength exercise" -> "🏋️"
                            else -> "💪"
                        }

                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .background(Color(0xFFFAFAFA), RoundedCornerShape(14.dp))
                                .border(1.dp, Color(0xFFEEEEEE), RoundedCornerShape(14.dp))
                                .padding(12.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(10.dp)
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(34.dp)
                                        .background(Color(0xFFFFE0B2).copy(alpha = 0.5f), CircleShape),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(matchingEmoji, fontSize = 16.sp)
                                }

                                Column {
                                    Text(
                                        text = log.activity,
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 13.sp,
                                        color = Color(0xFF37474F)
                                    )
                                    Row(
                                        horizontalArrangement = Arrangement.spacedBy(6.dp),
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Text(
                                            text = "${log.durationMin} mins",
                                            fontSize = 11.sp,
                                            color = Color.Gray
                                        )
                                        Box(
                                            modifier = Modifier
                                                .size(3.dp)
                                                .background(Color.Gray, CircleShape)
                                        )
                                        Text(
                                            text = "${log.caloriesBurned} kcal expended",
                                            fontSize = 11.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = Color(0xFFD84315)
                                        )
                                    }
                                }
                            }

                            IconButton(
                                onClick = {
                                    viewModel.deleteExerciseLog(log.id)
                                    Toast.makeText(context, if (isBengali) "ব্যায়াম লগ মুছে ফেলা হয়েছে।" else "Workout log removed.", Toast.LENGTH_SHORT).show()
                                },
                                modifier = Modifier.size(32.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Delete,
                                    contentDescription = "Delete Log",
                                    tint = Color.Gray,
                                    modifier = Modifier.size(16.dp)
                                )
                            }
                        }
                    }
                }
            }
        }
    }

        Spacer(modifier = Modifier.height(4.dp))

        // --- Workout Calendar Section ---
        var selectedCalendarDate by remember { mutableStateOf("") }
        val allExerciseLogs by viewModel.allExerciseLogs.collectAsState()

        WorkoutCalendarView(
            allExerciseLogs = allExerciseLogs,
            isBengali = isBengali,
            selectedDateStr = selectedCalendarDate,
            onDateSelect = { selectedCalendarDate = it }
        )

        if (selectedCalendarDate.isNotEmpty()) {
            val logsForSelectedDate = allExerciseLogs.filter { it.date == selectedCalendarDate }
            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = Color(0xFFFAFAFA)),
                border = BorderStroke(1.dp, Color(0xFFE0E0E0)),
                modifier = Modifier.fillMaxWidth().padding(bottom = 8.dp)
            ) {
                Column(
                    modifier = Modifier.padding(14.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text(
                        text = if (isBengali) "📅 $selectedCalendarDate এর ব্যায়ামসমূহ" else "📅 Exercises on $selectedCalendarDate",
                        fontWeight = FontWeight.Bold,
                        fontSize = 12.5.sp,
                        color = Color(0xFFD84315)
                    )
                    
                    if (logsForSelectedDate.isEmpty()) {
                        Text(
                            text = if (isBengali) "এই দিনে কোনো ব্যায়াম রেকর্ড করা হয়নি।" else "No exercises logged on this date.",
                            fontSize = 11.sp,
                            color = Color.Gray
                        )
                    } else {
                        logsForSelectedDate.forEach { log ->
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Row(horizontalArrangement = Arrangement.spacedBy(6.dp), verticalAlignment = Alignment.CenterVertically) {
                                    Text("🔥", fontSize = 14.sp)
                                    Column {
                                        Text(log.activity, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                                        Text("${log.durationMin} mins", fontSize = 10.sp, color = Color.Gray)
                                    }
                                }
                                Text("-${log.caloriesBurned} kcal", fontWeight = FontWeight.Bold, color = Color(0xFFD84315), fontSize = 11.sp)
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun WorkoutCalendarView(
    allExerciseLogs: List<ExerciseLogEntity>,
    isBengali: Boolean,
    selectedDateStr: String,
    onDateSelect: (String) -> Unit
) {
    var calendarInstance by remember { mutableStateOf(Calendar.getInstance()) }
    val currentMonthName = remember(calendarInstance, isBengali) {
        val sdf = if (isBengali) {
            SimpleDateFormat("MMMM yyyy", Locale("bn", "BD"))
        } else {
            SimpleDateFormat("MMMM yyyy", Locale.US)
        }
        sdf.format(calendarInstance.time)
    }

    val year = calendarInstance.get(Calendar.YEAR)
    val month = calendarInstance.get(Calendar.MONTH) // 0-indexed
    
    val daysInMonth = calendarInstance.getActualMaximum(Calendar.DAY_OF_MONTH)
    val monthHelper = remember(calendarInstance) {
        val cal = calendarInstance.clone() as Calendar
        cal.set(Calendar.DAY_OF_MONTH, 1)
        cal
    }
    val firstDayOfWeek = monthHelper.get(Calendar.DAY_OF_WEEK) // 1 = Sun, 2 = Mon ...

    Card(
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        border = BorderStroke(1.dp, Color(0xFFFFD180).copy(alpha = 0.5f)),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
        modifier = Modifier
            .fillMaxWidth()
            .testTag("workout_calendar_card_inner")
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = {
                    val prev = calendarInstance.clone() as Calendar
                    prev.add(Calendar.MONTH, -1)
                    calendarInstance = prev
                }) {
                    Icon(Icons.Default.ArrowBack, contentDescription = "Prev Month", tint = Color(0xFFE65100))
                }

                Text(
                    text = currentMonthName,
                    fontWeight = FontWeight.Bold,
                    fontSize = 14.sp,
                    color = Color(0xFFE65100)
                )

                IconButton(onClick = {
                    val next = calendarInstance.clone() as Calendar
                    next.add(Calendar.MONTH, 1)
                    calendarInstance = next
                }) {
                    Icon(Icons.Default.ArrowForward, contentDescription = "Next Month", tint = Color(0xFFE65100))
                }
            }

            val shiftedDaysOfWeek = if (isBengali) {
                listOf("শনি", "রবি", "সোম", "মঙ্গল", "বুধ", "বৃহ", "শুক্র")
            } else {
                listOf("Su", "Mo", "Tu", "We", "Th", "Fr", "Sa")
            }

            Row(modifier = Modifier.fillMaxWidth()) {
                shiftedDaysOfWeek.forEach { dayName ->
                    Text(
                        text = dayName,
                        modifier = Modifier.weight(1f),
                        textAlign = TextAlign.Center,
                        fontWeight = FontWeight.Bold,
                        fontSize = 11.sp,
                        color = Color.Gray
                    )
                }
            }

            Divider(color = Color(0xFFFAFAFA), thickness = 0.5.dp)

            val totalCells = (firstDayOfWeek - 1) + daysInMonth
            val totalRows = (totalCells + 6) / 7

            for (row in 0 until totalRows) {
                Row(modifier = Modifier.fillMaxWidth()) {
                    for (col in 0 until 7) {
                        val cellIndex = row * 7 + col
                        val dayNumber = cellIndex - (firstDayOfWeek - 2)
                        
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .aspectRatio(1f)
                                .padding(1.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            if (dayNumber in 1..daysInMonth) {
                                val cellDateString = String.format(Locale.US, "%04d-%02d-%02d", year, month + 1, dayNumber)
                                val dayLogs = allExerciseLogs.filter { it.date == cellDateString }
                                val hasWorkout = dayLogs.isNotEmpty()
                                val isSelected = selectedDateStringEquals(selectedDateStr, cellDateString)
                                
                                Box(
                                    modifier = Modifier
                                        .fillMaxSize()
                                        .clip(CircleShape)
                                        .background(
                                            if (isSelected) Color(0xFFFFB74D)
                                            else if (hasWorkout) Color(0xFFFFE0B2).copy(alpha = 0.6f)
                                            else Color.Transparent
                                        )
                                        .border(
                                            width = 1.dp,
                                            color = if (isSelected) Color(0xFFE65100) else if (hasWorkout) Color(0xFFFFB74D).copy(alpha = 0.5f) else Color.Transparent,
                                            shape = CircleShape
                                        )
                                        .clickable {
                                            if (isSelected) {
                                                onDateSelect("")
                                            } else {
                                                onDateSelect(cellDateString)
                                            }
                                        },
                                    contentAlignment = Alignment.Center
                                ) {
                                    Column(horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.Center) {
                                        Text(
                                            text = dayNumber.toString(),
                                            fontWeight = if (hasWorkout) FontWeight.Bold else FontWeight.Normal,
                                            color = if (hasWorkout) Color(0xFFE65100) else Color.DarkGray,
                                            fontSize = 11.sp
                                        )
                                        if (hasWorkout) {
                                            Box(
                                                modifier = Modifier
                                                    .size(4.dp)
                                                    .background(Color(0xFFE65100), CircleShape)
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

private fun selectedDateStringEquals(sel: String, cell: String): Boolean {
    return sel == cell
}
