package com.example.ui

import android.widget.Toast
import androidx.compose.animation.*
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.viewmodel.DietPlannerViewModel

@Composable
fun NutritionCalculator(
    viewModel: DietPlannerViewModel,
    isBengali: Boolean,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val userProfile by viewModel.userProfile.collectAsState()

    // State bindings
    var ageStr by remember { mutableStateOf(userProfile?.age?.toString() ?: "25") }
    var weightStr by remember { mutableStateOf(userProfile?.weight?.toString() ?: "70.0") }
    var heightStr by remember { mutableStateOf(userProfile?.height?.toString() ?: "175.0") }
    var gender by remember { mutableStateOf(userProfile?.gender ?: "Male") } // Male, Female

    // Activity level: 0: Sedentary, 1: Moderate, 2: Active
    var activityIndex by remember { mutableStateOf(1) }
    // Weight Goal target: 0: Loss, 1: Maintain, 2: Gain
    var goalIndex by remember { mutableStateOf(if (userProfile?.goal?.contains("Loss") == true || userProfile?.goal?.contains("কমানো") == true) 0 else if (userProfile?.goal?.contains("Gain") == true || userProfile?.goal?.contains("বাড়ানো") == true) 2 else 1) }

    // Dropdown/Detail dialog selected metric
    var selectedDetailMetric by remember { mutableStateOf<String?>(null) }

    // Parse inputs
    val weightVal = weightStr.toDoubleOrNull() ?: 70.0
    val heightVal = heightStr.toDoubleOrNull() ?: 175.0
    val ageVal = ageStr.toIntOrNull() ?: 25

    // 1. BMI Calculation
    val heightMeters = heightVal / 100.0
    val bmi = if (heightMeters > 0) weightVal / (heightMeters * heightMeters) else 0.0
    val bmiStatus = getBmiStatus(bmi, isBengali)
    val bmiColor = getBmiColor(bmi)

    // 2. Body Fat % Estimation (US Navy standard model formula based on BMI, Age, Gender)
    val genderValue = if (gender.lowercase() == "male") 1 else 0
    val bodyFat = if (bmi > 0) {
        (1.20 * bmi) + (0.23 * ageVal) - (10.8 * genderValue) - 5.4
    } else 0.0
    val bodyFatStatus = getBodyFatStatus(bodyFat, gender.lowercase() == "male", isBengali)

    // 3. BMR (Mifflin-St Jeor Equation)
    val bmr = if (gender.lowercase() == "male") {
        (10.0 * weightVal) + (6.25 * heightVal) - (5.0 * ageVal) + 5.0
    } else {
        (10.0 * weightVal) + (6.25 * heightVal) - (5.0 * ageVal) - 161.0
    }

    // 4. TDEE Calculation
    val activityMultiplier = when (activityIndex) {
        0 -> 1.2    // Sedentary
        1 -> 1.55   // Moderate
        else -> 1.725 // Active
    }
    val tdee = bmr * activityMultiplier

    // 5. Ideal Weight Range (Robinson formula)
    // Robinson base weight: Male 52kg + 1.9kg per inch over 5 feet. Female 49kg + 1.7kg per inch over 5 feet.
    val heightInches = heightVal / 2.54
    val inchesOver5Feet = (heightInches - 60.0).coerceAtLeast(0.0)
    val idealWeightRobinson = if (gender.lowercase() == "male") {
        52.0 + (1.9 * inchesOver5Feet)
    } else {
        49.0 + (1.7 * inchesOver5Feet)
    }
    val idealWeightMin = (idealWeightRobinson * 0.95).coerceAtLeast(40.0)
    val idealWeightMax = idealWeightRobinson * 1.05

    // 6. Lean Body Mass (Boer / James Clinical Formula)
    val lbm = if (gender.lowercase() == "male") {
        (1.10 * weightVal) - (128.0 * (weightVal * weightVal) / (heightVal * heightVal))
    } else {
        (1.07 * weightVal) - (148.0 * (weightVal * weightVal) / (heightVal * heightVal))
    }
    val lbmClamped = lbm.coerceIn(0.0, weightVal)

    // 7. Calorie Requirement based on Goal Selection
    val calorieRequirement = when (goalIndex) {
        0 -> (tdee - 500).toInt().coerceAtLeast(1200) // Fat loss deficit
        2 -> (tdee + 500).toInt() // Muscle gain surplus
        else -> tdee.toInt() // Maintenance
    }

    // 8. Protein Requirement (Grams)
    val proteinPerKg = when (activityIndex) {
        0 -> 1.2
        1 -> 1.6
        else -> 2.0
    }
    val proteinRequirementGrams = weightVal * proteinPerKg

    // 9. Water Requirement (Milliliters)
    // Base 35ml per kg + activity adjustments
    val waterRequirementMl = (weightVal * 35).toInt() + (if (activityIndex == 1) 400 else if (activityIndex == 2) 800 else 0)

    Card(
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f)),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        modifier = modifier
            .fillMaxWidth()
            .testTag("nutrition_calculator_panel")
    ) {
        Column(
            modifier = Modifier.padding(18.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Calculator Header
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
                            .background(Color(0xFFE8F5E9), CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Calculate,
                            contentDescription = null,
                            tint = Color(0xFF2E7D32),
                            modifier = Modifier.size(22.dp)
                        )
                    }
                    Column {
                        Text(
                            text = if (isBengali) "ক্লিনিকাল স্বাস্থ্য ক্যালকুলেটর" else "Clinical Fitness Calculators",
                            fontWeight = FontWeight.Bold,
                            fontSize = 16.sp,
                            color = Color(0xFF1B5E20)
                        )
                        Text(
                            text = if (isBengali) "৯টি বৈজ্ঞানিক স্বাস্থ্য পরিমাপক সংকলন" else "9-in-1 medical equations suite",
                            fontSize = 11.sp,
                            color = Color.Gray
                        )
                    }
                }

                Badge(
                    containerColor = Color(0xFFE8F5E9),
                    modifier = Modifier.padding(end = 4.dp)
                ) {
                    Text(
                        text = if (isBengali) "PRO" else "Clinical",
                        color = Color(0xFF2E7D32),
                        fontSize = 10.sp,
                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                    )
                }
            }

            Divider(color = Color(0xFFECEFF1))

            // Inputs Group
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                OutlinedTextField(
                    value = weightStr,
                    onValueChange = { weightStr = it },
                    label = { Text(if (isBengali) "ওজন (কেজি)" else "Weight (kg)", fontSize = 11.sp) },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                    singleLine = true,
                    modifier = Modifier
                        .weight(1f)
                        .testTag("calculator_weight_input"),
                    shape = RoundedCornerShape(12.dp)
                )

                OutlinedTextField(
                    value = heightStr,
                    onValueChange = { heightStr = it },
                    label = { Text(if (isBengali) "উচ্চতা (সেমি)" else "Height (cm)", fontSize = 11.sp) },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                    singleLine = true,
                    modifier = Modifier
                        .weight(1f)
                        .testTag("calculator_height_input"),
                    shape = RoundedCornerShape(12.dp)
                )

                OutlinedTextField(
                    value = ageStr,
                    onValueChange = { ageStr = it },
                    label = { Text(if (isBengali) "বয়স" else "Age (yr)", fontSize = 11.sp) },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    singleLine = true,
                    modifier = Modifier
                        .weight(0.8f)
                        .testTag("calculator_age_input"),
                    shape = RoundedCornerShape(12.dp)
                )
            }

            // Gender & Activity Row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                // Gender Selector
                Column(modifier = Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                    Text(
                        text = if (isBengali) "লিঙ্গ" else "Biological Sex",
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.Gray
                    )
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(40.dp)
                            .border(1.dp, Color.LightGray, RoundedCornerShape(10.dp)),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        listOf("Male", "Female").forEach { g ->
                            val isSel = gender.equals(g, ignoreCase = true)
                            Box(
                                modifier = Modifier
                                    .weight(1f)
                                    .fillMaxHeight()
                                    .clip(
                                        if (g == "Male") RoundedCornerShape(topStart = 9.dp, bottomStart = 9.dp)
                                        else RoundedCornerShape(topEnd = 9.dp, bottomEnd = 9.dp)
                                    )
                                    .background(if (isSel) Color(0xFFE8F5E9) else Color.Transparent)
                                    .clickable { gender = g },
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = if (g == "Male") (if (isBengali) "পুরুষ" else "Male") else (if (isBengali) "নারী" else "Female"),
                                    fontWeight = if (isSel) FontWeight.Bold else FontWeight.Normal,
                                    fontSize = 11.sp,
                                    color = if (isSel) Color(0xFF1B5E20) else Color.DarkGray
                                )
                            }
                        }
                    }
                }

                // Activity Index
                Column(modifier = Modifier.weight(1.2f), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                    Text(
                        text = if (isBengali) "পরিশ্রম" else "Activity Exertion",
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.Gray
                    )
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(40.dp)
                            .border(1.dp, Color.LightGray, RoundedCornerShape(10.dp)),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        listOf("Sedentary", "Moderate", "Active").forEachIndexed { idx, label ->
                            val isSel = activityIndex == idx
                            val display = if (idx == 0) (if (isBengali) "অলস" else "Low")
                            else if (idx == 1) (if (isBengali) "মাঝারি" else "Mid")
                            else (if (isBengali) "ভারী" else "High")

                            Box(
                                modifier = Modifier
                                    .weight(1f)
                                    .fillMaxHeight()
                                    .clip(
                                        when (idx) {
                                            0 -> RoundedCornerShape(topStart = 9.dp, bottomStart = 9.dp)
                                            2 -> RoundedCornerShape(topEnd = 9.dp, bottomEnd = 9.dp)
                                            else -> RoundedCornerShape(0.dp)
                                        }
                                    )
                                    .background(if (isSel) Color(0xFFE8F5E9) else Color.Transparent)
                                    .clickable { activityIndex = idx },
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = display,
                                    fontWeight = if (isSel) FontWeight.Bold else FontWeight.Normal,
                                    fontSize = 11.sp,
                                    color = if (isSel) Color(0xFF1B5E20) else Color.DarkGray
                                )
                            }
                        }
                    }
                }
            }

            // Health Goals Pill selectors
            Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                Text(
                    text = if (isBengali) "স্বাস্থ্য লক্ষ্য (Defines calorie modifier)" else "Biological Target Goal",
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.Gray
                )
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    val goalsEn = listOf("Fat Loss", "Maintenance", "Muscle Gain")
                    val goalsBn = listOf("ওজন কমানো", "নিয়ন্ত্রণ করা", "মাসল গেইন")

                    goalsEn.forEachIndexed { idx, label ->
                        val isSel = goalIndex == idx
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .height(34.dp)
                                .clip(RoundedCornerShape(8.dp))
                                .background(if (isSel) Color(0xFFFFECEB) else Color(0xFFF5F7F6))
                                .border(1.dp, if (isSel) Color(0xFFD84315) else Color(0xFFECEFF1), RoundedCornerShape(8.dp))
                                .clickable { goalIndex = idx },
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = if (isBengali) goalsBn[idx] else label,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = if (isSel) Color(0xFFC62828) else Color.DarkGray
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(4.dp))

            // Diagnostic Metrics 3x3 Dashboard Grid
            Text(
                text = if (isBengali) "ডায়াগনস্টিক স্বাস্থ্য সূচকসমূহ (Tap to learn more)" else "Clinical Metric Suite Dashboard (Click cards for deep dives)",
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
                color = Color.DarkGray
            )

            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                // Row 1: BMI, Body Fat %, BMR
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    MetricCard(
                        title = if (isBengali) "বিএমআই (BMI)" else "1. BMI Index",
                        value = String.format("%.1f", bmi),
                        subtext = bmiStatus,
                        color = bmiColor,
                        modifier = Modifier.weight(1f),
                        onClick = { selectedDetailMetric = "bmi" }
                    )

                    MetricCard(
                        title = if (isBengali) "বডি ফ্যাট %" else "2. Body Fat %",
                        value = "${String.format("%.1f", bodyFat.coerceAtLeast(2.0))}%",
                        subtext = bodyFatStatus,
                        color = Color(0xFFEF6C00),
                        modifier = Modifier.weight(1f),
                        onClick = { selectedDetailMetric = "body_fat" }
                    )

                    MetricCard(
                        title = if (isBengali) "বিএমআর (BMR)" else "3. BMR Rate",
                        value = "${bmr.toInt()}",
                        subtext = if (isBengali) "ক্যালরি/দিন (Rest)" else "kcal/day (Basal)",
                        color = Color(0xFF1565C0),
                        modifier = Modifier.weight(1f),
                        onClick = { selectedDetailMetric = "bmr" }
                    )
                }

                // Row 2: TDEE, Ideal Weight, Lean Body Mass
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    MetricCard(
                        title = if (isBengali) "টিডিইই (TDEE)" else "4. TDEE Spend",
                        value = "${tdee.toInt()}",
                        subtext = if (isBengali) "সক্রিয় ব্যয়/দিন" else "kcal/day (Active)",
                        color = Color(0xFF2E7D32),
                        modifier = Modifier.weight(1f),
                        onClick = { selectedDetailMetric = "tdee" }
                    )

                    MetricCard(
                        title = if (isBengali) "আদর্শ ওজন" else "5. Ideal Weight",
                        value = "${idealWeightRobinson.toInt()} kg",
                        subtext = "${idealWeightMin.toInt()}-${idealWeightMax.toInt()} kg range",
                        color = Color(0xFF00ACC1),
                        modifier = Modifier.weight(1f),
                        onClick = { selectedDetailMetric = "ideal_weight" }
                    )

                    MetricCard(
                        title = if (isBengali) "লিন বডি মাস" else "6. Lean Mass",
                        value = "${String.format("%.1f", lbmClamped)} kg",
                        subtext = if (isBengali) "চর্বিহীন কঙ্কাল ভর" else "Skeletal & muscle",
                        color = Color(0xFF8E24AA),
                        modifier = Modifier.weight(1f),
                        onClick = { selectedDetailMetric = "lbm" }
                    )
                }

                // Row 3: Suggested Calories, Protein Requirement, Water Requirement
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    MetricCard(
                        title = if (isBengali) "দৈনিক ক্যালরি" else "7. Target Cal",
                        value = "$calorieRequirement",
                        subtext = if (isBengali) "টার্গেট কি.ক্যাল" else "Daily Target kcal",
                        color = Color(0xFFC62828),
                        modifier = Modifier.weight(1f),
                        onClick = { selectedDetailMetric = "calorie" }
                    )

                    MetricCard(
                        title = if (isBengali) "প্রোটিন চাহিদা" else "8. Protein Req",
                        value = "${proteinRequirementGrams.toInt()}g",
                        subtext = if (isBengali) "পেশী গঠনের জন্য" else "For muscle building",
                        color = Color(0xFF00796B),
                        modifier = Modifier.weight(1f),
                        onClick = { selectedDetailMetric = "protein" }
                    )

                    MetricCard(
                        title = if (isBengali) "পানি পানের মাত্রা" else "9. Fluids / Water",
                        value = if (waterRequirementMl >= 1000) String.format("%.2f L", waterRequirementMl / 1000.0) else "$waterRequirementMl ml",
                        subtext = if (isBengali) "ডিহাইড্রেশন প্রতিরোধ" else "Hydration target",
                        color = Color(0xFF0288D1),
                        modifier = Modifier.weight(1f),
                        onClick = { selectedDetailMetric = "water" }
                    )
                }
            }

            Divider(color = Color(0xFFECEFF1))

            // Action: apply values directly to profile and trigger Room update!
            Button(
                onClick = {
                    viewModel.saveCustomCalorieTarget(calorieRequirement, waterRequirementMl)
                    val toastMsg = if (isBengali) {
                        "সফলভাবে দৈনিক লক্ষ্য সেট করা হয়েছে: $calorieRequirement কি.ক্যালোরি এবং ${String.format("%.1f", waterRequirementMl / 1000.0)}L জল!"
                    } else {
                        "Successfully applied daily target: $calorieRequirement kcal & ${String.format("%.1f", waterRequirementMl / 1000.0)}L water to your profile!"
                    }
                    Toast.makeText(context, toastMsg, Toast.LENGTH_LONG).show()
                },
                shape = RoundedCornerShape(14.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF2E7D32)),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp)
                    .testTag("apply_calculated_nutrition_target_btn")
            ) {
                Icon(
                    imageVector = Icons.Default.DoneAll,
                    contentDescription = null,
                    tint = Color.White,
                    modifier = Modifier.size(18.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = if (isBengali) "ক্যালকুলেশনগুলো ডায়েট প্রোফাইলে সেট করুন" else "Apply Calculated Targets to Profile",
                    fontWeight = FontWeight.Bold,
                    fontSize = 13.sp,
                    color = Color.White
                )
            }
        }
    }

    // Interactive Deep Dive Metric Explanation Dialogues
    selectedDetailMetric?.let { metricKey ->
        AlertDialog(
            onDismissRequest = { selectedDetailMetric = null },
            confirmButton = {
                TextButton(onClick = { selectedDetailMetric = null }) {
                    Text(if (isBengali) "ঠিক আছে" else "Close", color = Color(0xFF2E7D32))
                }
            },
            title = {
                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    Icon(Icons.Default.Info, contentDescription = null, tint = Color(0xFF2E7D32))
                    Text(
                        text = getMetricTitle(metricKey, isBengali),
                        fontWeight = FontWeight.Bold,
                        fontSize = 16.sp,
                        color = Color(0xFF263238)
                    )
                }
            },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    Text(
                        text = getMetricExplanation(metricKey, bmi, bodyFat, bmr, tdee, idealWeightRobinson, lbmClamped, calorieRequirement, proteinRequirementGrams, waterRequirementMl, isBengali),
                        fontSize = 12.sp,
                        color = Color.DarkGray,
                        lineHeight = 18.sp
                    )
                    
                    // Display clinical references / advice
                    Card(
                        shape = RoundedCornerShape(8.dp),
                        colors = CardDefaults.cardColors(containerColor = Color(0xFFF5F7F6)),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(modifier = Modifier.padding(10.dp), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                            Text(
                                text = if (isBengali) "💡 ডায়েটিশিয়ান টিপস" else "💡 Dietitian Clinical Tip",
                                fontWeight = FontWeight.Bold,
                                fontSize = 11.sp,
                                color = Color(0xFFE65100)
                            )
                            Text(
                                text = getMetricDietTip(metricKey, isBengali),
                                fontSize = 10.5.sp,
                                color = Color.Gray,
                                lineHeight = 14.sp
                            )
                        }
                    }
                }
            }
        )
    }
}

