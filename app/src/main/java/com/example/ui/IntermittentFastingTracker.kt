package com.example.ui

import android.content.Context
import android.widget.Toast
import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
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
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.json.JSONArray
import org.json.JSONObject
import java.text.SimpleDateFormat
import java.util.*

@Composable
fun IntermittentFastingTracker(
    isBengali: Boolean,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()

    // Fasting Protocols list
    val protocols = remember {
        listOf(
            FastingProtocol("16:8", 16, if (isBengali) "১৬:৮ লীন-গেইনস" else "16:8 LeanGains", if (isBengali) "১৬ ঘণ্টা উপবাস, ৮ ঘণ্টা আহার" else "16 hours fast, 8 hours feed"),
            FastingProtocol("14:10", 14, if (isBengali) "১৪:১০ সাধারণ" else "14:10 Moderate", if (isBengali) "১৪ ঘণ্টা উপবাস, ১০ ঘণ্টা আহার" else "14 hours fast, 10 hours feed"),
            FastingProtocol("12:12", 12, if (isBengali) "১২:১২ সুষম" else "12:12 Balanced", if (isBengali) "১২ ঘণ্টা উপবাস, ১২ ঘণ্টা আহার" else "12 hours fast, 12 hours feed"),
            FastingProtocol("18:6", 18, if (isBengali) "১৮:৬ ওয়ারিয়র" else "18:6 Warrior", if (isBengali) "১৮ ঘণ্টা উপবাস, ৬ ঘণ্টা আহার" else "18 hours fast, 6 hours feed")
        )
    }

    var selectedProtocolIndex by remember { mutableStateOf(0) }
    val selectedProtocol = protocols[selectedProtocolIndex]

    // Fasting active state
    val sharedPrefs = remember {
        context.getSharedPreferences("niljori_fasting_prefs", Context.MODE_PRIVATE)
    }

    var isFastingActive by remember {
        mutableStateOf(sharedPrefs.getBoolean("is_fasting_active", false))
    }
    var startTimeMillis by remember {
        mutableStateOf(sharedPrefs.getLong("fasting_start_time", 0L))
    }
    var activeProtocolHours by remember {
        mutableStateOf(sharedPrefs.getInt("active_protocol_hours", 16))
    }

    var currentElapsedSeconds by remember { mutableStateOf(0L) }

    // Fasting history logs state
    var fastingLogs by remember {
        mutableStateOf(loadFastingLogs(context))
    }

    // Timer logic to increment seconds dynamically while fasting is active
    LaunchedEffect(isFastingActive, startTimeMillis) {
        if (isFastingActive && startTimeMillis > 0L) {
            while (isFastingActive) {
                val elapsed = (System.currentTimeMillis() - startTimeMillis) / 1000
                currentElapsedSeconds = if (elapsed > 0) elapsed else 0L
                delay(1000)
            }
        } else {
            currentElapsedSeconds = 0L
        }
    }

    // Calculating percentages and display formats
    val targetSeconds = activeProtocolHours * 3600L
    val progressFraction = if (targetSeconds > 0) {
        (currentElapsedSeconds.toFloat() / targetSeconds.toFloat()).coerceIn(0f, 1f)
    } else {
        0f
    }

    val displayHours = currentElapsedSeconds / 3600
    val displayMinutes = (currentElapsedSeconds % 3600) / 60
    val displaySeconds = currentElapsedSeconds % 60
    val formattedTimeStr = String.format("%02d:%02d:%02d", displayHours, displayMinutes, displaySeconds)

    val targetEndTimeStr = remember(startTimeMillis, activeProtocolHours) {
        if (startTimeMillis > 0L) {
            val targetCal = Calendar.getInstance().apply {
                timeInMillis = startTimeMillis + (activeProtocolHours * 3600 * 1000)
            }
            val sdf = SimpleDateFormat("hh:mm a, MMM dd", Locale.getDefault())
            sdf.format(targetCal.time)
        } else {
            ""
        }
    }

    Card(
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        border = BorderStroke(1.dp, Color(0xFFFFB74D).copy(alpha = 0.5f)),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        modifier = modifier
            .fillMaxWidth()
            .testTag("fasting_tracker_card")
    ) {
        Column(
            modifier = Modifier.padding(18.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
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
                        Text("⏳", fontSize = 22.sp)
                    }
                    Column {
                        Text(
                            text = if (isBengali) "সবিরাম উপবাস ট্র্যাকার" else "Intermittent Fasting Tracker",
                            fontWeight = FontWeight.Bold,
                            fontSize = 16.sp,
                            color = Color(0xFFE65100)
                        )
                        Text(
                            text = if (isBengali) "উপবাসের সময়কাল ট্র্যাক করুন ও মেটাবলিজম উন্নত করুন" else "Track active fasting intervals to boost metabolism",
                            fontSize = 11.sp,
                            color = Color.Gray
                        )
                    }
                }
            }

            Divider(color = Color(0xFFFFF3E0))

            // Protocol Selection (only enabled when not currently fasting)
            if (!isFastingActive) {
                Text(
                    text = if (isBengali) "১. উপবাসের ধরন নির্বাচন করুন:" else "1. Select Fasting Protocol:",
                    fontWeight = FontWeight.Bold,
                    fontSize = 13.sp,
                    color = Color(0xFFE65100)
                )

                Column(
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    protocols.forEachIndexed { index, proto ->
                        val isSelected = selectedProtocolIndex == index
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(14.dp))
                                .background(if (isSelected) Color(0xFFFFF3E0) else Color(0xFFFAFAFA))
                                .border(
                                    width = if (isSelected) 1.5.dp else 1.dp,
                                    color = if (isSelected) Color(0xFFE65100) else Color(0xFFECEFF1),
                                    shape = RoundedCornerShape(14.dp)
                                )
                                .clickable { selectedProtocolIndex = index }
                                .padding(horizontal = 14.dp, vertical = 10.dp)
                                .testTag("fasting_protocol_$index"),
                            contentAlignment = Alignment.CenterStart
                        ) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column {
                                    Text(
                                        text = proto.label,
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 12.5.sp,
                                        color = if (isSelected) Color(0xFFE65100) else Color(0xFF37474F)
                                    )
                                    Text(
                                        text = proto.description,
                                        fontSize = 10.sp,
                                        color = Color.Gray
                                    )
                                }
                                RadioButton(
                                    selected = isSelected,
                                    onClick = { selectedProtocolIndex = index },
                                    colors = RadioButtonDefaults.colors(selectedColor = Color(0xFFE65100))
                                )
                            }
                        }
                    }
                }
            }

            // Visual Timer Display
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(210.dp)
                    .background(
                        brush = Brush.verticalGradient(
                            colors = listOf(Color(0xFFFFF3E0), Color(0xFFFFE0B2).copy(alpha = 0.3f))
                        ),
                        shape = RoundedCornerShape(20.dp)
                    )
                    .border(1.dp, Color(0xFFFFE0B2), RoundedCornerShape(20.dp)),
                contentAlignment = Alignment.Center
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    // Circle Progress Visualizer
                    Box(
                        modifier = Modifier.size(110.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator(
                            progress = if (isFastingActive) progressFraction else 0f,
                            color = Color(0xFFE65100),
                            trackColor = Color(0xFFFFE0B2),
                            strokeWidth = 6.dp,
                            modifier = Modifier.fillMaxSize()
                        )
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text(
                                text = if (isFastingActive) formattedTimeStr else "--:--:--",
                                fontSize = 18.sp,
                                fontWeight = FontWeight.Black,
                                color = Color(0xFFE65100)
                            )
                            Text(
                                text = if (isFastingActive) {
                                    "${(progressFraction * 100).toInt()}% " + (if (isBengali) "সম্পন্ন" else "done")
                                } else {
                                    if (isBengali) "প্রস্তুত" else "Ready"
                                },
                                fontSize = 9.5.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.Gray
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    Text(
                        text = if (isFastingActive) {
                            if (isBengali) {
                                "সক্রিয় সারণী: ${activeProtocolHours} ঘণ্টার উপবাস\nসমাপ্তি সময়: $targetEndTimeStr"
                            } else {
                                "Fasting Protocol: ${activeProtocolHours} Hours\nFasting ends at: $targetEndTimeStr"
                            }
                        } else {
                            if (isBengali) {
                                "সেশন শুরু করতে নিচের বোতামটি চাপুন"
                            } else {
                                "Tap Start Fasting to begin your session"
                            }
                        },
                        fontSize = 11.5.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFFBF360C),
                        textAlign = TextAlign.Center,
                        lineHeight = 15.sp
                    )
                }
            }

            // Start / Stop Timer Controls
            Button(
                onClick = {
                    if (isFastingActive) {
                        // Stop Fasting & Save Log
                        val completedDurationHours = currentElapsedSeconds.toDouble() / 3600.0
                        val durationStr = String.format(Locale.getDefault(), "%.2f", completedDurationHours)

                        // Save log if it was at least 1 second
                        if (currentElapsedSeconds > 0) {
                            val newLog = FastingLog(
                                id = "fast_${System.currentTimeMillis()}",
                                protocol = "${activeProtocolHours}:${24 - activeProtocolHours}",
                                durationHours = completedDurationHours,
                                timestamp = System.currentTimeMillis()
                            )
                            val updatedList = fastingLogs.toMutableList()
                            updatedList.add(0, newLog)
                            fastingLogs = updatedList
                            saveFastingLogs(context, updatedList)

                            Toast.makeText(
                                context,
                                if (isBengali) {
                                    "অভিনন্দন! আপনি $durationStr ঘণ্টা ফাস্টিং সম্পন্ন করেছেন। 🎉"
                                } else {
                                    "Congratulations! You completed $durationStr hours of fasting. 🎉"
                                },
                                Toast.LENGTH_LONG
                            ).show()
                        }

                        // Reset states
                        isFastingActive = false
                        startTimeMillis = 0L
                        sharedPrefs.edit()
                            .putBoolean("is_fasting_active", false)
                            .putLong("fasting_start_time", 0L)
                            .apply()
                    } else {
                        // Start Fasting
                        val now = System.currentTimeMillis()
                        isFastingActive = true
                        startTimeMillis = now
                        activeProtocolHours = selectedProtocol.hours

                        sharedPrefs.edit()
                            .putBoolean("is_fasting_active", true)
                            .putLong("fasting_start_time", now)
                            .putInt("active_protocol_hours", selectedProtocol.hours)
                            .apply()

                        Toast.makeText(
                            context,
                            if (isBengali) "উপবাস শুরু হয়েছে! পানি পান করতে ভুলবেন না।" else "Fast started! Keep hydrated.",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                },
                colors = ButtonDefaults.buttonColors(
                    containerColor = if (isFastingActive) Color(0xFFD32F2F) else Color(0xFFE65100)
                ),
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp)
                    .testTag("fasting_toggle_btn")
            ) {
                Icon(
                    imageVector = if (isFastingActive) Icons.Default.Stop else Icons.Default.PlayArrow,
                    contentDescription = null,
                    tint = Color.White
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = if (isFastingActive) {
                        if (isBengali) "উপবাস শেষ করুন ও সংরক্ষণ করুন" else "Stop Fast & Save Record"
                    } else {
                        if (isBengali) "এখনই উপবাস শুরু করুন" else "Start Fasting Now"
                    },
                    fontWeight = FontWeight.Bold,
                    fontSize = 13.sp,
                    color = Color.White
                )
            }

            // Fasting Tips & Autophagy Guidance Callout
            Card(
                colors = CardDefaults.cardColors(containerColor = Color(0xFFFFF3E0).copy(alpha = 0.5f)),
                shape = RoundedCornerShape(14.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier.padding(12.dp),
                    horizontalArrangement = Arrangement.spacedBy(10.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("💡", fontSize = 20.sp)
                    Column {
                        Text(
                            text = if (isBengali) "টিপস ও অটোফ্যাজি সংযোগ" else "Fasting Tips & Autophagy",
                            fontWeight = FontWeight.Bold,
                            fontSize = 11.5.sp,
                            color = Color(0xFFE65100)
                        )
                        Text(
                            text = if (isBengali) {
                                "১২ ঘণ্টা ফাস্টিং সম্পন্ন হওয়ার পর শরীরের মেদ কমা ও কোষ পুনর্গঠন প্রক্রিয়া (অটোফ্যাজি) শুরু হয়। ফাস্টিং পিরিয়ডে চিনি ছাড়া পানি, চা বা কফি পান করা সম্পূর্ণ নিরাপদ।"
                            } else {
                                "Autophagy begins around 12-16 hours of fasting, accelerating cell repair. Plain water, green tea, or black coffee without sugar do not break your fast."
                            },
                            fontSize = 10.sp,
                            color = Color(0xFF5D4037),
                            lineHeight = 13.sp
                        )
                    }
                }
            }

            // Fasting History Section
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                Text("📜", fontSize = 14.sp)
                Text(
                    text = if (isBengali) "বিগত উপবাসের রেকর্ড" else "Past Fasting History",
                    fontWeight = FontWeight.Bold,
                    fontSize = 13.sp,
                    color = Color(0xFFE65100)
                )
            }

            if (fastingLogs.isEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(Color(0xFFFAFAFA), RoundedCornerShape(14.dp))
                        .padding(20.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = if (isBengali) "এখনও কোনো উপবাসের রেকর্ড নেই। ওপর থেকে প্রথম উপবাস শুরু করুন!" else "No fasting logs recorded yet. Start your first session above!",
                        fontSize = 11.sp,
                        color = Color.Gray,
                        textAlign = TextAlign.Center
                    )
                }
            } else {
                Column(
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    fastingLogs.take(5).forEach { log ->
                        val formattedDate = remember(log.timestamp) {
                            try {
                                val sdf = SimpleDateFormat("MMM dd, yyyy - hh:mm a", Locale.getDefault())
                                sdf.format(Date(log.timestamp))
                            } catch (e: Exception) {
                                ""
                            }
                        }

                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .background(Color(0xFFFAFAFA), RoundedCornerShape(14.dp))
                                .border(1.dp, Color(0xFFECEFF1), RoundedCornerShape(14.dp))
                                .padding(12.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(36.dp)
                                    .background(Color.White, CircleShape),
                                contentAlignment = Alignment.Center
                            ) {
                                Text("🔥", fontSize = 18.sp)
                            }

                            Column(modifier = Modifier.weight(1f)) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(
                                        text = if (isBengali) "সফল উপবাস: ${log.protocol}" else "Fasted: ${log.protocol}",
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 12.sp,
                                        color = Color(0xFFE65100)
                                    )
                                    Text(
                                        text = formattedDate,
                                        fontSize = 9.sp,
                                        color = Color.Gray
                                    )
                                }
                                Text(
                                    text = if (isBengali) {
                                        "সময়কাল: ${String.format(Locale.getDefault(), "%.2f", log.durationHours)} ঘণ্টা"
                                    } else {
                                        "Duration: ${String.format(Locale.getDefault(), "%.2f", log.durationHours)} hours"
                                    },
                                    fontSize = 11.sp,
                                    color = Color.DarkGray
                                )
                            }

                            IconButton(
                                onClick = {
                                    val updated = fastingLogs.toMutableList()
                                    updated.remove(log)
                                    fastingLogs = updated
                                    saveFastingLogs(context, updated)
                                },
                                modifier = Modifier
                                    .size(32.dp)
                                    .testTag("delete_fasting_log_${log.id}")
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Delete,
                                    contentDescription = "Delete Fasting log",
                                    tint = Color(0xFFD32F2F),
                                    modifier = Modifier.size(16.dp)
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

private data class FastingProtocol(
    val key: String,
    val hours: Int,
    val label: String,
    val description: String
)

private data class FastingLog(
    val id: String,
    val protocol: String,
    val durationHours: Double,
    val timestamp: Long
)

private fun loadFastingLogs(context: Context): List<FastingLog> {
    val list = mutableListOf<FastingLog>()
    try {
        val sharedPrefs = context.getSharedPreferences("niljori_fasting_prefs", Context.MODE_PRIVATE)
        val jsonStr = sharedPrefs.getString("fasting_logs_arr", null) ?: return list
        val array = JSONArray(jsonStr)
        for (i in 0 until array.length()) {
            val obj = array.getJSONObject(i)
            list.add(
                FastingLog(
                    id = obj.getString("id"),
                    protocol = obj.getString("protocol"),
                    durationHours = obj.getDouble("durationHours"),
                    timestamp = obj.getLong("timestamp")
                )
            )
        }
    } catch (_: Exception) {}
    return list
}

private fun saveFastingLogs(context: Context, list: List<FastingLog>) {
    try {
        val array = JSONArray()
        list.forEach { log ->
            val obj = JSONObject().apply {
                put("id", log.id)
                put("protocol", log.protocol)
                put("durationHours", log.durationHours)
                put("timestamp", log.timestamp)
            }
            array.put(obj)
        }
        val sharedPrefs = context.getSharedPreferences("niljori_fasting_prefs", Context.MODE_PRIVATE)
        sharedPrefs.edit().putString("fasting_logs_arr", array.toString()).apply()
    } catch (_: Exception) {}
}
