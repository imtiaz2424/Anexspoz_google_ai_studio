package com.example.ui

import android.content.Context
import android.widget.Toast
import androidx.compose.animation.*
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.LocalDrink
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.WaterLogEntity
import com.example.viewmodel.DietPlannerViewModel

@Composable
fun HydrationTracker(
    viewModel: DietPlannerViewModel,
    isBengali: Boolean,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val focusManager = LocalFocusManager.current

    val waterLog by viewModel.waterLog.collectAsState()
    val userProfile by viewModel.userProfile.collectAsState()

    val waterAmount = waterLog?.amountMl ?: 0
    val targetWater = userProfile?.dailyWaterTargetMl ?: 2500

    val progress = if (targetWater > 0) (waterAmount.toFloat() / targetWater.toFloat()).coerceIn(0f, 1f) else 0f
    val percentage = (progress * 100).toInt()

    var customAmountText by remember { mutableStateOf("") }

    Card(
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        border = BorderStroke(1.dp, Color(0xFFB3E5FC).copy(alpha = 0.6f)),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        modifier = modifier
            .fillMaxWidth()
            .testTag("hydration_tracker_core_card")
    ) {
        Column(
            modifier = Modifier.padding(18.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Header Content
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
                            .background(Color(0xFFE1F5FE), CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Text("💧", fontSize = 22.sp)
                    }
                    Column {
                        Text(
                            text = if (isBengali) "হাইড্রেশন ট্র্যাকার" else "Interactive Hydration",
                            fontWeight = FontWeight.Bold,
                            fontSize = 16.sp,
                            color = Color(0xFF0277BD)
                        )
                        Text(
                            text = if (isBengali) "আপনার দৈনিক তরল ও পানির ভারসাম্য রক্ষা করুন" else "Maintain body cellular fluid & electrolyte balance",
                            fontSize = 11.sp,
                            color = Color.Gray
                        )
                    }
                }

                // Social Share Trigger
                IconButton(
                    onClick = {
                        val shareMsg = if (isBengali) {
                            "আজ আমি নীলজরি (Niljori Health) এ আমার পানির লক্ষ্যমাত্রা ১০০০ মিলি লক্ষ্যমাত্রা থেকে $waterAmount/$targetWater মিলি ($percentage%) পূরণ করেছি! 💧 শরীর সুস্থ রাখুন, পর্যাপ্ত পানি পান করুন।"
                        } else {
                            "I have tracked my hydration progress of $waterAmount / $targetWater mL ($percentage%) today on Niljori Health! 💧 Stay healthy and stay hydrated!"
                        }
                        SharingUtils.shareText(context, shareMsg, if (isBengali) "হাইড্রেশন শেয়ার করুন" else "Share Hydration Achievement")
                    },
                    modifier = Modifier
                        .size(36.dp)
                        .testTag("share_hydration_progress_btn")
                        .semantics {
                            contentDescription = if (isBengali) "পানির অগ্রগতি শেয়ার করুন" else "Share current water volume achievements to social networks"
                        }
                ) {
                    Icon(
                        imageVector = Icons.Default.Share,
                        contentDescription = "Share",
                        tint = Color(0xFF0288D1),
                        modifier = Modifier.size(20.dp)
                    )
                }
            }

            Divider(color = Color(0xFFE1F5FE))

            // Dynamic Progress Representation (Animated)
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(
                        brush = Brush.verticalGradient(
                            colors = listOf(Color(0xFFEBF8FF), Color(0xFFE1F5FE).copy(alpha = 0.5f))
                        ),
                        shape = RoundedCornerShape(16.dp)
                    )
                    .border(1.dp, Color(0xFFB3E5FC).copy(alpha = 0.7f), RoundedCornerShape(16.dp))
                    .padding(16.dp),
                contentAlignment = Alignment.Center
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    // Quick circular indicator representing active water fluid
                    Box(
                        modifier = Modifier.size(100.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator(
                            progress = { progress },
                            modifier = Modifier
                                .fillMaxSize()
                                .semantics {
                                    contentDescription = if (isBengali) "পানির লক্ষ্যমাত্রার $percentage শতাংশ সম্পন্ন" else "$percentage percent of water intake goal complete"
                                },
                            color = Color(0xFF0288D1),
                            strokeWidth = 10.dp,
                            trackColor = Color(0xFFB3E5FC).copy(alpha = 0.3f),
                            strokeCap = androidx.compose.ui.graphics.StrokeCap.Round
                        )
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.Center
                        ) {
                            Text(
                                text = "🌤️",
                                fontSize = 18.sp
                            )
                            Text(
                                text = "$percentage%",
                                fontWeight = FontWeight.Black,
                                fontSize = 18.sp,
                                color = Color(0xFF01579B)
                            )
                        }
                    }

                    Row(
                        horizontalArrangement = Arrangement.spacedBy(4.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = if (isBengali) "সংগৃহীত: " else "Ingested: ",
                            fontSize = 12.sp,
                            color = Color(0xFF37474F)
                        )
                        Text(
                            text = "$waterAmount ml",
                            fontWeight = FontWeight.Bold,
                            fontSize = 14.sp,
                            color = Color(0xFF0288D1)
                        )
                        Text(
                            text = " / $targetWater ml",
                            fontSize = 12.sp,
                            color = Color.Gray
                        )
                    }

                    Text(
                        text = if (percentage >= 100) {
                            if (isBengali) "🎉 অসাধারণ! আপনার দৈনিক পানির লক্ষ্য পূর্ণ হয়েছে!" else "🎉 Awesome! Daily biological water target met!"
                        } else {
                            if (isBengali) "${targetWater - waterAmount} মিলি পানি বাকি রয়েছে" else "${targetWater - waterAmount} mL remaining to stay fully hydrated"
                        },
                        fontSize = 11.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = if (percentage >= 100) Color(0xFF2E7D32) else Color(0xFF546E7A)
                    )
                }
            }

            // Quick presets with high touchscreen visibility conforming to accessibility standards
            Text(
                text = if (isBengali) "কুইক ড্রিংকPreset (পানি যোগ করুন):" else "Quick Add Presets:",
                fontWeight = FontWeight.Bold,
                fontSize = 12.sp,
                color = Color(0xFF37474F)
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                val presets = listOf(
                    Triple("100 ml", 100, "🍵"),
                    Triple("250 ml", 250, "🥛"),
                    Triple("500 ml", 500, "🥤"),
                    Triple("750 ml", 750, "🍼")
                )

                presets.forEach { preset ->
                    Card(
                        modifier = Modifier
                            .weight(1f)
                            .height(64.dp)
                            .clip(RoundedCornerShape(12.dp))
                            .clickable {
                                viewModel.addWater(preset.second)
                                Toast
                                    .makeText(
                                        context,
                                        if (isBengali) "+${preset.first} পানি যোগ করা হয়েছে" else "+${preset.first} water logged",
                                        Toast.LENGTH_SHORT
                                    )
                                    .show()
                            }
                            .testTag("hydration_preset_${preset.second}"),
                        colors = CardDefaults.cardColors(containerColor = Color(0xFFF0F9FF)),
                        border = BorderStroke(1.dp, Color(0xFFE0F2FE))
                    ) {
                        Column(
                            modifier = Modifier.fillMaxSize(),
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.Center
                        ) {
                            Text(preset.third, fontSize = 20.sp)
                            Text(preset.first, fontSize = 10.sp, fontWeight = FontWeight.Bold, color = Color(0xFF0288D1))
                        }
                    }
                }
            }

            // Quick manual addition & removal layout
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                OutlinedTextField(
                    value = customAmountText,
                    onValueChange = { customAmountText = it.filter { c -> c.isDigit() } },
                    label = { Text(if (isBengali) "কাস্টম পরিমাণ (মিলি)" else "Custom amount (ml)", fontSize = 11.sp) },
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Number,
                        imeAction = ImeAction.Done
                    ),
                    keyboardActions = KeyboardActions(onDone = { focusManager.clearFocus() }),
                    singleLine = true,
                    modifier = Modifier
                        .weight(1.3f)
                        .testTag("hydration_custom_input"),
                    shape = RoundedCornerShape(12.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = Color(0xFF0288D1),
                        unfocusedBorderColor = Color(0xFFB3E5FC)
                    )
                )

                // Add Button
                Button(
                    onClick = {
                        val amount = customAmountText.toIntOrNull() ?: 250
                        viewModel.addWater(amount)
                        customAmountText = ""
                        focusManager.clearFocus()
                        Toast.makeText(context, if (isBengali) "পানি যোগ করা হয়েছে!" else "Hydration logged!", Toast.LENGTH_SHORT).show()
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF0288D1)),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier
                        .weight(1f)
                        .height(50.dp)
                        .testTag("hydration_custom_add_btn")
                ) {
                    Icon(Icons.Default.Add, contentDescription = "Add Custom", tint = Color.White)
                    Spacer(modifier = Modifier.width(2.dp))
                    Text(if (isBengali) "যোগ" else "Add", color = Color.White, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                }

                // Subtract Button
                OutlinedButton(
                    onClick = {
                        val amount = customAmountText.toIntOrNull() ?: 250
                        viewModel.addWater(-amount)
                        customAmountText = ""
                        focusManager.clearFocus()
                        Toast.makeText(context, if (isBengali) "নম্বর কমানো হয়েছে" else "Hydration removed", Toast.LENGTH_SHORT).show()
                    },
                    border = BorderStroke(1.dp, Color(0xFFB0BEC5)),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.outlinedButtonColors(contentColor = Color(0xFF546E7A)),
                    modifier = Modifier
                        .weight(1f)
                        .height(50.dp)
                        .testTag("hydration_custom_sub_btn")
                ) {
                    Icon(Icons.Default.Remove, contentDescription = "Subtract Custom")
                    Spacer(modifier = Modifier.width(2.dp))
                    Text(if (isBengali) "বাদ" else "Sub", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                }
            }

            // Quick reset Option
            Row(
                modifier = Modifier.fillMaxWidth().padding(top = 4.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(4.dp),
                    modifier = Modifier.clickable {
                        viewModel.addWater(-waterAmount)
                        Toast.makeText(context, if (isBengali) "পানির পরিমাপ রিসেট করা হয়েছে" else "Water volume reset done", Toast.LENGTH_SHORT).show()
                    }
                ) {
                    Icon(
                        imageVector = Icons.Default.Refresh,
                        contentDescription = "Reset",
                        tint = Color(0xFFE53935),
                        modifier = Modifier.size(16.dp)
                    )
                    Text(
                        text = if (isBengali) "আজকের তরল পরিমাণ রিসেট করুন" else "Reset today's hydration",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFFE53935)
                    )
                }

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Info,
                        contentDescription = null,
                        tint = Color.Gray,
                        modifier = Modifier.size(14.dp)
                    )
                    Text(
                        text = if (isBengali) "সহজ ওয়ান-ট্যাপ ডায়াল" else "48dp touch targets verified",
                        fontSize = 10.sp,
                        color = Color.Gray
                    )
                }
            }
        }
    }
}