@Composable
fun MetricCard(
    title: String,
    value: String,
    subtext: String,
    color: Color,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    Card(
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFFFAFAFA)),
        border = BorderStroke(1.dp, Color(0xFFECEFF1)),
        modifier = modifier
            .fillMaxWidth()
            .clickable { onClick() }
    ) {
        Column(
            modifier = Modifier.padding(10.dp),
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            Text(
                text = title,
                fontSize = 9.5.sp,
                fontWeight = FontWeight.Bold,
                color = Color.Gray
            )

            Text(
                text = value,
                fontSize = 15.sp,
                fontWeight = FontWeight.ExtraBold,
                color = color
            )

            Text(
                text = subtext,
                fontSize = 8.5.sp,
                color = Color.DarkGray,
                lineHeight = 10.sp
            )
        }
    }
}

// Helpers for calculations
fun getBmiStatus(bmi: Double, isBengali: Boolean): String {
    return if (bmi < 18.5) {
        if (isBengali) "কম ওজন (Underweight)" else "Underweight (<18.5)"
    } else if (bmi < 25.0) {
        if (isBengali) "স্বাভাবিক (Normal)" else "Normal (18.5-24.9)"
    } else if (bmi < 30.0) {
        if (isBengali) "অতি ওজন (Overweight)" else "Overweight (25-29.9)"
    } else {
        if (isBengali) "স্থূলতা (Obese)" else "Clinical Obese (>=30)"
    }
}

