package com.example.ui

import android.app.DatePickerDialog
import androidx.compose.animation.*
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.*
import com.example.viewmodel.DietPlannerViewModel
import java.text.SimpleDateFormat
import java.util.*

// ==========================================
// PROFILE INITIAL SETUP VIEW
// ==========================================
@Composable
fun ProfileSetupView(
    isBengali: Boolean,
    onSave: (Int, String, Double, Double, String, String, String, String, String) -> Unit
) {
    var ageText by remember { mutableStateOf("25") }
    var weightText by remember { mutableStateOf("70.0") }
    var heightText by remember { mutableStateOf("175.0") }
    var selectedGender by remember { mutableStateOf("Male") }
    var selectedGoal by remember { mutableStateOf("Weight Loss") }
    var selectedPreference by remember { mutableStateOf("Non-Vegetarian") }
    var allergiesInput by remember { mutableStateOf("") }
    var medicalInput by remember { mutableStateOf("None") }
    var cuisineInput by remember { mutableStateOf("Bengali") }

    val genders = listOf("Male", "Female", "Other")
    val goals = listOf("Weight Loss", "Maintain Weight", "Weight Gain")
    val preferences = listOf("Vegetarian", "Non-Vegetarian", "Vegan", "Keto")

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(20.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Welcoming Branding Logo
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(10.dp),
            modifier = Modifier.padding(vertical = 12.dp)
        ) {
            ANEXSOPZModernLogo(
                modifier = Modifier.size(64.dp),
                showText = true,
                isBengali = isBengali
            )
        }

        Text(
            text = if (isBengali) "আপনার ফিটনেস প্রোফাইল তৈরি করুন" else "Create Your Personalized Dashboard",
            fontWeight = FontWeight.Black,
            fontSize = 18.sp,
            color = Color(0xFF1E5E2F),
            textAlign = TextAlign.Center
        )

        Text(
            text = if (isBengali)
                "আমরা আপনার বডি মাস ইনডেক্স (BMI) এবং ক্যালোরি লক্ষ্য বৈজ্ঞানিক Harris-Benedict সূত্রে গণনা করবো।"
            else
                "We customize baseline targets using your height, age, and clinical health inputs instantly.",
            fontSize = 11.5.sp,
            color = Color.Gray,
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(horizontal = 8.dp)
        )

        Card(
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            border = BorderStroke(1.dp, Color(0xFFECEFF1)),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(
                modifier = Modifier.padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                // Age Input
                OutlinedTextField(
                    value = ageText,
                    onValueChange = { ageText = it },
                    label = { Text(if (isBengali) "বয়স (Age)" else "Age (Years)") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    modifier = Modifier.fillMaxWidth().testTag("profile_age_input"),
                    shape = RoundedCornerShape(12.dp)
                )

                // Height and Weight Row
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    OutlinedTextField(
                        value = heightText,
                        onValueChange = { heightText = it },
                        label = { Text(if (isBengali) "উচ্চতা (সেমি)" else "Height (cm)") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                        modifier = Modifier.weight(1f).testTag("profile_height_input"),
                        shape = RoundedCornerShape(12.dp)
                    )

                    OutlinedTextField(
                        value = weightText,
                        onValueChange = { weightText = it },
                        label = { Text(if (isBengali) "ওজন (কেজি)" else "Weight (kg)") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                        modifier = Modifier.weight(1f).testTag("profile_weight_input"),
                        shape = RoundedCornerShape(12.dp)
                    )
                }

                // Gender Options Dropdown Selector Replacement
                Column {
                    Text(text = if (isBengali) "লিঙ্গ বাছুন (Gender):" else "Gender:", fontSize = 12.sp, color = Color.Gray)
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        genders.forEach { gender ->
                            val isSel = selectedGender == gender
                            Box(
                                modifier = Modifier
                                    .weight(1f)
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(if (isSel) Color(0xFF2E7D32) else Color(0xFFF1F8E9))
                                    .border(1.dp, if (isSel) Color(0xFF1B5E20) else Color(0xFFC8E6C9), RoundedCornerShape(8.dp))
                                    .clickable { selectedGender = gender }
                                    .padding(vertical = 10.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = gender,
                                    fontWeight = FontWeight.Bold,
                                    color = if (isSel) Color.White else Color(0xFF2E7D32),
                                    fontSize = 12.sp
                                )
                            }
                        }
                    }
                }

                // Goal Options Selection
                Column {
                    Text(text = if (isBengali) "আপনার লক্ষ্য (Fitness Goal):" else "Target Fitness Goal:", fontSize = 12.sp, color = Color.Gray)
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        goals.forEach { goal ->
                            val isSel = selectedGoal == goal
                            Box(
                                modifier = Modifier
                                    .weight(1f)
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(if (isSel) Color(0xFF2E7D32) else Color(0xFFF1F8E9))
                                    .border(1.dp, if (isSel) Color(0xFF1B5E20) else Color(0xFFC8E6C9), RoundedCornerShape(8.dp))
                                    .clickable { selectedGoal = goal }
                                    .padding(vertical = 10.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = goal,
                                    fontWeight = FontWeight.Bold,
                                    color = if (isSel) Color.White else Color(0xFF2E7D32),
                                    fontSize = 11.sp,
                                    textAlign = TextAlign.Center
                                )
                            }
                        }
                    }
                }

                // Preference Selection
                Column {
                    Text(text = if (isBengali) "খাবারের ধরণ (Diet Preference):" else "Dietary Preference:", fontSize = 12.sp, color = Color.Gray)
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        preferences.forEach { pref ->
                            val isSel = selectedPreference == pref
                            Box(
                                modifier = Modifier
                                    .weight(1f)
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(if (isSel) Color(0xFF2E7D32) else Color(0xFFF1F8E9))
                                    .border(1.dp, if (isSel) Color(0xFF1B5E20) else Color(0xFFC8E6C9), RoundedCornerShape(8.dp))
                                    .clickable { selectedPreference = pref }
                                    .padding(vertical = 8.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = pref,
                                    fontWeight = FontWeight.Bold,
                                    color = if (isSel) Color.White else Color(0xFF2E7D32),
                                    fontSize = 10.5.sp
                                )
                            }
                        }
                    }
                }

                // Allergies text field
                OutlinedTextField(
                    value = allergiesInput,
                    onValueChange = { allergiesInput = it },
                    label = { Text(if (isBengali) "অ্যালার্জিসমূহ (Allergies)" else "Allergies / Restrictions") },
                    placeholder = { Text("e.g., Peanuts, Shrimp, Milk") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth().testTag("profile_allergies_input"),
                    shape = RoundedCornerShape(12.dp)
                )
            }
        }

        Button(
            onClick = {
                val age = ageText.toIntOrNull() ?: 25
                val w = weightText.toDoubleOrNull() ?: 70.0
                val h = heightText.toDoubleOrNull() ?: 175.0
                onSave(age, selectedGender, w, h, selectedGoal, selectedPreference, allergiesInput.trim(), medicalInput, cuisineInput)
            },
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF2E7D32)),
            shape = RoundedCornerShape(14.dp),
            modifier = Modifier
                .fillMaxWidth()
                .height(48.dp)
                .testTag("save_profile_button")
        ) {
            Text(
                text = if (isBengali) "সেভ করুন এবং প্রবেশ করুন" else "Create Personal Health Board",
                fontWeight = FontWeight.Bold,
                fontSize = 14.sp,
                color = Color.White
            )
        }
    }
}

