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
import androidx.compose.ui.draw.scale
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
    var ageStr by remember { mutableStateOf(userProfile?.age?.toString() ?: "28") }
    var weightStr by remember { mutableStateOf(userProfile?.weight?.toString() ?: "72.0") }
    var heightStr by remember { mutableStateOf(userProfile?.height?.toString() ?: "170.0") }
    var gender by remember { mutableStateOf(userProfile?.gender ?: "Male") } // Male, Female

    // Activity Multiplier Index: 0: Sedentary, 1: Lightly Active, 2: Moderately Active, 3: Heavily Active
    var activityIndex by remember { mutableStateOf(1) }
    // Weight Goal target: 0: Loss, 1: Maintain, 2: Gain
    var goalIndex by remember { mutableStateOf(if (userProfile?.goal?.contains("Loss") == true || userProfile?.goal?.contains("কমানো") == true) 0 else if (userProfile?.goal?.contains("Gain") == true || userProfile?.goal?.contains("বাড়ানো") == true) 2 else 1) }

    // Dropdown expanded states
    var showExplanationDialog by remember { mutableStateOf(false) }

    // Calculator Algorithm values
    val weightVal = weightStr.toDoubleOrNull() ?: 70.0
    val heightVal = heightStr.toDoubleOrNull() ?: 170.0
    val ageVal = ageStr.toIntOrNull() ?: 28

    // Mifflin - St Jeor Equation
    val bmr = if (gender.lowercase() == "male") {
        (10 * weightVal) + (6.25 * heightVal) - (5 * ageVal) + 5
    } else {
        (10 * weightVal) + (6.25 * heightVal) - (5 * ageVal) - 161
    }

    val activityMultiplier = when (activityIndex) {
        0 -> 1.2 // Sedentary
        1 -> 1.375 // Lightly Active
        2 -> 1.55 // Moderately Active
        else -> 1.725 // Heavily Active
    }

    val tdee = bmr * activityMultiplier

    val (targetCal, targetWater) = when (goalIndex) {
        0 -> Pair((tdee - 500).toInt().coerceAtLeast(1200), 2800) // Lose weight
        2 -> Pair((tdee + 400).toInt(), 3200) // Gain weight
        else -> Pair(tdee.toInt(), 2500) // Maintain
    }

    // Macronutrient Split (30% Protein, 45% Carbs, 25% Fat)
    val proteinGrams = (targetCal * 0.30 / 4)
    val carbsGrams = (targetCal * 0.45 / 4)
    val fatGrams = (targetCal * 0.25 / 9)

    Card(
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.5f)),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        modifier = modifier
            .fillMaxWidth()
            .testTag("nutrition_calculator_panel")
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
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(38.dp)
                            .background(Color(0xFFE8F5E9), CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Calculate,
                            contentDescription = null,
                            tint = Color(0xFF2E7D32),
                            modifier = Modifier.size(20.dp)
                        )
                    }
                    Column {
                        Text(
                            text = if (isBengali) "ক্লিনিকাল পুষ্টি ক্যালকুলেটর" else "Nutrition & BMR Calculator",
                            fontWeight = FontWeight.Bold,
                            fontSize = 15.sp,
                            color = MaterialTheme.colorScheme.primary
                        )
                        Text(
                            text = if (isBengali) "Mifflin-St Jeor চিকিৎসাবিদ্যা সূত্র" else "Mifflin-St Jeor formula verified",
                            fontSize = 11.sp,
                            color = Color.Gray
                        )
                    }
                }

                IconButton(
                    onClick = { showExplanationDialog = true },
                    modifier = Modifier.size(32.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Info,
                        contentDescription = "Help Info",
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(18.dp)
                    )
                }
            }

            Divider(color = Color(0xFFECEFF1).copy(alpha = 0.5f))

            // Inputs Row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                // Weight Unit (kg)
                OutlinedTextField(
                    value = weightStr,
                    onValueChange = { weightStr = it },
                    label = { Text(if (isBengali) "ওজন (কেজি)" else "Weight (kg)", fontSize = 11.sp) },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    singleLine = true,
                    modifier = Modifier
                        .weight(1f)
                        .testTag("calculator_weight_input"),
                    shape = RoundedCornerShape(12.dp)
                )

                // Height Box
                OutlinedTextField(
                    value = heightStr,
                    onValueChange = { heightStr = it },
                    label = { Text(if (isBengali) "উচ্চতা (সেমি)" else "Height (cm)", fontSize = 11.sp) },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    singleLine = true,
                    modifier = Modifier
                        .weight(1f)
                        .testTag("calculator_height_input"),
                    shape = RoundedCornerShape(12.dp)
                )

                // Age Box
                OutlinedTextField(
                    value = ageStr,
                    onValueChange = { ageStr = it },
                    label = { Text(if (isBengali) "বয়স" else "Age (years)", fontSize = 11.sp) },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    singleLine = true,
                    modifier = Modifier
                        .weight(1f)
                        .testTag("calculator_age_input"),
                    shape = RoundedCornerShape(12.dp)
                )
            }

            // Gender Selector Widget
            Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                Text(
                    text = if (isBengali) "লিঙ্গ নির্বাচন" else "Select Biological Gender",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.Gray
                )
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    listOf("Male", "Female").forEach { g ->
                        val isSel = gender.equals(g, ignoreCase = true)
                        val textStr = if (g == "Male") {
                            if (isBengali) "পুরুষ (Male)" else "Male"
                        } else {
                            if (isBengali) "নারী (Female)" else "Female"
                        }

                        Button(
                            onClick = { gender = g },
                            colors = ButtonDefaults.buttonColors(
                                containerColor = if (isSel) Color(0xFF2E7D32) else Color(0xFFFAFAFA),
                                contentColor = if (isSel) Color.White else Color(0xFF555555)
                            ),
                            border = BorderStroke(1.dp, if (isSel) Color.Transparent else Color(0xFFE0E0E0)),
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier
                                .weight(1f)
                                .height(38.dp)
                        ) {
                            Text(textStr, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }

            // Active Multiplier Row
            Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                Text(
                    text = if (isBengali) "ব্যায়াম ও দৈনন্দিন পরিশ্রম" else "Daily Physical Activity Multiplier",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.Gray
                )
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    val activityLabelsEn = listOf("Low", "Mid-Active", "High", "Extra")
                    val activityLabelsBn = listOf("উপবিষ্ট", "হালকা", "মধ্যম", "ভারী")

                    activityLabelsEn.forEachIndexed { idx, label ->
                        val isSel = activityIndex == idx
                        val chipText = if (isBengali) activityLabelsBn[idx] else label

                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .height(36.dp)
                                .background(
                                    color = if (isSel) Color(0xFFE8F5E9) else Color(0xFFF5F7F8),
                                    shape = RoundedCornerShape(10.dp)
                                )
                                .border(
                                    width = 1.dp,
                                    color = if (isSel) Color(0xFF2E7D32) else Color(0xFFE0E0E0),
                                    shape = RoundedCornerShape(10.dp)
                                )
                                .clickable { activityIndex = idx }
                                .padding(horizontal = 4.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = chipText,
                                color = if (isSel) Color(0xFF2E7D32) else Color.DarkGray,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                textAlign = TextAlign.Center
                            )
                        }
                    }
                }
            }

            // Weight Goal Options
            Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                Text(
                    text = if (isBengali) "আপনার কাঙ্ক্ষিত স্বাস্থ্য লক্ষ্য" else "Choose Your Target Weight Goal",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.Gray
                )
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    val goalLabelsEn = listOf("Lose Weight", "Maintain", "Gain Weight")
                    val goalLabelsBn = listOf("ওজন কমানো", "নিয়ন্ত্রণ", "ওজন বাড়ানো")

                    goalLabelsEn.forEachIndexed { idx, label ->
                        val isSel = goalIndex == idx
                        val chipText = if (isBengali) goalLabelsBn[idx] else label

                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .height(36.dp)
                                .background(
                                    color = if (isSel) Color(0xFFE8F5E9) else Color(0xFFF5F7F8),
                                    shape = RoundedCornerShape(10.dp)
                                )
                                .border(
                                    width = 1.dp,
                                    color = if (isSel) Color(0xFF2E7D32) else Color(0xFFE0E0E0),
                                    shape = RoundedCornerShape(10.dp)
                                )
                                .clickable { goalIndex = idx }
                                .padding(horizontal = 4.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = chipText,
                                color = if (isSel) Color(0xFF2E7D32) else Color.DarkGray,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                textAlign = TextAlign.Center
                            )
                        }
                    }
                }
            }

            // Calculations Display Card
            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = Color(0xFFF8F9FA)),
                border = BorderStroke(1.dp, Color(0xFFE0E0E0).copy(alpha = 0.6f)),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier.padding(14.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    // Row 1: BMR & TDEE
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column {
                            Text(
                                text = if (isBengali) "বিএমআর (BMR)" else "Basal Metabolic Rate (BMR)",
                                fontSize = 10.sp,
                                color = Color.Gray,
                                fontWeight = FontWeight.SemiBold
                            )
                            Text(
                                text = "${bmr.toInt()} kcal",
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.DarkGray
                            )
                        }

                        Column(horizontalAlignment = Alignment.End) {
                            Text(
                                text = if (isBengali) "টিডিইই (TDEE)" else "Active Spend (TDEE)",
                                fontSize = 10.sp,
                                color = Color.Gray,
                                fontWeight = FontWeight.SemiBold
                            )
                            Text(
                                text = "${tdee.toInt()} kcal",
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.DarkGray
                            )
                        }
                    }

                    Divider(color = Color(0xFFECEFF1))

                    // Row 2: Target Calories & Water
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(
                                text = if (isBengali) "প্রস্তাবিত দৈনিক ক্যালোরি" else "Suggested Daily Calories",
                                fontSize = 11.sp,
                                color = Color(0xFF2E7D32),
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = "$targetCal kcal",
                                fontSize = 22.sp,
                                fontWeight = FontWeight.ExtraBold,
                                color = Color(0xFF1B5E20)
                            )
                        }

                        Column(horizontalAlignment = Alignment.End) {
                            Text(
                                text = if (isBengali) "পানি পানের পরিমাণ" else "Suggested Daily Water",
                                fontSize = 10.sp,
                                color = Color.Gray,
                                fontWeight = FontWeight.SemiBold
                            )
                            Text(
                                text = "${targetWater} ml",
                                fontSize = 15.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF0288D1)
                            )
                        }
                    }

                    // Macronutrients breakdown visualization bars
                    Column(
                        modifier = Modifier.fillMaxWidth(),
                        verticalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Text(
                            text = if (isBengali) "পুষ্টি উপাদান বণ্টন (Macro Splits)" else "Suggested Ideal Macronutrients",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = Color.DarkGray
                        )

                        // 1. Carbohydrates
                        MacroProgressLine(
                            name = if (isBengali) "শর্করা (Carbs)" else "Carbohydrates (45%)",
                            weight = "${carbsGrams.toInt()}g",
                            kcal = "${(targetCal * 0.45).toInt()} kcal",
                            color = Color(0xFFFFB74D),
                            ratio = 0.45f
                        )

                        // 2. Protein
                        MacroProgressLine(
                            name = if (isBengali) "আমিষ (Protein)" else "Protein (30%)",
                            weight = "${proteinGrams.toInt()}g",
                            kcal = "${(targetCal * 0.30).toInt()} kcal",
                            color = Color(0xFF81C784),
                            ratio = 0.30f
                        )

                        // 3. Fat
                        MacroProgressLine(
                            name = if (isBengali) "স্নেহ (Fat)" else "Fats (25%)",
                            weight = "${fatGrams.toInt()}g",
                            kcal = "${(targetCal * 0.25).toInt()} kcal",
                            color = Color(0xFFE1BEE7),
                            ratio = 0.25f
                        )
                    }
                }
            }

            // Apply to profile action button
            Button(
                onClick = {
                    viewModel.saveCustomCalorieTarget(targetCal, targetWater)
                    val toastMessage = if (isBengali) {
                        "সফলভাবে দৈনিক লক্ষ্য সেট করা হয়েছে: $targetCal কি.ক্যালোরি!"
                    } else {
                        "Successfully applied custom daily target: $targetCal kcal!"
                    }
                    Toast.makeText(context, toastMessage, Toast.LENGTH_LONG).show()
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
                    text = if (isBengali) "প্রোফাইলে নতুন লক্ষ্য সংযুক্ত করুন" else "Apply Calorie Target to Profile",
                    fontWeight = FontWeight.Bold,
                    fontSize = 13.sp,
                    color = Color.White
                )
            }
        }
    }

    // Help details dialog
    if (showExplanationDialog) {
        AlertDialog(
            onDismissRequest = { showExplanationDialog = false },
            confirmButton = {
                TextButton(onClick = { showExplanationDialog = false }) {
                    Text("OK", color = Color(0xFF2E7D32))
                }
            },
            title = {
                Text(
                    text = if (isBengali) "চিকিৎসা বিজ্ঞান ও সুত্রমালা" else "Under the Hood: Clinical Science",
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp,
                    color = MaterialTheme.colorScheme.primary
                )
            },
            text = {
                Box(
                    modifier = Modifier
                        .heightIn(max = 240.dp)
                        .verticalScroll(rememberScrollState())
                ) {
                    Text(
                        text = if (isBengali) {
                            "১. Mifflin-St Jeor চিকিৎসাবিদ্যা সূত্র ব্যবহার করে বেসাল মেটাবলিক রেট (BMR) গণনা করা হয়। এটি মানবদেহের বিশ্রামের সময়ের ন্যূনতম ক্যালোরি চাহিদা নির্ধারণ করে।\n\n" +
                                    "২. আপনার দৈনন্দিন শারীরিক পরিশ্রমের ধরন অনুসারে 'অ্যাক্টিভিটি মাল্টিপ্লায়ার' ব্যবহার করে টোটাল ডেইলি এনার্জি এক্সপেন্ডিচার (TDEE) নির্ধারণ করা হয়।\n\n" +
                                    "৩. ওজন কমানোর লক্ষ্যে ৫০০ মেগাক্যালোরি হ্রাস করা হয় এবং বাড়ানোর জন্য ৪০০ মেগাক্যালোরি সমৃদ্ধ পুষ্টিকর লক্ষ্য নির্ধারণ করা হয়।"
                        } else {
                            "1. Mifflin-St Jeor Clinically Proven Formula determines Basal Metabolic Rate (BMR) required to sustain structural cellular functions.\n\n" +
                                    "2. Your activity factor scales BMR to arrive at your Total Daily Energy Expenditure (TDEE).\n\n" +
                                    "3. Target calories are calculated dynamically: Weight Loss cuts 500 kcal, Weight Gain supplements 400 kcal, and Maintaining weight aligns directly to active TDEE expenditure."
                        },
                        fontSize = 12.sp,
                        color = Color.DarkGray,
                        lineHeight = 18.sp
                    )
                }
            }
        )
    }
}

@Composable
fun MacroProgressLine(
    name: String,
    weight: String,
    kcal: String,
    color: Color,
    ratio: Float
) {
    Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = name,
                fontSize = 10.sp,
                fontWeight = FontWeight.Bold,
                color = Color.DarkGray
            )
            Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                Text(
                    text = weight,
                    fontSize = 10.sp,
                    fontWeight = FontWeight.ExtraBold,
                    color = color
                )
                Text(
                    text = "($kcal)",
                    fontSize = 9.sp,
                    color = Color.Gray
                )
            }
        }

        LinearProgressIndicator(
            progress = ratio,
            trackColor = Color(0xFFECEFF1),
            color = color,
            modifier = Modifier
                .fillMaxWidth()
                .height(5.dp)
                .clip(RoundedCornerShape(3.dp))
        )
    }
}