fun getBmiColor(bmi: Double): Color {
    return if (bmi < 18.5) {
        Color(0xFFFFB300)
    } else if (bmi < 25.0) {
        Color(0xFF2E7D32)
    } else if (bmi < 30.0) {
        Color(0xFFE65100)
    } else {
        Color(0xFFC62828)
    }
}

fun getBodyFatStatus(fat: Double, isMale: Boolean, isBengali: Boolean): String {
    if (isMale) {
        return if (fat < 6) (if (isBengali) "খুব কম (Essential)" else "Essential Fat")
        else if (fat < 14) (if (isBengali) "ফিটনেস অ্যাথলেট" else "Athletic / Fit")
        else if (fat < 18) (if (isBengali) "স্বাভাবিক (Fitness)" else "Optimal Fitness")
        else if (fat < 25) (if (isBengali) "গ্রহণযোগ্য (Average)" else "Acceptable")
        else (if (isBengali) "উচ্চ চর্বি (Obese)" else "Excessive Obese")
    } else {
        return if (fat < 14) (if (isBengali) "খুব কম (Essential)" else "Essential Fat")
        else if (fat < 21) (if (isBengali) "ফিটনেস অ্যাথলেট" else "Athletic / Fit")
        else if (fat < 25) (if (isBengali) "স্বাভাবিক (Fitness)" else "Optimal Fitness")
        else if (fat < 32) (if (isBengali) "গ্রহণযোগ্য (Average)" else "Acceptable")
        else (if (isBengali) "উচ্চ চর্বি (Obese)" else "Excessive Obese")
    }
}