// ==========================================
// TAB 2: EXPLORE COMPREHENSIVE TAB
// ==========================================
@Composable
fun ExploreTab(
    viewModel: DietPlannerViewModel,
    isBengali: Boolean,
    onNavigateToTab: (Int) -> Unit
) {
    val recipes by viewModel.allRecipes.collectAsState()
    var selectedRecipe by remember { mutableStateOf<RecipeEntity?>(null) }
    var recipeSearchQuery by remember { mutableStateOf("") }

    val filtered = remember(recipes, recipeSearchQuery) {
        recipes.filter {
            it.title.lowercase(Locale.ROOT).contains(recipeSearchQuery.lowercase(Locale.ROOT)) ||
                    it.titleBn.contains(recipeSearchQuery)
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Welcome and App Identity Banner
        Card(
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(containerColor = Color(0xFFFFFAED)),
            border = BorderStroke(1.dp, Color(0xFFFFECB3)),
            modifier = Modifier.fillMaxWidth()
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(18.dp),
                horizontalArrangement = Arrangement.spacedBy(14.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text("🍉", fontSize = 42.sp)

                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = if (isBengali) "পুষ্টি এবং ডায়েট রেসিপিস" else "Healthy Recipes Library",
                        fontWeight = FontWeight.Bold,
                        fontSize = 15.sp,
                        color = Color(0xFF5D4037)
                    )
                    Text(
                        text = if (isBengali)
                            "আমাদের সংগৃহীত রেসিপি ভাণ্ডার আপনার সুস্থ শরীরের সহায়ক হিসেবে সতেজ ও দেশীয় উপাদানের ভিত্তিতে তৈরি।"
                        else
                            "Explore carefully structured traditional and biological menus conforming to low carb, low glycemic wellness principles.",
                        fontSize = 11.sp,
                        color = Color(0xFF795548),
                        lineHeight = 15.sp
                    )
                }
            }
        }

        // Horizontal Preset Category chips inside Explore
        var selectedCategoryIndex by remember { mutableStateOf(0) }
        val categories = listOf("All", "Breakfast", "Lunch", "Dinner", "Snacks")
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .horizontalScroll(rememberScrollState()),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            categories.forEachIndexed { i, cat ->
                val isSel = selectedCategoryIndex == i
                FilterChip(
                    selected = isSel,
                    onClick = {
                        selectedCategoryIndex = i
                        recipeSearchQuery = if (i == 0) "" else cat
                    },
                    label = { Text(cat) },
                    colors = FilterChipDefaults.filterChipColors(
                        selectedContainerColor = Color(0xFFE8F5E9),
                        selectedLabelColor = Color(0xFF2E7D32)
                    )
                )
            }
        }

        // Custom Search Recipe Bar inside Explore Tab
        OutlinedTextField(
            value = recipeSearchQuery,
            onValueChange = { recipeSearchQuery = it },
            placeholder = { Text(if (isBengali) "রেসিপি বা উপকরণ খুঁজুন..." else "Search healthy recipes...") },
            leadingIcon = { Icon(Icons.Default.Search, contentDescription = null) },
            singleLine = true,
            modifier = Modifier
                .fillMaxWidth()
                .testTag("recipe_search_input"),
            shape = RoundedCornerShape(12.dp)
        )

        // Vertical List representing the recipe cards
        Column(
            modifier = Modifier.fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            filtered.forEach { recipe ->
                Card(
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    border = BorderStroke(1.dp, Color(0xFFECEFF1)),
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { selectedRecipe = recipe }
                ) {
                    Column(modifier = Modifier.padding(14.dp)) {
                        Row(
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text(
                                text = if (isBengali) recipe.titleBn else recipe.title,
                                fontWeight = FontWeight.Bold,
                                fontSize = 14.sp,
                                color = Color(0xFF1E5E2F)
                            )

                            IconButton(
                                onClick = { viewModel.toggleRecipeFavorite(recipe.id, !recipe.isFavorite) },
                                modifier = Modifier.testTag("recipe_fav_button_${recipe.id}")
                            ) {
                                Icon(
                                    imageVector = if (recipe.isFavorite) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
                                    contentDescription = "Favorite",
                                    tint = if (recipe.isFavorite) Color.Red else Color.LightGray
                                )
                            }
                        }

                        Text(
                            text = if (isBengali) recipe.tipBn else recipe.tip,
                            fontSize = 11.5.sp,
                            color = Color.Gray,
                            lineHeight = 15.sp,
                            modifier = Modifier.padding(vertical = 4.dp)
                        )

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(6.dp))
                                    .background(Color(0xFFE8F5E9))
                                    .padding(horizontal = 6.dp, vertical = 2.dp)
                            ) {
                                Text(
                                    text = if (recipe.calories.contains("kcal")) recipe.calories else "${recipe.caloriesValue} kcal",
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 10.sp,
                                    color = Color(0xFF2E7D32)
                                )
                            }

                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(6.dp))
                                    .background(Color(0xFFE3F2FD))
                                    .padding(horizontal = 6.dp, vertical = 2.dp)
                            ) {
                                Text(
                                    text = if (isBengali) "প্রস্তুত সময়: ${recipe.durationBn}" else "Ready in ${recipe.duration}",
                                    fontSize = 10.sp,
                                    color = Color(0xFF1E88E5)
                                )
                            }
                        }
                    }
                }
            }

            if (filtered.isEmpty()) {
                Text(
                    text = "No compatible delicious dishes found with matching queries.",
                    fontStyle = FontStyle.Italic,
                    color = Color.LightGray,
                    fontSize = 12.sp,
                    modifier = Modifier.padding(16.dp)
                )
            }
        }

        // Show details dialog / sheet of the active recipe
        selectedRecipe?.let { recipe ->
            // In-memory ingredients checklist
            val checkedMap by viewModel.recipeCheckedIngredients.collectAsState()
            val currentRecipeCheckedIndexSet = checkedMap[recipe.title] ?: emptySet()

            AlertDialog(
                onDismissRequest = { selectedRecipe = null },
                title = {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = if (isBengali) recipe.titleBn else recipe.title,
                            fontWeight = FontWeight.Black,
                            fontSize = 16.sp
                        )
                        IconButton(onClick = { selectedRecipe = null }) {
                            Icon(Icons.Default.Close, contentDescription = "Close")
                        }
                    }
                },
                text = {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .verticalScroll(rememberScrollState()),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Text(
                            text = if (isBengali) recipe.tipBn else recipe.tip,
                            fontSize = 12.sp,
                            fontStyle = FontStyle.Italic,
                            color = Color.DarkGray
                        )

                        // Nutrients badge
                        Row(
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(Color(0xFFE8F5E9))
                                    .padding(horizontal = 8.dp, vertical = 4.dp)
                            ) {
                                Text("Calories: ${recipe.caloriesValue} kcal", fontSize = 11.sp, color = Color(0xFF1E5E2F), fontWeight = FontWeight.Bold)
                            }
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(Color(0xFFFFF3E0))
                                    .padding(horizontal = 8.dp, vertical = 4.dp)
                            ) {
                                val p = (recipe.caloriesValue * 0.25 / 4).toInt()
                                val c = (recipe.caloriesValue * 0.45 / 4).toInt()
                                val f = (recipe.caloriesValue * 0.3 / 9).toInt()
                                Text("P: ${p}g | C: ${c}g | F: ${f}g", fontSize = 11.sp, color = Color(0xFFE65100))
                            }
                        }

                        // Checklist of Ingredients
                        Text(
                            text = if (isBengali) "রান্নার প্রয়োজনীয় উপকরণসমূহ (Checklist):" else "Interactive Ingredients Checker:",
                            fontWeight = FontWeight.Bold,
                            fontSize = 13.sp,
                            color = Color.Gray
                        )

                        val ingList = if (isBengali) recipe.ingredientsBn else recipe.ingredients
                        Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                            ingList.forEachIndexed { idx, ing ->
                                val isChecked = currentRecipeCheckedIndexSet.contains(idx)
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .clickable { viewModel.toggleRecipeIngredientChecked(recipe.title, idx) }
                                        .background(if (isChecked) Color(0xFFF1F8E9) else Color.Transparent, RoundedCornerShape(6.dp))
                                        .padding(vertical = 4.dp, horizontal = 6.dp),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                                ) {
                                    Icon(
                                        imageVector = if (isChecked) Icons.Default.CheckCircle else Icons.Default.RadioButtonUnchecked,
                                        contentDescription = null,
                                        tint = if (isChecked) Color(0xFF2E7D32) else Color.Gray,
                                        modifier = Modifier.size(16.dp)
                                    )
                                    Text(
                                        text = ing,
                                        fontSize = 11.5.sp,
                                        color = if (isChecked) Color.Gray else Color.Black
                                    )
                                }
                            }
                        }

                        Divider()

                        // Cooking instructions text block
                        Text(
                            text = if (isBengali) "প্রস্তুত প্রণালী (Steps):" else "Step-by-Step Directions:",
                            fontWeight = FontWeight.Bold,
                            fontSize = 13.sp,
                            color = Color.Gray
                        )

                        val stepsList = if (isBengali) recipe.stepsBn else recipe.steps
                        stepsList.forEachIndexed { sIdx, step ->
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(18.dp)
                                        .background(Color(0xFF2E7D32), CircleShape),
                                    contentAlignment = Alignment.Center
                                    ) {
                                    Text("${sIdx + 1}", fontSize = 10.sp, color = Color.White, fontWeight = FontWeight.Bold)
                                }
                                Text(text = step, fontSize = 11.5.sp, color = Color.DarkGray, lineHeight = 15.sp)
                            }
                        }
                    }
                },
                confirmButton = {}
            )
        }
    }
}