fun getMetricTitle(key: String, isBengali: Boolean): String {
    return when (key) {
        "bmi" -> if (isBengali) "বডি মাস ইনডেক্স (BMI)" else "Body Mass Index (BMI)"
        "body_fat" -> if (isBengali) "শরীরের চর্বির শতাংশ (Body Fat %)" else "Body Fat Percentage"
        "bmr" -> if (isBengali) "বেসাল মেটাবলিক রেট (BMR)" else "Basal Metabolic Rate (BMR)"
        "tdee" -> if (isBengali) "মোট দৈনিক শক্তি খরচ (TDEE)" else "Total Daily Energy Spend (TDEE)"
        "ideal_weight" -> if (isBengali) "বিজ্ঞানসম্মত আদর্শ ওজন" else "Robinson's Ideal Body Weight"
        "lbm" -> if (isBengali) "লিন বডি মাস (LBM)" else "Lean Body Mass (LBM)"
        "calorie" -> if (isBengali) "দৈনিক প্রস্তাবিত ক্যালরি" else "Daily Calorie Goals"
        "protein" -> if (isBengali) "দৈনিক প্রোটিন চাহিদা" else "Daily Protein Requirements"
        else -> if (isBengali) "দৈনিক পানির চাহিদা" else "Daily Water intake target"
    }
}

fun getMetricExplanation(
    key: String,
    bmi: Double,
    bodyFat: Double,
    bmr: Double,
    tdee: Double,
    idealWeight: Double,
    lbm: Double,
    calorie: Int,
    protein: Double,
    water: Int,
    isBengali: Boolean
): String {
    return when (key) {
        "bmi" -> if (isBengali) {
            "আপনার বিএমআই হলো ${String.format("%.1f", bmi)}। এটি ওজন ও উচ্চতার বৈজ্ঞানিক অনুপাত।\n\n১৮.৫ থেকে ২৪.৯ হচ্ছে স্বাভাবিক স্বাস্থ্যকর পর্যায়। অতিরিক্ত কম বা বেশি বিএমআই হৃদরোগ ও টাইপ-২ ডায়াবেটিসের ঝুঁকি বাড়ায়।"
        } else {
            "Your Body Mass Index is ${String.format("%.1f", bmi)}. Underweight is defined as <18.5, Normal weight is 18.5-24.9, Overweight is 25-29.9, and Obese is >=30.0."
        }
        "body_fat" -> if (isBengali) {
            "আপনার শরীরের আনুমানিক মেদের পরিমাণ ${String.format("%.1f", bodyFat.coerceAtLeast(2.0))}%। এই মানটি ডিউরেনবার্গ ফর্মুলা প্রয়োগ করে বয়স, লিঙ্গ এবং বিএমআই সূচকের সাহায্যে গণিত হয়েছে। মেদ কমানো সামগ্রিক কার্ডিওভাসকুলার স্বাস্থ্যের উন্নতি ঘটায়।"
        } else {
            "Your estimated Body Fat is ${String.format("%.1f", bodyFat.coerceAtLeast(2.0))}%. Calculated using the clinical Deurenberg formula based on sex, age, and skeletal BMI. High body fat percentages put pressure on metabolic functions."
        }
        "bmr" -> if (isBengali) {
            "আপনার বিএমআর হলো ${bmr.toInt()} কি.ক্যালোরি। সম্পূর্ণ বিশ্রামের অবস্থায় আপনার শ্বাস-প্রশ্বাস, রক্তসঞ্চালন ও কোষ সচল রাখতে এই পরিমাণ শক্তি ব্যয় হয়। এটি Mifflin-St Jeor সুত্র দ্বারা গণনা করা হয়েছে।"
        } else {
            "Your Basal Metabolic Rate is ${bmr.toInt()} kcal/day. This represents the absolute minimum baseline thermal energy needed to maintain essential biological systems at absolute rest."
        }
        "tdee" -> if (isBengali) {
            "আপনার টিডিইই হলো ${tdee.toInt()} কি.ক্যালোরি। এটি নির্দেশ করে আপনার দৈনন্দিন কাজ ও পরিশ্রম সহ মোট কতটা ক্যালরি পুড়ে যাচ্ছে।"
        } else {
            "Your Total Daily Energy Expenditure (TDEE) is ${tdee.toInt()} kcal/day. This accounts for your metabolic rate plus extra heat energy burned through walking, work, and workouts."
        }
        "ideal_weight" -> if (isBengali) {
            "রবিনসন সমীকরণ অনুযায়ী আপনার আদর্শ ওজন হওয়া উচিত আনুমানিক ${idealWeight.toInt()} কেজি। একটি স্বাস্থ্যকর পরিধি হলো ${ (idealWeight * 0.9).toInt() } - ${ (idealWeight * 1.1).toInt() } কেজি।"
        } else {
            "According to Robinson's medical equation, your biological ideal weight is ${idealWeight.toInt()} kg. A healthy flexible margin is ${ (idealWeight * 0.9).toInt() } to ${ (idealWeight * 1.1).toInt() } kg."
        }
        "lbm" -> if (isBengali) {
            "আপনার চর্বিহীন কঙ্কাল ও পেশী ভর হলো ${String.format("%.1f", lbm)} কেজি। লিন বডি মাস বেশি থাকলে মেটাবলিজম বা হজম শক্তি বৃদ্ধি পায় এবং চর্বি সহজে জমা হয় না।"
        } else {
            "Your calculated Lean Body Mass (LBM) is ${String.format("%.1f", lbm)} kg. This indicates the total mass of your organs, bones, muscles, and water content without adipose fat tissue."
        }
        "calorie" -> if (isBengali) {
            "আপনার লক্ষ্য অনুযায়ী প্রস্তাবিত দৈনিক ক্যালরি ${calorie} kcal। সুস্থভাবে ফ্যাট কমাতে বা পেশী তৈরিতে আপনার টিডিইই এর সাথে এটি সামঞ্জস্য করা হয়েছে।"
        } else {
            "Your target calorie intake is ${calorie} kcal/day. This matches your selected goal to safely induce fat burning (deficit) or support lean protein loading (surplus) based on active TDEE expenditure."
        }
        "protein" -> if (isBengali) {
            "আপনার দৈনিক প্রোটিনের চাহিদা ন্যূনতম ${protein.toInt()} গ্রাম। প্রোটিন পেশী ক্ষয় রোধ করে, রক্তে গ্লুকোজ নিয়ন্ত্রণ করে এবং ডায়েটের সময় দীর্ঘক্ষণ পেট ভরা রাখে।"
        } else {
            "Your daily protein requirement is ${protein.toInt()} grams. Essential to construct and repair skeletal muscle fibers, support immune enzymes, and stabilize hunger hormones."
        }
        else -> if (isBengali) {
            "আপনার প্রতিদিনের সর্বনিম্ন পানির চাহিদা ${water} মিলি (${String.format("%.1f", water/1000.0)} লিটার)। জল মেটাবলিজম সচল রাখে ও ডিহাইড্রেশন দূর করে।"
        } else {
            "Your baseline water intake requirement is ${water} ml (${String.format("%.1f", water/1000.0)} Liters). Crucial to clear cellular toxic waste, facilitate kidney filtration, and hydrate active cells."
        }
    }
}