// ==========================================
// TAB 3: HEALTH PROGRESS & TRACKS TAB
// ==========================================
@Composable
fun ProgressTrackerTab(
    viewModel: DietPlannerViewModel,
    weightLogs: List<WeightLogEntity>,
    waterLog: WaterLogEntity?,
    userProfile: UserProfileEntity,
    selectedDate: String
) {
    var weightInputText by remember { mutableStateOf("") }
    val isBengali by viewModel.isBengali.collectAsState()
    val waterAmount = waterLog?.amountMl ?: 0
    val targetWater = userProfile.dailyWaterTargetMl

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Welcome and App Identity Banner
        Card(
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(containerColor = Color(0xFFF1F8E9)),
            modifier = Modifier.fillMaxWidth()
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(18.dp),
                horizontalArrangement = Arrangement.spacedBy(14.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text("📈", fontSize = 42.sp)

                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = if (isBengali) "ওজন বৃদ্ধি এবং হাইড্রেশন" else "Active Progress Companion",
                        fontWeight = FontWeight.Bold,
                        fontSize = 15.sp,
                        color = Color(0xFF1E5E2F)
                    )
                    Text(
                        text = if (isBengali)
                            "আপনার দৈনিক লক্ষ্য অর্জনের জন্য পানি এবং ওজন ডেটা ট্র্যাক করতে শুরু করুন।"
                        else
                            "Track hydration logs in milliliters and monitor weight change baseline cycles seamlessly.",
                        fontSize = 11.sp,
                        color = Color.DarkGray,
                        lineHeight = 15.sp
                    )
                }
            }
        }

        // WATER HYDRATION CONTROLS CARD
        Card(
            shape = RoundedCornerShape(24.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            border = BorderStroke(1.dp, Color(0xFFECEFF1)),
            modifier = Modifier
                .fillMaxWidth()
                .testTag("water_tracker_card")
        ) {
            Column(
                modifier = Modifier.padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
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
                                .size(36.dp)
                                .background(Color(0xFFE3F2FD), CircleShape),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.LocalDrink, // Water glass icon
                                contentDescription = "Water Tracker",
                                tint = Color(0xFF1E88E5),
                                modifier = Modifier.size(18.dp)
                            )
                        }
                        Text(
                            text = if (isBengali) "দৈনিক পানি পানের মাত্রা" else "Daily Hydration Water Tracker",
                            fontWeight = FontWeight.Bold,
                            fontSize = 15.sp,
                            color = Color(0xFF1E88E5)
                        )
                    }

                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(8.dp))
                            .background(Color(0xFFE3F2FD))
                            .padding(horizontal = 8.dp, vertical = 4.dp)
                    ) {
                        Text(
                            text = if (isBengali) "${waterAmount} / ${targetWater} মি.লি." else "$waterAmount / $targetWater mL",
                            fontWeight = FontWeight.Bold,
                            fontSize = 11.sp,
                            color = Color(0xFF1E88E5)
                        )
                    }
                }

                // Hydro progress visual ring
                val hydroProgress = if (targetWater > 0) (waterAmount.toFloat() / targetWater.toFloat()).coerceIn(0f, 1f) else 0f
                LinearProgressIndicator(
                    progress = hydroProgress,
                    color = Color(0xFF1E88E5),
                    trackColor = Color(0xFFE3F2FD),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(8.dp)
                        .clip(RoundedCornerShape(4.dp))
                )

                // Fast cup incrementing options row
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Button(
                        onClick = { viewModel.addWater(250) },
                        modifier = Modifier.weight(1f).testTag("add_water_250_btn"),
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF1E88E5)),
                        shape = RoundedCornerShape(10.dp)
                    ) {
                        Text("+250 mL", fontSize = 11.sp, color = Color.White)
                    }

                    Button(
                        onClick = { viewModel.addWater(500) },
                        modifier = Modifier.weight(1f).testTag("add_water_500_btn"),
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF1565C0)),
                        shape = RoundedCornerShape(10.dp)
                    ) {
                        Text("+500 mL", fontSize = 11.sp, color = Color.White)
                    }

                    OutlinedButton(
                        onClick = { viewModel.addWater(-250) },
                        modifier = Modifier.weight(1f).testTag("sub_water_250_btn"),
                        shape = RoundedCornerShape(10.dp),
                        border = BorderStroke(1.dp, Color(0xFF1E88E5))
                    ) {
                        Text("-250 mL", fontSize = 11.sp, color = Color(0xFF1E88E5))
                    }
                }
            }
        }

        // WEIGHT TRACKER CARD WITH INTERACTIVE LOCAL CANVAS GRAPH VISUALS!
        Card(
            shape = RoundedCornerShape(24.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            border = BorderStroke(1.dp, Color(0xFFECEFF1)),
            modifier = Modifier
                .fillMaxWidth()
                .testTag("weight_tracker_card")
        ) {
            Column(
                modifier = Modifier.padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
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
                                .size(36.dp)
                                .background(Color(0xFFFFEBEE), CircleShape),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.TrendingUp,
                                contentDescription = "Weight Tracker",
                                tint = Color(0xFFE53935),
                                modifier = Modifier.size(18.dp)
                            )
                        }
                        Text(
                            text = if (isBengali) "ওজন ট্র্যাকিং রেকর্ড" else "Metabolic Weight Logger",
                            fontWeight = FontWeight.Bold,
                            fontSize = 15.sp,
                            color = Color(0xFFE53935)
                        )
                    }

                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(8.dp))
                            .background(Color(0xFFFFEBEE))
                            .padding(horizontal = 8.dp, vertical = 4.dp)
                    ) {
                        Text(
                            text = "Current: ${userProfile.weight} kg",
                            fontWeight = FontWeight.Bold,
                            fontSize = 11.sp,
                            color = Color(0xFFE53935)
                        )
                    }
                }

                // Interactive Canvas Graph visual
                Text(
                    text = if (isBengali) "ওজন পরিবর্তনের চিত্রলেখ (Metabolic Cycles):" else "Metabolic Weight Trend Chart:",
                    fontWeight = FontWeight.Bold,
                    fontSize = 12.sp,
                    color = Color.Gray
                )

                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(130.dp)
                        .background(Color(0xFFFAFAFA), RoundedCornerShape(12.dp))
                        .padding(10.dp)
                ) {
                    Canvas(modifier = Modifier.fillMaxSize()) {
                        val strokeColor = Color(0xFFE53935)
                        val gridColor = Color(0xFFEEEEEE)
                        
                        // Draw grid lines
                        val verticalSegments = 5
                        for (i in 0..verticalSegments) {
                            val y = size.height * (i.toFloat() / verticalSegments)
                            drawLine(
                                color = gridColor,
                                start = Offset(0f, y),
                                end = Offset(size.width, y),
                                strokeWidth = 1.dp.toPx()
                            )
                        }

                        // Plot weight logs points
                        if (weightLogs.size >= 2) {
                            val sortedLogs = weightLogs.sortedBy { it.date }
                            val minW = sortedLogs.minOf { it.weight } - 2.0
                            val maxW = sortedLogs.maxOf { it.weight } + 2.0
                            val range = if (maxW > minW) maxW - minW else 1.0

                            val points = sortedLogs.mapIndexed { idx, log ->
                                val x = size.width * (idx.toFloat() / (sortedLogs.size - 1))
                                val y = size.height * (1.0f - ((log.weight - minW) / range).toFloat())
                                Offset(x, y)
                            }

                            // Draw continuous curve
                            for (i in 0 until points.size - 1) {
                                drawLine(
                                    color = strokeColor,
                                    start = points[i],
                                    end = points[i + 1],
                                    strokeWidth = 3.dp.toPx(),
                                    cap = StrokeCap.Round
                                )
                            }

                            // Draw point circles
                            points.forEach { pt ->
                                drawCircle(
                                    color = Color.White,
                                    radius = 5.dp.toPx(),
                                    center = pt
                                )
                                drawCircle(
                                    color = strokeColor,
                                    radius = 3.dp.toPx(),
                                    center = pt
                                )
                            }
                        } else {
                            // Empty data hint text representation
                            drawCircle(
                                color = Color.LightGray.copy(alpha = 0.4f),
                                radius = 24.dp.toPx(),
                                center = Offset(size.width / 2, size.height / 2)
                            )
                        }
                    }
                }

                // Add Weight input field
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    OutlinedTextField(
                        value = weightInputText,
                        onValueChange = { weightInputText = it },
                        label = { Text(if (isBengali) "নতুন সংগৃহীত ওজন" else "Add Weight (kg)", fontSize = 11.sp) },
                        placeholder = { Text("e.g., 68.2", fontSize = 11.sp) },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        singleLine = true,
                        modifier = Modifier
                            .weight(1.5f)
                            .testTag("weight_log_input"),
                        shape = RoundedCornerShape(12.dp)
                    )

                    Button(
                        onClick = {
                            val w = weightInputText.toDoubleOrNull()
                            if (w != null && w > 0) {
                                viewModel.logWeight(w, selectedDate)
                                weightInputText = ""
                            }
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFE53935)),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier
                            .weight(1f)
                            .height(48.dp)
                            .testTag("submit_weight_btn")
                    ) {
                        Text(if (isBengali) "যুক্ত করুন" else "Log", fontWeight = FontWeight.Bold, color = Color.White)
                    }
                }

                // Scrollable List of past logs inside Weight tab
                if (weightLogs.isNotEmpty()) {
                    Column(
                        modifier = Modifier.fillMaxWidth(),
                        verticalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Text(
                            text = if (isBengali) "ওজন পরিবর্তনের ইতিহাস" else "Weight Logging Logs:",
                            fontWeight = FontWeight.Bold,
                            fontSize = 12.sp,
                            color = Color.Gray
                        )

                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .background(Color(0xFFFAFAFA), RoundedCornerShape(12.dp))
                                .padding(8.dp),
                            verticalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            weightLogs.forEach { log ->
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(horizontal = 6.dp),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(
                                        text = "📅 ${log.date}",
                                        fontSize = 12.sp,
                                        fontWeight = FontWeight.Medium
                                    )

                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Text(
                                            text = "${log.weight} kg",
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 12.sp,
                                            color = Color(0xFFE53935)
                                        )

                                        IconButton(
                                            onClick = { viewModel.deleteWeight(log.date) },
                                            modifier = Modifier.size(36.dp).testTag("delete_weight_${log.date}")
                                        ) {
                                            Icon(
                                                imageVector = Icons.Default.Delete,
                                                contentDescription = "Delete weight",
                                                tint = Color.LightGray,
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
        }
    }
}

// ==========================================
// TAB 4: PROFILE MANAGE / EDIT TAB
// ==========================================
@Composable
fun ProfileEditTab(
    userProfile: UserProfileEntity,
    isBengali: Boolean,
    onNavigateToHealthPrefs: () -> Unit,
    onSave: (Int, String, Double, Double, String, String, String, String, String) -> Unit
) {
    var ageText by remember { mutableStateOf(userProfile.age.toString()) }
    var weightText by remember { mutableStateOf(userProfile.weight.toString()) }
    var heightText by remember { mutableStateOf(userProfile.height.toString()) }
    var selectedGender by remember { mutableStateOf(userProfile.gender) }
    var selectedGoal by remember { mutableStateOf(userProfile.goal) }
    var selectedPreference by remember { mutableStateOf(userProfile.dietaryPreference) }
    var allergiesInput by remember { mutableStateOf(userProfile.allergies) }
    var medicalInput by remember { mutableStateOf(userProfile.medicalConditions) }
    var cuisineInput by remember { mutableStateOf(userProfile.cuisinePreferences) }

    val genders = listOf("Male", "Female", "Other")
    val goals = listOf("Weight Loss", "Maintain Weight", "Weight Gain")
    val preferences = listOf("Vegetarian", "Non-Vegetarian", "Vegan", "Keto")

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Welcome and App Identity Banner
        Card(
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(containerColor = Color(0xFFE8F5E9)),
            modifier = Modifier.fillMaxWidth()
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(18.dp),
                horizontalArrangement = Arrangement.spacedBy(14.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text("👤", fontSize = 42.sp)

                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = if (isBengali) "আমার স্বাস্থ্য প্রোফাইল" else "User Biological Profile",
                        fontWeight = FontWeight.Bold,
                        fontSize = 15.sp,
                        color = Color(0xFF1E5E2F)
                    )
                    Text(
                        text = if (isBengali)
                            "এখানে আপনার লক্ষ্য ও পরিমাপ পরিবর্তন করুন। প্রতিটি তথ্যের ভিত্তিতে ডায়েট ক্যালরি পরিবর্তিত হয়।"
                        else
                            "Edit baseline metrics. Adjusting active weight values changes Harris-Benedict formulas instantly.",
                        fontSize = 11.sp,
                        color = Color.DarkGray,
                        lineHeight = 15.sp
                    )
                }
            }
        }

        // Action trigger to Health Preferential Settings
        Button(
            onClick = onNavigateToHealthPrefs,
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF1B5E20)),
            shape = RoundedCornerShape(12.dp),
            modifier = Modifier
                .fillMaxWidth()
                .height(44.dp)
                .testTag("health_prefs_screen_button")
        ) {
            Icon(imageVector = Icons.Default.Healing, contentDescription = null, modifier = Modifier.size(18.dp))
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = if (isBengali) "মেডিকেল কন্ডিশন ও রান্নার ধরণ সমন্বয় করুন" else "Customize Health Needs & Flavors",
                fontWeight = FontWeight.Bold,
                fontSize = 13.sp,
                color = Color.White
            )
        }

        Card(
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            border = BorderStroke(1.dp, Color(0xFFECEFF1)),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(
                modifier = Modifier.padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                // Age Input
                OutlinedTextField(
                    value = ageText,
                    onValueChange = { ageText = it },
                    label = { Text(if (isBengali) "বয়স (Age)" else "Age (Years)") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    modifier = Modifier.fillMaxWidth().testTag("profile_age_input"),
                    shape = RoundedCornerShape(12.dp)
                )

                // Height and Weight Row
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    OutlinedTextField(
                        value = heightText,
                        onValueChange = { heightText = it },
                        label = { Text(if (isBengali) "উচ্চতা (সেমি)" else "Height (cm)") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                        modifier = Modifier.weight(1f).testTag("profile_height_input"),
                        shape = RoundedCornerShape(12.dp)
                    )

                    OutlinedTextField(
                        value = weightText,
                        onValueChange = { weightText = it },
                        label = { Text(if (isBengali) "ওজন (কেজি)" else "Weight (kg)") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                        modifier = Modifier.weight(1f).testTag("profile_weight_input"),
                        shape = RoundedCornerShape(12.dp)
                    )
                }

                // Gender Options Dropdown Selector Replacement
                Column {
                    Text(text = if (isBengali) "লিঙ্গ বাছুন (Gender):" else "Gender:", fontSize = 12.sp, color = Color.Gray)
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        genders.forEach { gender ->
                            val isSel = selectedGender == gender
                            Box(
                                modifier = Modifier
                                    .weight(1f)
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(if (isSel) Color(0xFF2E7D32) else Color(0xFFF1F8E9))
                                    .border(1.dp, if (isSel) Color(0xFF1B5E20) else Color(0xFFC8E6C9), RoundedCornerShape(8.dp))
                                    .clickable { selectedGender = gender }
                                    .padding(vertical = 10.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = gender,
                                    fontWeight = FontWeight.Bold,
                                    color = if (isSel) Color.White else Color(0xFF2E7D32),
                                    fontSize = 12.sp
                                )
                            }
                        }
                    }
                }

                // Goal Options Selection
                Column {
                    Text(text = if (isBengali) "আপনার লক্ষ্য (Fitness Goal):" else "Target Fitness Goal:", fontSize = 12.sp, color = Color.Gray)
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        goals.forEach { goal ->
                            val isSel = selectedGoal == goal
                            Box(
                                modifier = Modifier
                                    .weight(1f)
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(if (isSel) Color(0xFF2E7D32) else Color(0xFFF1F8E9))
                                    .border(1.dp, if (isSel) Color(0xFF1B5E20) else Color(0xFFC8E6C9), RoundedCornerShape(8.dp))
                                    .clickable { selectedGoal = goal }
                                    .padding(vertical = 10.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = goal,
                                    fontWeight = FontWeight.Bold,
                                    color = if (isSel) Color.White else Color(0xFF2E7D32),
                                    fontSize = 11.sp,
                                    textAlign = TextAlign.Center
                                )
                            }
                        }
                    }
                }

                // Preference Selection
                Column {
                    Text(text = if (isBengali) "খাবারের ধরণ (Diet Preference):" else "Dietary Preference:", fontSize = 12.sp, color = Color.Gray)
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        preferences.forEach { pref ->
                            val isSel = selectedPreference == pref
                            Box(
                                modifier = Modifier
                                    .weight(1f)
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(if (isSel) Color(0xFF2E7D32) else Color(0xFFF1F8E9))
                                    .border(1.dp, if (isSel) Color(0xFF1B5E20) else Color(0xFFC8E6C9), RoundedCornerShape(8.dp))
                                    .clickable { selectedPreference = pref }
                                    .padding(vertical = 8.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = pref,
                                    fontWeight = FontWeight.Bold,
                                    color = if (isSel) Color.White else Color(0xFF2E7D32),
                                    fontSize = 10.5.sp
                                )
                            }
                        }
                    }
                }

                // Allergies text field
                OutlinedTextField(
                    value = allergiesInput,
                    onValueChange = { allergiesInput = it },
                    label = { Text(if (isBengali) "অ্যালার্জিসমূহ (Allergies)" else "Allergies / Restrictions") },
                    placeholder = { Text("e.g., Peanuts, Shrimp, Milk") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth().testTag("profile_allergies_input"),
                    shape = RoundedCornerShape(12.dp)
                )
            }
        }

        Button(
            onClick = {
                val age = ageText.toIntOrNull() ?: 25
                val w = weightText.toDoubleOrNull() ?: 70.0
                val h = heightText.toDoubleOrNull() ?: 175.0
                onSave(age, selectedGender, w, h, selectedGoal, selectedPreference, allergiesInput.trim(), medicalInput, cuisineInput)
            },
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF2E7D32)),
            shape = RoundedCornerShape(14.dp),
            modifier = Modifier
                .fillMaxWidth()
                .height(48.dp)
                .testTag("save_profile_button")
        ) {
            Text(
                text = if (isBengali) "প্রোফাইল আপডেট করুন" else "Persist Updates",
                fontWeight = FontWeight.Bold,
                fontSize = 14.sp,
                color = Color.White
            )
        }
    }
}

// ==========================================
// UTILITY FUNCTIONS DEFINITIONS
// ==========================================

fun adjustDateString(currentDate: String, daysOffset: Int): String {
    val format = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
    return try {
        val date = format.parse(currentDate) ?: Date()
        val calendar = Calendar.getInstance().apply {
            time = date
            add(Calendar.DATE, daysOffset)
        }
        format.format(calendar.time)
    } catch (e: Exception) {
        currentDate
    }
}