fun getMetricDietTip(key: String, isBengali: Boolean): String {
    return when (key) {
        "bmi" -> if (isBengali) {
            "আপনার বিএমআই অনুসারে আমাদের এআই ডায়েট প্ল্যানার সুষম খাবার নির্বাচন করে। নিয়মিত পরিমিত হাঁটাহাঁটি করুন।"
        } else {
            "Focus on nutrient density over strict starvation. Pair a moderate diet with simple resistance training to optimize BMI markers safely."
        }
        "body_fat" -> if (isBengali) {
            "শরীরের মেদ কমাতে আঁশযুক্ত শাকসবজি, শসা, লেবু জল এবং পর্যাপ্ত প্রোটিন গ্রহণ করুন। চিনি এড়িয়ে চলুন।"
        } else {
            "Prioritize fat reduction through low glycemic load traditional foods, and minimize carbonated or high fructose simple sugars."
        }
        "bmr" -> if (isBengali) {
            "পেশী ভর (LBM) বৃদ্ধি করলে আপনার স্বাভাবিক বিএমআর বা ক্যালরি পোড়ার হার বেড়ে যাবে।"
        } else {
            "Increasing lean skeletal muscle mass directly elevates your baseline BMR, boosting metabolism even while sleeping."
        }
        "tdee" -> if (isBengali) {
            "অফিসে এক জায়গায় বসে না থেকে প্রতি ঘন্টায় ৫ মিনিট করে হাঁটাহাঁটি করার চেষ্টা করুন।"
        } else {
            "Enhance TDEE without heavy fatigue by incorporating more Non-Exercise Activity (NEAT), like standing desks or daily stairs."
        }
        "ideal_weight" -> if (isBengali) {
            "ওজন বেশি হলে ক্রাশ ডায়েট না করে প্রতি সপ্তাহে ৫০০ গ্রাম চর্বি কমানোর লক্ষ্য নির্ধারণ করুন।"
        } else {
            "Never chase artificial weight metrics blindly. Focus on sustainable, realistic fat loss and strength building instead."
        }
        "lbm" -> if (isBengali) {
            "লিন মাস বজায় রাখতে খাবারের ৩০% প্রোটিন রাখুন এবং প্রতি রাতে ৭-৮ ঘন্টা গভীর ঘুম নিশ্চিত করুন।"
        } else {
            "Eat adequate high-quality proteins and secure 8 hours of restorative sleep to prevent cortisol-induced muscle atrophy."
        }
        "calorie" -> if (isBengali) {
            "দৈনিক এনার্জি ডায়েরি ব্যবহার করে খাওয়ার হিসাব রাখুন এবং অতিরিক্ত ভাজা-পোড়া খাবার বন্ধ করুন।"
        } else {
            "Track logged foods directly on your home screen. Small calorie tracking consistency creates massive weight loss success over time."
        }
        "protein" -> if (isBengali) {
            "ছোট মাছ, ডাল, ডিমের সাদা অংশ, মুরগি ও টফু আপনার প্রোটিনের চমৎকার উৎস হতে পারে।"
        } else {
            "Great local sources include eggs, dal (lentils), local fish, lean chicken, and peanuts."
        }
        else -> if (isBengali) {
            "ভোরে খালি পেটে ১ গ্লাস ঈষদুষ্ণ পানি এবং খাবারের ৩০ মিনিট আগে পানি পান করা অত্যন্ত উপকারী।"
        } else {
            "Drink 1 full glass of warm water right upon waking up to jumpstart digestive motility and flush midnight waste."
        }
    }
}
