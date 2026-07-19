package com.example.ui

import android.content.Context
import android.widget.Toast
import androidx.compose.animation.*
import androidx.compose.foundation.*
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
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.viewmodel.DietPlannerViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.util.*

// Dynamic roles configuration
data class AdminUser(
    val id: String,
    val name: String,
    val email: String,
    val role: String,
    val isPremium: Boolean,
    val joinedDate: String,
    val plan: String = "None"
)

data class CustomFoodItem(
    val id: String,
    val name: String,
    val nameBn: String,
    val calories: Int,
    val protein: Double,
    val carbs: Double,
    val fat: Double
)

data class CustomRecipeItem(
    val id: String,
    val title: String,
    val titleBn: String,
    val calories: Int,
    val prepTime: String,
    val videoUrl: String,
    val ingredients: String
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdminSecurityPremiumHub(
    viewModel: DietPlannerViewModel,
    isBengali: Boolean,
    onBack: () -> Unit
) {
    val context = LocalContext.current
    var selectedMainTab by remember { mutableStateOf(0) } // 0: Admin Panel, 1: Security Suite, 2: Premium & Billing
    
    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text(
                            text = if (isBengali) "নিলজরি কন্ট্রোল সেন্টার" else "Niljori Control Center",
                            fontWeight = FontWeight.Black,
                            fontSize = 18.sp
                        )
                        Text(
                            text = if (isBengali) "অ্যাডমিন প্যানেল, নিরাপত্তা ও প্রিমিয়াম বিলিং" else "Admin Panel, Security Suite & Premium Billing Simulator",
                            fontSize = 11.sp,
                            color = MaterialTheme.colorScheme.primary
                        )
                    }
                },
                navigationIcon = {
                    IconButton(onClick = onBack, modifier = Modifier.testTag("hub_back_btn")) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface,
                    titleContentColor = MaterialTheme.colorScheme.onSurface
                )
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .background(MaterialTheme.colorScheme.background)
        ) {
            // Main Navigation Tabs
            TabRow(
                selectedTabIndex = selectedMainTab,
                containerColor = MaterialTheme.colorScheme.surface,
                contentColor = MaterialTheme.colorScheme.primary
            ) {
                Tab(
                    selected = selectedMainTab == 0,
                    onClick = { selectedMainTab = 0 },
                    text = { Text(if (isBengali) "অ্যাডমিন প্যানেল" else "🛠️ Admin", fontSize = 12.sp, fontWeight = FontWeight.Bold) }
                )
                Tab(
                    selected = selectedMainTab == 1,
                    onClick = { selectedMainTab = 1 },
                    text = { Text(if (isBengali) "নিরাপত্তা স্যুট" else "🛡️ Security", fontSize = 12.sp, fontWeight = FontWeight.Bold) }
                )
                Tab(
                    selected = selectedMainTab == 2,
                    onClick = { selectedMainTab = 2 },
                    text = { Text(if (isBengali) "প্রিমিয়াম বিলিং" else "💎 Billing", fontSize = 12.sp, fontWeight = FontWeight.Bold) }
                )
            }

            Box(modifier = Modifier.weight(1f)) {
                when (selectedMainTab) {
                    0 -> AdminPanelView(isBengali = isBengali)
                    1 -> SecuritySuiteView(isBengali = isBengali)
                    2 -> PremiumBillingView(isBengali = isBengali, viewModel = viewModel)
                }
            }
        }
    }
}

// =========================================================================
// VIEW 1: ADMIN PANEL
// =========================================================================
@Composable
fun AdminPanelView(isBengali: Boolean) {
    var adminSubSection by remember { mutableStateOf(0) } // 0: Dashboard, 1: Users, 2: Foods & Recipes, 3: Comms & Ads, 4: Coupons, 5: Roles & CMS

    Column(modifier = Modifier.fillMaxSize()) {
        // Horizontal Scrollable Sub Bar
        ScrollableTabRow(
            selectedTabIndex = adminSubSection,
            edgePadding = 8.dp,
            containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f),
            divider = {}
        ) {
            val tabs = listOf(
                "📊 Dashboard",
                "👥 Users",
                "🥗 Food/Recipe DB",
                "📣 Alerts/Ads",
                "🎫 Coupon/Promo",
                "⚙️ Roles & CMS"
            )
            tabs.forEachIndexed { index, title ->
                Tab(
                    selected = adminSubSection == index,
                    onClick = { adminSubSection = index },
                    text = { Text(title, fontSize = 11.sp, fontWeight = FontWeight.SemiBold) }
                )
            }
        }

        Box(modifier = Modifier.weight(1f)) {
            when (adminSubSection) {
                0 -> AdminDashboardSubView(isBengali)
                1 -> AdminUsersSubView(isBengali)
                2 -> AdminFoodRecipeSubView(isBengali)
                3 -> AdminAlertsAdsSubView(isBengali)
                4 -> AdminCouponsSubView(isBengali)
                5 -> AdminCMSAndRolesSubView(isBengali)
            }
        }
    }
}

@Composable
fun AdminDashboardSubView(isBengali: Boolean) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Text(
            text = "System Summary KPIs",
            fontWeight = FontWeight.Bold,
            fontSize = 14.sp,
            color = MaterialTheme.colorScheme.primary
        )

        // KPI Row 1
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
            KPICard(
                title = "Total Active Users",
                value = "12,540",
                trend = "▲ 14% this mo",
                color = Color(0xFF0288D1),
                modifier = Modifier.weight(1f)
            )
            KPICard(
                title = "Monthly Revenue",
                value = "$45,280",
                trend = "▲ 22% this mo",
                color = Color(0xFF2E7D32),
                modifier = Modifier.weight(1f)
            )
        }

        // KPI Row 2
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
            KPICard(
                title = "Active Subscriptions",
                value = "3,420",
                trend = "▲ 8% churn drop",
                color = Color(0xFF8E24AA),
                modifier = Modifier.weight(1f)
            )
            KPICard(
                title = "System API Health",
                value = "24% CPU",
                trend = "100% Uptime",
                color = Color(0xFFE65100),
                modifier = Modifier.weight(1f)
            )
        }

        // System Health Indicator Details Card
        Card(
            shape = RoundedCornerShape(12.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f))
        ) {
            Column(modifier = Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                Text("System Diagnostics", fontWeight = FontWeight.Bold, fontSize = 12.sp)
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                    Text("API Requests (Today)", fontSize = 11.sp, color = Color.Gray)
                    Text("15,200 / 50,000", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                }
                LinearProgressIndicator(
                    progress = 0.30f,
                    modifier = Modifier.fillMaxWidth().height(6.dp).clip(RoundedCornerShape(3.dp)),
                    color = MaterialTheme.colorScheme.primary
                )
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                    Text("Database Integrity Logs", fontSize = 11.sp, color = Color.Gray)
                    Text("PASS (Zero Anomalies)", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = Color(0xFF2E7D32))
                }
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                    Text("Active Security Alerts", fontSize = 11.sp, color = Color.Gray)
                    Text("0 Incidents Reported", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = Color(0xFF2E7D32))
                }
            }
        }

        // Revenue Growth Chart Simulator
        Card(
            shape = RoundedCornerShape(12.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant)
        ) {
            Column(modifier = Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                Text("Subscription Distribution Trend (Deshi Markets)", fontWeight = FontWeight.Bold, fontSize = 12.sp)
                
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(80.dp)
                        .padding(vertical = 4.dp),
                    horizontalArrangement = Arrangement.SpaceAround,
                    verticalAlignment = Alignment.Bottom
                ) {
                    val bars = listOf(
                        Pair("Jan", 30),
                        Pair("Feb", 45),
                        Pair("Mar", 60),
                        Pair("Apr", 55),
                        Pair("May", 80),
                        Pair("Jun", 95)
                    )
                    bars.forEach { (month, heightPct) ->
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Box(
                                modifier = Modifier
                                    .width(22.dp)
                                    .fillMaxHeight(heightPct / 100f)
                                    .clip(RoundedCornerShape(topStart = 4.dp, topEnd = 4.dp))
                                    .background(MaterialTheme.colorScheme.primary)
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(month, fontSize = 9.sp, color = Color.Gray)
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun KPICard(title: String, value: String, trend: String, color: Color, modifier: Modifier = Modifier) {
    Card(
        modifier = modifier,
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = color.copy(alpha = 0.08f)),
        border = BorderStroke(1.dp, color.copy(alpha = 0.3f))
    ) {
        Column(modifier = Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(4.dp)) {
            Text(title, fontSize = 11.sp, fontWeight = FontWeight.SemiBold, color = Color.Gray)
            Text(value, fontSize = 20.sp, fontWeight = FontWeight.Black, color = color)
            Text(trend, fontSize = 10.sp, fontWeight = FontWeight.Bold, color = color)
        }
    }
}

@Composable
fun AdminUsersSubView(isBengali: Boolean) {
    var searchQuery by remember { mutableStateOf("") }
    var filterPremiumOnly by remember { mutableStateOf(false) }
    
    // Core Admin State Database
    var usersList by remember {
        mutableStateOf(
            listOf(
                AdminUser("1", "Imtiaz Sharif", "imtiaz@niljori.com", "Super Admin", true, "12-05-2025", "Yearly Plan"),
                AdminUser("2", "Anika Rahman", "anika.r@gmail.com", "Standard User", false, "15-06-2025"),
                AdminUser("3", "Dr. Safayet Hossain", "safayet@health.bd", "Medical Editor", true, "01-02-2025", "Monthly Plan"),
                AdminUser("4", "Kazi Tasnim", "kazi@yahoo.com", "Standard User", true, "10-07-2025", "7-Day Free Trial"),
                AdminUser("5", "Sajib Ahmed", "sajib@outlook.com", "Support Agent", false, "22-07-2025")
            )
        )
    }

    var selectedUserDetails by remember { mutableStateOf<AdminUser?>(null) }

    val filteredUsers = usersList.filter {
        (it.name.contains(searchQuery, true) || it.email.contains(searchQuery, true) || it.role.contains(searchQuery, true)) &&
        (!filterPremiumOnly || it.isPremium)
    }

    Column(modifier = Modifier.fillMaxSize().padding(16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
        OutlinedTextField(
            value = searchQuery,
            onValueChange = { searchQuery = it },
            placeholder = { Text("Search users by name, email, role...", fontSize = 12.sp) },
            leadingIcon = { Icon(Icons.Default.Search, contentDescription = null, modifier = Modifier.size(18.dp)) },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true,
            shape = RoundedCornerShape(10.dp)
        )

        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.clickable { filterPremiumOnly = !filterPremiumOnly }) {
                Checkbox(checked = filterPremiumOnly, onCheckedChange = { filterPremiumOnly = it }, modifier = Modifier.scale(0.85f))
                Text("Show Premium Members Only", fontSize = 11.sp, fontWeight = FontWeight.Bold)
            }
            Text("${filteredUsers.size} Users Found", fontSize = 11.sp, color = Color.Gray)
        }

        LazyColumn(
            verticalArrangement = Arrangement.spacedBy(8.dp),
            modifier = Modifier.weight(1f)
        ) {
            items(filteredUsers) { user ->
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(10.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant)
                ) {
                    Row(
                        modifier = Modifier.padding(12.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                                Text(user.name, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                                if (user.isPremium) {
                                    Box(
                                        modifier = Modifier
                                            .background(Color(0xFFFFB300), RoundedCornerShape(4.dp))
                                            .padding(horizontal = 4.dp, vertical = 1.dp)
                                    ) {
                                        Text("PREMIUM", fontSize = 7.sp, fontWeight = FontWeight.Black, color = Color.White)
                                    }
                                }
                            }
                            Text(user.email, fontSize = 11.sp, color = Color.Gray)
                            Text("Role: ${user.role} | Joined: ${user.joinedDate}", fontSize = 10.sp, color = Color.DarkGray)
                            if (user.isPremium) {
                                Text("Active Tier: ${user.plan}", fontSize = 9.sp, fontWeight = FontWeight.Bold, color = Color(0xFF673AB7))
                            }
                        }

                        Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                            IconButton(onClick = { selectedUserDetails = user }, modifier = Modifier.size(30.dp)) {
                                Icon(Icons.Default.Info, contentDescription = "Details", tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(18.dp))
                            }
                            IconButton(
                                onClick = {
                                    usersList = usersList.filter { it.id != user.id }
                                },
                                modifier = Modifier.size(30.dp)
                            ) {
                                Icon(Icons.Default.Delete, contentDescription = "Delete", tint = Color.Red, modifier = Modifier.size(18.dp))
                            }
                        }
                    }
                }
            }
        }
    }

    if (selectedUserDetails != null) {
        val user = selectedUserDetails!!
        AlertDialog(
            onDismissRequest = { selectedUserDetails = null },
            title = { Text("Manage Role & Premium Status", fontSize = 14.sp, fontWeight = FontWeight.Bold) },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    Text("User: ${user.name}", fontWeight = FontWeight.Bold, fontSize = 12.sp)
                    Text("Email: ${user.email}", fontSize = 11.sp)
                    Text("Joined: ${user.joinedDate}", fontSize = 11.sp)
                    
                    Text("Modify User Role Privilege:", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                    Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                        listOf("Standard User", "Support Agent", "Medical Editor", "Super Admin").forEach { role ->
                            FilterChip(
                                selected = user.role == role,
                                onClick = {
                                    usersList = usersList.map {
                                        if (it.id == user.id) it.copy(role = role) else it
                                    }
                                    selectedUserDetails = usersList.find { it.id == user.id }
                                },
                                label = { Text(role, fontSize = 9.sp) }
                            )
                        }
                    }

                    Text("Toggle Subscription State:", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        Button(
                            onClick = {
                                usersList = usersList.map {
                                    if (it.id == user.id) it.copy(isPremium = true, plan = "Yearly Plan") else it
                                }
                                selectedUserDetails = usersList.find { it.id == user.id }
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF2E7D32)),
                            contentPadding = PaddingValues(horizontal = 8.dp, vertical = 2.dp),
                            modifier = Modifier.height(28.dp)
                        ) {
                            Text("Grant Premium", fontSize = 9.sp, color = Color.White)
                        }
                        Button(
                            onClick = {
                                usersList = usersList.map {
                                    if (it.id == user.id) it.copy(isPremium = false, plan = "None") else it
                                }
                                selectedUserDetails = usersList.find { it.id == user.id }
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = Color.Red),
                            contentPadding = PaddingValues(horizontal = 8.dp, vertical = 2.dp),
                            modifier = Modifier.height(28.dp)
                        ) {
                            Text("Revoke Premium", fontSize = 9.sp, color = Color.White)
                        }
                    }
                }
            },
            confirmButton = {
                TextButton(onClick = { selectedUserDetails = null }) {
                    Text("Done")
                }
            }
        )
    }
}

@Composable
fun AdminFoodRecipeSubView(isBengali: Boolean) {
    var foodSearchQuery by remember { mutableStateOf("") }
    var activeSubTab by remember { mutableStateOf(0) } // 0: Food DB, 1: Recipe Manager

    // Mock Databases
    var foodList by remember {
        mutableStateOf(
            listOf(
                CustomFoodItem("1", "Deshi Rice (Basmati)", "দেশী বাসমতি চালের ভাত", 130, 2.7, 28.0, 0.3),
                CustomFoodItem("2", "Egg (Boiled)", "সেদ্ধ ডিম", 155, 13.0, 1.1, 11.0),
                CustomFoodItem("3", "Hilsha Fish Fry", "ইলিশ মাছ ভাজা", 262, 22.0, 1.5, 18.0),
                CustomFoodItem("4", "Red Lentil Soup (Dal)", "মসুর ডাল", 116, 9.0, 20.0, 0.4),
                CustomFoodItem("5", "Chicken Breast Curry", "মুরগির বুকের মাংসের কারি", 165, 31.0, 3.5, 3.6)
            )
        )
    }

    var recipeList by remember {
        mutableStateOf(
            listOf(
                CustomRecipeItem("1", "Avocado Greek Salad", "অ্যাভোকাডো গ্রিক সালাদ", 280, "15 min", "https://youtube.com/mock-avocado", "Avocado, Tomatoes, Cucumber, Olive Oil"),
                CustomRecipeItem("2", "Keto Mustard Hilsha", "ক্যাটো সরিষা ইলিশ", 340, "25 min", "https://youtube.com/mock-hilsha", "Hilsha fish, Mustard paste, Green chillies, Mustard oil"),
                CustomRecipeItem("3", "High Protein Oats Porridge", "ওটস খিচুড়ি", 220, "10 min", "https://youtube.com/mock-oats", "Oats, Moong dal, Turmeric, Mixed vegetables")
            )
        )
    }

    var showAddFoodForm by remember { mutableStateOf(false) }
    var showAddRecipeForm by remember { mutableStateOf(false) }

    Column(modifier = Modifier.fillMaxSize().padding(16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
        TabRow(selectedTabIndex = activeSubTab, containerColor = Color.Transparent) {
            Tab(selected = activeSubTab == 0, onClick = { activeSubTab = 0 }) {
                Text("Deshi Food Database", fontSize = 12.sp, fontWeight = FontWeight.Bold, modifier = Modifier.padding(bottom = 6.dp))
            }
            Tab(selected = activeSubTab == 1, onClick = { activeSubTab = 1 }) {
                Text("Recipes Manager", fontSize = 12.sp, fontWeight = FontWeight.Bold, modifier = Modifier.padding(bottom = 6.dp))
            }
        }

        if (activeSubTab == 0) {
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                OutlinedTextField(
                    value = foodSearchQuery,
                    onValueChange = { foodSearchQuery = it },
                    placeholder = { Text("Search ingredients...", fontSize = 11.sp) },
                    modifier = Modifier.weight(1f).height(46.dp),
                    singleLine = true,
                    shape = RoundedCornerShape(8.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Button(
                    onClick = { showAddFoodForm = true },
                    shape = RoundedCornerShape(8.dp),
                    contentPadding = PaddingValues(horizontal = 8.dp),
                    modifier = Modifier.height(36.dp)
                ) {
                    Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(16.dp))
                    Text("Add Food", fontSize = 11.sp)
                }
            }

            LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp), modifier = Modifier.weight(1f)) {
                items(foodList.filter { it.name.contains(foodSearchQuery, true) || it.nameBn.contains(foodSearchQuery) }) { food ->
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant)
                    ) {
                        Row(modifier = Modifier.padding(10.dp), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text(food.name, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                                Text(food.nameBn, fontSize = 11.sp, color = Color.Gray)
                                Row(horizontalArrangement = Arrangement.spacedBy(8.dp), modifier = Modifier.padding(top = 4.dp)) {
                                    Text("Cal: ${food.calories}kcal", fontSize = 10.sp, fontWeight = FontWeight.Bold)
                                    Text("P: ${food.protein}g", fontSize = 10.sp, color = Color(0xFF2E7D32))
                                    Text("C: ${food.carbs}g", fontSize = 10.sp, color = Color(0xFF0288D1))
                                    Text("F: ${food.fat}g", fontSize = 10.sp, color = Color(0xFFFF9800))
                                }
                            }
                            IconButton(onClick = { foodList = foodList.filter { it.id != food.id } }) {
                                Icon(Icons.Default.Delete, contentDescription = "Delete", tint = Color.Red, modifier = Modifier.size(18.dp))
                            }
                        }
                    }
                }
            }
        } else {
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                Text("Active Diet Recipes Database", fontWeight = FontWeight.Bold, fontSize = 12.sp)
                Button(
                    onClick = { showAddRecipeForm = true },
                    shape = RoundedCornerShape(8.dp),
                    contentPadding = PaddingValues(horizontal = 8.dp),
                    modifier = Modifier.height(36.dp)
                ) {
                    Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(16.dp))
                    Text("Create Recipe", fontSize = 11.sp)
                }
            }

            LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp), modifier = Modifier.weight(1f)) {
                items(recipeList) { recipe ->
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant)
                    ) {
                        Column(modifier = Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                                Column {
                                    Text(recipe.title, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                                    Text(recipe.titleBn, fontSize = 11.sp, color = Color.Gray)
                                }
                                IconButton(onClick = { recipeList = recipeList.filter { it.id != recipe.id } }, modifier = Modifier.size(24.dp)) {
                                    Icon(Icons.Default.Delete, contentDescription = "Delete", tint = Color.Red, modifier = Modifier.size(18.dp))
                                }
                            }
                            Text("Ingredients: ${recipe.ingredients}", fontSize = 10.sp, maxLines = 1, overflow = TextOverflow.Ellipsis)
                            Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                                Text("Calories: ${recipe.calories} kcal", fontSize = 10.sp, fontWeight = FontWeight.Bold)
                                Text("Prep Time: ${recipe.prepTime}", fontSize = 10.sp, color = Color.Gray)
                                Text("Video: YouTube ✅", fontSize = 10.sp, color = Color(0xFFC62828), fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                }
            }
        }
    }

    // Dynamic Form Dialogues
    if (showAddFoodForm) {
        var nName by remember { mutableStateOf("") }
        var nNameBn by remember { mutableStateOf("") }
        var nCals by remember { mutableStateOf("") }
        var nProt by remember { mutableStateOf("") }
        var nCarbs by remember { mutableStateOf("") }
        var nFat by remember { mutableStateOf("") }

        AlertDialog(
            onDismissRequest = { showAddFoodForm = false },
            title = { Text("Add Custom Food Ingredient", fontSize = 14.sp, fontWeight = FontWeight.Bold) },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedTextField(value = nName, onValueChange = { nName = it }, label = { Text("Name (English)", fontSize = 10.sp) }, singleLine = true)
                    OutlinedTextField(value = nNameBn, onValueChange = { nNameBn = it }, label = { Text("Name (Bengali)", fontSize = 10.sp) }, singleLine = true)
                    Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                        OutlinedTextField(value = nCals, onValueChange = { nCals = it }, label = { Text("Cals", fontSize = 10.sp) }, modifier = Modifier.weight(1f))
                        OutlinedTextField(value = nProt, onValueChange = { nProt = it }, label = { Text("Protein (g)", fontSize = 10.sp) }, modifier = Modifier.weight(1f))
                    }
                    Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                        OutlinedTextField(value = nCarbs, onValueChange = { nCarbs = it }, label = { Text("Carbs (g)", fontSize = 10.sp) }, modifier = Modifier.weight(1f))
                        OutlinedTextField(value = nFat, onValueChange = { nFat = it }, label = { Text("Fat (g)", fontSize = 10.sp) }, modifier = Modifier.weight(1f))
                    }
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        if (nName.isNotEmpty() && nNameBn.isNotEmpty()) {
                            foodList = foodList + CustomFoodItem(
                                UUID.randomUUID().toString(),
                                nName,
                                nNameBn,
                                nCals.toIntOrNull() ?: 0,
                                nProt.toDoubleOrNull() ?: 0.0,
                                nCarbs.toDoubleOrNull() ?: 0.0,
                                nFat.toDoubleOrNull() ?: 0.0
                            )
                        }
                        showAddFoodForm = false
                    }
                ) {
                    Text("Add Ingredient")
                }
            },
            dismissButton = { TextButton(onClick = { showAddFoodForm = false }) { Text("Cancel") } }
        )
    }

    if (showAddRecipeForm) {
        var rTitle by remember { mutableStateOf("") }
        var rTitleBn by remember { mutableStateOf("") }
        var rCals by remember { mutableStateOf("") }
        var rPrep by remember { mutableStateOf("") }
        var rIngredients by remember { mutableStateOf("") }
        var rVideo by remember { mutableStateOf("") }

        AlertDialog(
            onDismissRequest = { showAddRecipeForm = false },
            title = { Text("Create New Recipe Card", fontSize = 14.sp, fontWeight = FontWeight.Bold) },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedTextField(value = rTitle, onValueChange = { rTitle = it }, label = { Text("Recipe Title", fontSize = 10.sp) }, singleLine = true)
                    OutlinedTextField(value = rTitleBn, onValueChange = { rTitleBn = it }, label = { Text("Title (Bengali)", fontSize = 10.sp) }, singleLine = true)
                    Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                        OutlinedTextField(value = rCals, onValueChange = { rCals = it }, label = { Text("Cals", fontSize = 10.sp) }, modifier = Modifier.weight(1f))
                        OutlinedTextField(value = rPrep, onValueChange = { rPrep = it }, label = { Text("Prep Time", fontSize = 10.sp) }, modifier = Modifier.weight(1f))
                    }
                    OutlinedTextField(value = rIngredients, onValueChange = { rIngredients = it }, label = { Text("Ingredients List", fontSize = 10.sp) })
                    OutlinedTextField(value = rVideo, onValueChange = { rVideo = it }, label = { Text("Video URL (YouTube)", fontSize = 10.sp) }, singleLine = true)
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        if (rTitle.isNotEmpty()) {
                            recipeList = recipeList + CustomRecipeItem(
                                UUID.randomUUID().toString(),
                                rTitle,
                                rTitleBn,
                                rCals.toIntOrNull() ?: 0,
                                rPrep,
                                rVideo,
                                rIngredients
                            )
                        }
                        showAddRecipeForm = false
                    }
                ) {
                    Text("Create Card")
                }
            },
            dismissButton = { TextButton(onClick = { showAddRecipeForm = false }) { Text("Cancel") } }
        )
    }
}

@Composable
fun AdminAlertsAdsSubView(isBengali: Boolean) {
    val context = LocalContext.current
    var notifTitle by remember { mutableStateOf("") }
    var notifBody by remember { mutableStateOf("") }
    var targetAudience by remember { mutableStateOf("All Users") }
    
    var notifHistory by remember {
        mutableStateOf(
            listOf(
                "Sent to [All]: Stay hydrated! Keep logs updated 💧",
                "Sent to [Premium Only]: New Clinical AI Suites added!"
            )
        )
    }

    // Advertisements Setup
    var adCampaigns by remember {
        mutableStateOf(
            listOf(
                Pair("Deshi Diet Premium Banner", true),
                Pair("Intermittent Fasting Interstitial ad", false)
            )
        )
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Text("Manual Push Notification Broadcaster", fontWeight = FontWeight.Bold, fontSize = 13.sp, color = MaterialTheme.colorScheme.primary)
        OutlinedTextField(
            value = notifTitle,
            onValueChange = { notifTitle = it },
            label = { Text("Notification Header", fontSize = 11.sp) },
            modifier = Modifier.fillMaxWidth()
        )
        OutlinedTextField(
            value = notifBody,
            onValueChange = { notifBody = it },
            label = { Text("Alert Body Text", fontSize = 11.sp) },
            modifier = Modifier.fillMaxWidth()
        )

        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(10.dp), verticalAlignment = Alignment.CenterVertically) {
            Text("Target Audience:", fontSize = 11.sp, fontWeight = FontWeight.Bold)
            listOf("All Users", "Premium Only", "Free Tiers").forEach { aud ->
                FilterChip(
                    selected = targetAudience == aud,
                    onClick = { targetAudience = aud },
                    label = { Text(aud, fontSize = 9.sp) }
                )
            }
        }

        Button(
            onClick = {
                if (notifTitle.isNotEmpty() && notifBody.isNotEmpty()) {
                    notifHistory = listOf("Sent to [$targetAudience]: $notifTitle - $notifBody") + notifHistory
                    Toast.makeText(context, "Push Sent Successfully to $targetAudience!", Toast.LENGTH_SHORT).show()
                    notifTitle = ""
                    notifBody = ""
                }
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Broadcast Notification Now 📣")
        }

        Text("History Logs", fontWeight = FontWeight.Bold, fontSize = 11.sp, color = Color.Gray)
        Card(modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(8.dp)) {
            Column(modifier = Modifier.padding(10.dp)) {
                notifHistory.forEach { hist ->
                    Text("• $hist", fontSize = 10.sp, color = Color.DarkGray, modifier = Modifier.padding(vertical = 2.dp))
                }
            }
        }

        Divider()

        Text("Mobile Ads Campaign & Monetization CMS", fontWeight = FontWeight.Bold, fontSize = 13.sp, color = MaterialTheme.colorScheme.primary)
        adCampaigns.forEachIndexed { index, campaign ->
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant)
            ) {
                Row(modifier = Modifier.padding(10.dp), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                    Column {
                        Text(campaign.first, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                        Text(if (campaign.second) "Status: Active Ads Network" else "Status: Paused", fontSize = 10.sp, color = if (campaign.second) Color(0xFF2E7D32) else Color.Red)
                    }
                    Switch(
                        checked = campaign.second,
                        onCheckedChange = { isChecked ->
                            adCampaigns = adCampaigns.mapIndexed { idx, pair ->
                                if (idx == index) pair.copy(second = isChecked) else pair
                            }
                        },
                        modifier = Modifier.scale(0.8f)
                    )
                }
            }
        }
    }
}

@Composable
fun AdminCouponsSubView(isBengali: Boolean) {
    val context = LocalContext.current
    var inputCoupon by remember { mutableStateOf("") }
    var inputDiscountPct by remember { mutableStateOf("") }
    
    var activeCouponsList by remember {
        mutableStateOf(
            listOf(
                Pair("SUMMER50", 50),
                Pair("NILJORIFREE", 100),
                Pair("DESHIHEALTH", 20)
            )
        )
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Text("Active Billing Promotion Coupons", fontWeight = FontWeight.Bold, fontSize = 13.sp, color = MaterialTheme.colorScheme.primary)
        
        Card(modifier = Modifier.fillMaxWidth()) {
            Column(modifier = Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                    Text("Coupon Code", fontWeight = FontWeight.Bold, fontSize = 11.sp, color = Color.Gray)
                    Text("Discount Value (%)", fontWeight = FontWeight.Bold, fontSize = 11.sp, color = Color.Gray)
                }
                Divider()
                activeCouponsList.forEach { (code, pct) ->
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                        Text(code, fontWeight = FontWeight.Bold, fontSize = 12.sp, color = MaterialTheme.colorScheme.primary)
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text("$pct% OFF", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = Color(0xFF2E7D32))
                            Spacer(modifier = Modifier.width(10.dp))
                            IconButton(onClick = { activeCouponsList = activeCouponsList.filter { it.first != code } }, modifier = Modifier.size(24.dp)) {
                                Icon(Icons.Default.Delete, contentDescription = "Delete", tint = Color.Red, modifier = Modifier.size(16.dp))
                            }
                        }
                    }
                }
            }
        }

        Text("Generate Discount Code", fontWeight = FontWeight.Bold, fontSize = 12.sp)
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            OutlinedTextField(
                value = inputCoupon,
                onValueChange = { inputCoupon = it },
                label = { Text("Code (e.g. FIT30)", fontSize = 10.sp) },
                modifier = Modifier.weight(1.5f),
                singleLine = true
            )
            OutlinedTextField(
                value = inputDiscountPct,
                onValueChange = { inputDiscountPct = it },
                label = { Text("Discount %", fontSize = 10.sp) },
                modifier = Modifier.weight(1f),
                singleLine = true
            )
        }
        Button(
            onClick = {
                val pct = inputDiscountPct.toIntOrNull()
                if (inputCoupon.isNotEmpty() && pct != null && pct in 1..100) {
                    activeCouponsList = activeCouponsList + Pair(inputCoupon.uppercase(), pct)
                    Toast.makeText(context, "Coupon Code Created!", Toast.LENGTH_SHORT).show()
                    inputCoupon = ""
                    inputDiscountPct = ""
                } else {
                    Toast.makeText(context, "Invalid input data", Toast.LENGTH_SHORT).show()
                }
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Publish Coupon Code 🎟️")
        }
    }
}

@Composable
fun AdminCMSAndRolesSubView(isBengali: Boolean) {
    val context = LocalContext.current
    var termsText by remember { mutableStateOf("Welcome to Niljori Health Plus! By using our clinical calculators and AI dietitian suggestions, you agree that this app does not constitute actual medical treatment...") }
    var announcementsText by remember { mutableStateOf("Announcement: Version 2.2 scheduled database migration is completed. Dynamic local cache is fully active.") }

    // Dynamic Permission Grid State
    var permissionsMap by remember {
        mutableStateOf(
            mapOf(
                "Super Admin" to mutableStateListOf(true, true, true, true, true),
                "Support Agent" to mutableStateListOf(false, true, false, true, false),
                "Medical Editor" to mutableStateListOf(true, false, true, false, false),
                "Premium User" to mutableStateListOf(false, false, false, false, true)
            )
        )
    }

    val permissionsList = listOf(
        "Can Manage Users",
        "Can Handle Support Alerts",
        "Can Edit Food DB & Recipes",
        "Can Send Push Alerts",
        "Can Export Medical Reports"
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        Text("Dynamic Permission Checkbox Matrix", fontWeight = FontWeight.Bold, fontSize = 13.sp, color = MaterialTheme.colorScheme.primary)
        Card(modifier = Modifier.fillMaxWidth()) {
            Column(modifier = Modifier.padding(10.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                permissionsMap.forEach { (role, permState) ->
                    Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                        Text(role, fontWeight = FontWeight.Bold, fontSize = 11.sp, color = MaterialTheme.colorScheme.primary)
                        Column {
                            permissionsList.forEachIndexed { index, permName ->
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    Checkbox(
                                        checked = permState[index],
                                        onCheckedChange = { isChecked ->
                                            permState[index] = isChecked
                                        },
                                        modifier = Modifier.scale(0.8f)
                                    )
                                    Text(permName, fontSize = 10.sp)
                                }
                            }
                        }
                        Divider(modifier = Modifier.padding(vertical = 4.dp))
                    }
                }
            }
        }

        Text("Live Content Management System (CMS)", fontWeight = FontWeight.Bold, fontSize = 13.sp, color = MaterialTheme.colorScheme.primary)
        
        Text("Modify App Announcements Block:", fontSize = 11.sp, fontWeight = FontWeight.Bold)
        OutlinedTextField(
            value = announcementsText,
            onValueChange = { announcementsText = it },
            modifier = Modifier.fillMaxWidth(),
            maxLines = 3
        )

        Text("Modify Terms & Service Text:", fontSize = 11.sp, fontWeight = FontWeight.Bold)
        OutlinedTextField(
            value = termsText,
            onValueChange = { termsText = it },
            modifier = Modifier.fillMaxWidth(),
            maxLines = 5
        )

        Button(
            onClick = {
                Toast.makeText(context, "Announcements & CMS Saved Successfully!", Toast.LENGTH_SHORT).show()
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Update App Static Data Live")
        }
    }
}

// =========================================================================
// VIEW 2: SECURITY SUITE
// =========================================================================
@Composable
fun SecuritySuiteView(isBengali: Boolean) {
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()
    
    // Accordion State tracking
    var expandedIndex by remember { mutableStateOf(0) }

    val sections = listOf(
        "🔑 JSON Web Token (JWT) Encoder/Decoder",
        "🔄 Access & Refresh Token Rotation",
        "🔒 HTTPS Cipher Secure & Pinning",
        "🛡️ Leaky Bucket Rate Limiting Simulation",
        "✉️ Email Verification Status Flow",
        "⏱️ OTP circular Authenticator",
        "🧮 AES-256 Symmetric & RSA Encryption"
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        Text(
            text = "Active Cryptographic Controls",
            fontWeight = FontWeight.Bold,
            fontSize = 14.sp,
            color = MaterialTheme.colorScheme.primary
        )

        sections.forEachIndexed { index, title ->
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(
                    containerColor = if (expandedIndex == index) MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f) else MaterialTheme.colorScheme.surface
                ),
                border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant)
            ) {
                Column(modifier = Modifier.fillMaxWidth()) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { expandedIndex = if (expandedIndex == index) -1 else index }
                            .padding(14.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(title, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                        Icon(
                            imageVector = if (expandedIndex == index) Icons.Default.ExpandLess else Icons.Default.ExpandMore,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.primary
                        )
                    }

                    AnimatedVisibility(visible = expandedIndex == index) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 14.dp, vertical = 8.dp)
                        ) {
                            when (index) {
                                0 -> JWTSimulator(isBengali)
                                1 -> RefreshTokenSimulator(isBengali)
                                2 -> HTTPSPinningSimulator(isBengali)
                                3 -> RateLimitingSimulator(isBengali)
                                4 -> EmailVerificationSimulator(isBengali)
                                5 -> OTPSimulator(isBengali)
                                6 -> EncryptionSimulator(isBengali)
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun JWTSimulator(isBengali: Boolean) {
    var rawJwt by remember { mutableStateOf("eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJ1c2VySWQiOiI5ODI4MyIsImVtYWlsIjoiaW10aWF6QG5pbGpvcmkuY29tIiwicm9sZSI6IlN1cGVyIEFkbWluIiwiZXhwIjoxNzg5MTkyODAwfQ.SignatureValidatedOK") }
    var decodedHeader by remember { mutableStateOf("{\n  \"alg\": \"HS256\",\n  \"typ\": \"JWT\"\n}") }
    var decodedPayload by remember { mutableStateOf("{\n  \"userId\": \"98283\",\n  \"email\": \"imtiaz@niljori.com\",\n  \"role\": \"Super Admin\",\n  \"exp\": 1789192800\n}") }

    var customUserId by remember { mutableStateOf("98283") }
    var customUserRole by remember { mutableStateOf("Super Admin") }

    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        Text("Raw JWT Access Token Block:", fontSize = 11.sp, fontWeight = FontWeight.Bold)
        OutlinedTextField(
            value = rawJwt,
            onValueChange = { rawJwt = it },
            modifier = Modifier.fillMaxWidth(),
            textStyle = MaterialTheme.typography.bodySmall.copy(fontFamily = FontFamily.Monospace, fontSize = 10.sp),
            maxLines = 4
        )

        Row(horizontalArrangement = Arrangement.spacedBy(8.dp), modifier = Modifier.fillMaxWidth()) {
            Button(
                onClick = {
                    // Simulating Decode action
                    decodedHeader = "{\n  \"alg\": \"HS256\",\n  \"typ\": \"JWT\"\n}"
                    decodedPayload = "{\n  \"userId\": \"$customUserId\",\n  \"role\": \"$customUserRole\",\n  \"exp\": 1789192800\n}"
                },
                modifier = Modifier.weight(1f)
            ) {
                Text("Decode JWT Claims", fontSize = 11.sp)
            }

            Button(
                onClick = {
                    // Simulating Encode action
                    rawJwt = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9." + 
                             Base64.getEncoder().encodeToString("{\"userId\":\"$customUserId\",\"role\":\"$customUserRole\",\"exp\":1789192800}".toByteArray()) + 
                             ".SignatureValidatedOK"
                },
                modifier = Modifier.weight(1f)
            ) {
                Text("Generate Signature", fontSize = 11.sp)
            }
        }

        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
            OutlinedTextField(value = customUserId, onValueChange = { customUserId = it }, label = { Text("User ID", fontSize = 10.sp) }, modifier = Modifier.weight(1f), singleLine = true)
            OutlinedTextField(value = customUserRole, onValueChange = { customUserRole = it }, label = { Text("Role Claim", fontSize = 10.sp) }, modifier = Modifier.weight(1f), singleLine = true)
        }

        Text("Decoded Token Claims Payload:", fontSize = 11.sp, fontWeight = FontWeight.Bold)
        Card(colors = CardDefaults.cardColors(containerColor = Color.Black)) {
            Column(modifier = Modifier.padding(10.dp).fillMaxWidth()) {
                Text(
                    text = decodedPayload,
                    fontFamily = FontFamily.Monospace,
                    fontSize = 11.sp,
                    color = Color(0xFF81C784)
                )
            }
        }
    }
}

@Composable
fun RefreshTokenSimulator(isBengali: Boolean) {
    val coroutineScope = rememberCoroutineScope()
    var logs by remember { mutableStateOf(listOf("System console initialized.", "HTTPS tunnel listening...")) }
    var clicksRemainingBeforeExpiration by remember { mutableStateOf(3) }
    var isRefreshing by remember { mutableStateOf(false) }

    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        Text("Click to trigger simulated Secure HTTPS API transactions:", fontSize = 11.sp)
        
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
            Button(
                onClick = {
                    if (clicksRemainingBeforeExpiration > 0) {
                        clicksRemainingBeforeExpiration--
                        logs = logs + "GET /api/v2/diet-profile: Success (Access Token is Active)"
                    } else {
                        coroutineScope.launch {
                            isRefreshing = true
                            logs = logs + "WARNING: Access Token Expired (401 Unauthorized)."
                            logs = logs + "Sending Refresh Token to /oauth/token..."
                            delay(1500)
                            clicksRemainingBeforeExpiration = 3
                            logs = logs + "OAUTH SUCCESS: Issued brand new JWT Rotation Token Pair!"
                            isRefreshing = false
                        }
                    }
                },
                enabled = !isRefreshing,
                modifier = Modifier.weight(1.5f)
            ) {
                Text(if (clicksRemainingBeforeExpiration == 0) "Access Token Expired! Refresh." else "Trigger API Query ($clicksRemainingBeforeExpiration clicks)")
            }

            Button(
                onClick = { logs = listOf("Console cleared.", "Ready for HTTPS queries...") },
                modifier = Modifier.weight(1f)
            ) {
                Text("Clear Log")
            }
        }

        Card(colors = CardDefaults.cardColors(containerColor = Color.Black)) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(110.dp)
                    .verticalScroll(rememberScrollState())
                    .padding(8.dp)
            ) {
                logs.forEach { log ->
                    Text("> $log", fontFamily = FontFamily.Monospace, fontSize = 9.sp, color = Color(0xFF64B5F6))
                }
            }
        }
    }
}

@Composable
fun HTTPSPinningSimulator(isBengali: Boolean) {
    var pinningEnabled by remember { mutableStateOf(true) }
    var pinLog by remember { mutableStateOf("Host: api.niljori.com\nFingerprint: sha256/grXm...=\nStatus: Secure Pin Verified ✅") }

    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
            Text("Enforce SSL Certificate Pinning", fontSize = 11.sp, fontWeight = FontWeight.Bold)
            Switch(
                checked = pinningEnabled,
                onCheckedChange = {
                    pinningEnabled = it
                    pinLog = if (it) {
                        "Host: api.niljori.com\nFingerprint: sha256/grXm...=\nStatus: Secure Pin Verified ✅"
                    } else {
                        "Host: api.niljori.com\nWarning: Certificate verification active but pinning disabled. Vulnerable to MITM ⚠️"
                    }
                },
                modifier = Modifier.scale(0.8f)
            )
        }

        Card(colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f))) {
            Column(modifier = Modifier.padding(10.dp).fillMaxWidth(), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                Text(pinLog, fontSize = 11.sp, fontFamily = FontFamily.Monospace, color = if (pinningEnabled) Color(0xFF2E7D32) else Color(0xFFE65100))
                Text("Negotiated TLS Block:\nTLS 1.3 / ECDHE_RSA_AES_256_GCM_SHA384", fontSize = 9.sp, fontFamily = FontFamily.Monospace, color = Color.Gray)
            }
        }
    }
}

@Composable
fun RateLimitingSimulator(isBengali: Boolean) {
    var tokensInBucket by remember { mutableStateOf(5) }
    var has429Error by remember { mutableStateOf(false) }
    var lastClickTime by remember { mutableStateOf(0L) }
    var blockTimerSeconds by remember { mutableStateOf(0) }

    LaunchedEffect(blockTimerSeconds) {
        if (blockTimerSeconds > 0) {
            delay(1000)
            blockTimerSeconds--
            if (blockTimerSeconds == 0) {
                has429Error = false
                tokensInBucket = 5
            }
        }
    }

    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        Text("DDoS Protection API Gateway Simulator (Token Bucket algorithm):", fontSize = 11.sp)
        
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
            Text("Tokens in bucket: ${"🔋".repeat(tokensInBucket)} ($tokensInBucket/5)", fontSize = 11.sp, fontWeight = FontWeight.Bold)
            Button(
                onClick = {
                    val now = System.currentTimeMillis()
                    if (tokensInBucket > 0) {
                        tokensInBucket--
                    } else {
                        has429Error = true
                        blockTimerSeconds = 5
                    }
                },
                enabled = !has429Error,
                colors = ButtonDefaults.buttonColors(containerColor = if (tokensInBucket <= 1) Color.Red else MaterialTheme.colorScheme.primary)
            ) {
                Text("Rapid API Click Test")
            }
        }

        if (has429Error) {
            Card(
                colors = CardDefaults.cardColors(containerColor = Color(0xFFFFEBEE)),
                border = BorderStroke(1.dp, Color.Red),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(modifier = Modifier.padding(10.dp), verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                    Text("⚠️", fontSize = 24.sp)
                    Column {
                        Text("HTTP 429 Too Many Requests", fontWeight = FontWeight.Bold, fontSize = 12.sp, color = Color.Red)
                        Text("Rate Limit exceeded! Request blocked for $blockTimerSeconds seconds.", fontSize = 11.sp, color = Color.DarkGray)
                    }
                }
            }
        }
    }
}

@Composable
fun EmailVerificationSimulator(isBengali: Boolean) {
    var emailVerified by remember { mutableStateOf(false) }
    var sendingEmail by remember { mutableStateOf(false) }
    val coroutineScope = rememberCoroutineScope()

    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
            Text("Verification Status:", fontSize = 11.sp, fontWeight = FontWeight.Bold)
            Box(
                modifier = Modifier
                    .background(if (emailVerified) Color(0xFFC8E6C9) else Color(0xFFFFCDD2), RoundedCornerShape(4.dp))
                    .padding(horizontal = 6.dp, vertical = 2.dp)
            ) {
                Text(
                    text = if (emailVerified) "VERIFIED ✅" else "UNVERIFIED ⚠️",
                    fontSize = 9.sp,
                    fontWeight = FontWeight.Bold,
                    color = if (emailVerified) Color(0xFF1B5E20) else Color(0xFFB71C1C)
                )
            }
        }

        Button(
            onClick = {
                coroutineScope.launch {
                    sendingEmail = true
                    delay(2000)
                    sendingEmail = false
                    emailVerified = true
                }
            },
            enabled = !sendingEmail && !emailVerified,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(if (sendingEmail) "Sending Secure SMTP verification link..." else if (emailVerified) "Email Verified successfully!" else "Send Verification Email Link")
        }
    }
}

@Composable
fun OTPSimulator(isBengali: Boolean) {
    var currentOtpCode by remember { mutableStateOf("482930") }
    var inputOtp by remember { mutableStateOf("") }
    var otpSecondsRemaining by remember { mutableStateOf(30) }
    var otpSuccess by remember { mutableStateOf(false) }

    LaunchedEffect(otpSecondsRemaining) {
        while (true) {
            delay(1000)
            if (otpSecondsRemaining > 1) {
                otpSecondsRemaining--
            } else {
                otpSecondsRemaining = 30
                currentOtpCode = String.format("%06d", Random().nextInt(999999))
                otpSuccess = false
            }
        }
    }

    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        Text("Two-Factor Authenticator Countdown (SMS/Google OTP):", fontSize = 11.sp)
        
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
            Card(colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer)) {
                Text(
                    text = currentOtpCode,
                    fontWeight = FontWeight.Bold,
                    fontFamily = FontFamily.Monospace,
                    fontSize = 20.sp,
                    color = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.padding(horizontal = 14.dp, vertical = 6.dp)
                )
            }
            Text("Regenerates in: ${otpSecondsRemaining}s ⏱️", fontSize = 11.sp, fontWeight = FontWeight.Bold)
        }

        Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(10.dp)) {
            OutlinedTextField(
                value = inputOtp,
                onValueChange = { inputOtp = it },
                label = { Text("Enter OTP", fontSize = 10.sp) },
                modifier = Modifier.weight(1f),
                singleLine = true
            )
            Button(
                onClick = {
                    if (inputOtp == currentOtpCode) {
                        otpSuccess = true
                    }
                }
            ) {
                Text("Verify OTP")
            }
        }

        if (otpSuccess) {
            Text("SUCCESS: Two-Factor Authenticated Successfully! ✅", fontSize = 11.sp, color = Color(0xFF2E7D32), fontWeight = FontWeight.Bold)
        }
    }
}

@Composable
fun EncryptionSimulator(isBengali: Boolean) {
    var clearText by remember { mutableStateOf("Clinical Diet Target: 2100kcal") }
    var algorithmSelected by remember { mutableStateOf("AES-256") }
    var cipherText by remember { mutableStateOf("") }
    var decryptedText by remember { mutableStateOf("") }

    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        OutlinedTextField(
            value = clearText,
            onValueChange = { clearText = it },
            label = { Text("Clinical Text Data to Encrypt", fontSize = 10.sp) },
            modifier = Modifier.fillMaxWidth()
        )

        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
            FilterChip(selected = algorithmSelected == "AES-256", onClick = { algorithmSelected = "AES-256" }, label = { Text("AES-256 Symmetric") })
            FilterChip(selected = algorithmSelected == "RSA", onClick = { algorithmSelected = "RSA" }, label = { Text("RSA Asymmetric") })
        }

        Button(
            onClick = {
                cipherText = if (algorithmSelected == "AES-256") {
                    "0x" + Base64.getEncoder().encodeToString(clearText.toByteArray()).take(16) + "...[Encrypted Symmetric AES]"
                } else {
                    "0x" + Base64.getEncoder().encodeToString(clearText.toByteArray()).take(12) + "...[RSA-2048 Public Key Cipher]"
                }
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Perform Hardware Cryptographic Encryption 🔒")
        }

        if (cipherText.isNotEmpty()) {
            Text("Ciphertext (Hex/Base64 Representation):", fontSize = 11.sp, fontWeight = FontWeight.Bold)
            Card(colors = CardDefaults.cardColors(containerColor = Color.Black)) {
                Text(
                    text = cipherText,
                    fontFamily = FontFamily.Monospace,
                    fontSize = 11.sp,
                    color = Color.Red,
                    modifier = Modifier.padding(10.dp).fillMaxWidth()
                )
            }

            Button(
                onClick = { decryptedText = clearText },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Decrypt Back to Original Cleartext 🔓")
            }
        }

        if (decryptedText.isNotEmpty()) {
            Text("Decrypted Output Data: $decryptedText", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = Color(0xFF2E7D32))
        }
    }
}

// =========================================================================
// VIEW 3: PREMIUM & BILLING
// =========================================================================
@Composable
fun PremiumBillingView(isBengali: Boolean, viewModel: DietPlannerViewModel) {
    var selectedPlanForCheckout by remember { mutableStateOf<String?>(null) }
    var selectedGateWay by remember { mutableStateOf<String?>(null) } // "stripe", "google", "apple"

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        Text(
            text = "Select Subscription Plan Tier",
            fontWeight = FontWeight.Bold,
            fontSize = 15.sp,
            color = MaterialTheme.colorScheme.primary
        )

        // Monthly Card
        PricingCard(
            tier = "Monthly Plan",
            price = "$4.99/month",
            desc = "Full access to 13 clinical AI tools, offline food databases, and premium charts.",
            badge = "Popular",
            color = MaterialTheme.colorScheme.primary,
            onClick = { selectedPlanForCheckout = "Monthly Plan" }
        )

        // Yearly Card
        PricingCard(
            tier = "Yearly Saver Plan",
            price = "$39.99/year",
            desc = "Save 33% on standard subscriptions. Secure deshi diet tracker active 12 months.",
            badge = "Best Value",
            color = Color(0xFF2E7D32),
            onClick = { selectedPlanForCheckout = "Yearly Saver Plan" }
        )

        // Trial Card
        PricingCard(
            tier = "7-Day Free Trial",
            price = "$0.00 / week",
            desc = "Test full premium capabilities. Cancel any time before expiration.",
            badge = "Free Tier",
            color = Color(0xFFE65100),
            onClick = { selectedPlanForCheckout = "7-Day Free Trial" }
        )

        if (selectedPlanForCheckout != null) {
            Divider()
            Text("Complete Payment Checkout For: ${selectedPlanForCheckout}", fontWeight = FontWeight.Bold, fontSize = 12.sp)
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                Button(onClick = { selectedGateWay = "stripe" }, modifier = Modifier.weight(1f)) {
                    Text("Stripe", fontSize = 11.sp)
                }
                Button(onClick = { selectedGateWay = "google" }, modifier = Modifier.weight(1f)) {
                    Text("Google Play", fontSize = 11.sp)
                }
                Button(onClick = { selectedGateWay = "apple" }, modifier = Modifier.weight(1f)) {
                    Text("Apple Store", fontSize = 11.sp)
                }
            }
        }
    }

    if (selectedGateWay != null) {
        when (selectedGateWay) {
            "stripe" -> StripeSimulatorDialog(
                plan = selectedPlanForCheckout ?: "",
                onDismiss = { selectedGateWay = null },
                onSuccess = {
                    viewModel.upgradeToPremium()
                    selectedGateWay = null
                    selectedPlanForCheckout = null
                }
            )
            "google" -> GoogleBillingSimulatorDialog(
                plan = selectedPlanForCheckout ?: "",
                onDismiss = { selectedGateWay = null },
                onSuccess = {
                    viewModel.upgradeToPremium()
                    selectedGateWay = null
                    selectedPlanForCheckout = null
                }
            )
            "apple" -> AppleBillingSimulatorDialog(
                plan = selectedPlanForCheckout ?: "",
                onDismiss = { selectedGateWay = null },
                onSuccess = {
                    viewModel.upgradeToPremium()
                    selectedGateWay = null
                    selectedPlanForCheckout = null
                }
            )
        }
    }
}

@Composable
fun PricingCard(tier: String, price: String, desc: String, badge: String, color: Color, onClick: () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() },
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        border = BorderStroke(1.dp, color.copy(alpha = 0.5f))
    ) {
        Column(modifier = Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                Text(tier, fontWeight = FontWeight.Black, fontSize = 14.sp, color = color)
                Box(modifier = Modifier.background(color.copy(alpha = 0.15f), RoundedCornerShape(4.dp)).padding(horizontal = 6.dp, vertical = 2.dp)) {
                    Text(badge, fontSize = 8.sp, fontWeight = FontWeight.Bold, color = color)
                }
            }
            Text(price, fontWeight = FontWeight.Bold, fontSize = 18.sp)
            Text(desc, fontSize = 11.sp, color = Color.Gray)
        }
    }
}

@Composable
fun StripeSimulatorDialog(plan: String, onDismiss: () -> Unit, onSuccess: () -> Unit) {
    val coroutineScope = rememberCoroutineScope()
    var ccNum by remember { mutableStateOf("4242 4242 4242 4242") }
    var ccExp by remember { mutableStateOf("12/28") }
    var ccCvc by remember { mutableStateOf("123") }
    var isPaying by remember { mutableStateOf(false) }
    var isDone by remember { mutableStateOf(false) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                Text("Stripe Gateway Checkout", fontWeight = FontWeight.Bold, fontSize = 14.sp)
            }
        },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                Text("Plan: $plan", fontWeight = FontWeight.Bold, fontSize = 12.sp)
                OutlinedTextField(value = ccNum, onValueChange = { ccNum = it }, label = { Text("Credit Card Number", fontSize = 10.sp) })
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedTextField(value = ccExp, onValueChange = { ccExp = it }, label = { Text("Expiry (MM/YY)", fontSize = 10.sp) }, modifier = Modifier.weight(1f))
                    OutlinedTextField(value = ccCvc, onValueChange = { ccCvc = it }, label = { Text("CVC", fontSize = 10.sp) }, modifier = Modifier.weight(1f), visualTransformation = PasswordVisualTransformation())
                }
                
                if (isPaying) {
                    LinearProgressIndicator(modifier = Modifier.fillMaxWidth().height(4.dp))
                    Text("Securing payment verification tunnel...", fontSize = 10.sp, color = Color.Gray)
                }

                if (isDone) {
                    Text("Stripe Success Callback: Authorized OK! ✅", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = Color(0xFF2E7D32))
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    coroutineScope.launch {
                        isPaying = true
                        delay(2000)
                        isPaying = false
                        isDone = true
                        delay(1000)
                        onSuccess()
                    }
                },
                enabled = !isPaying && !isDone
            ) {
                Text("Verify Stripe Pay")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Cancel") }
        }
    )
}

@Composable
fun GoogleBillingSimulatorDialog(plan: String, onDismiss: () -> Unit, onSuccess: () -> Unit) {
    val coroutineScope = rememberCoroutineScope()
    var isPaying by remember { mutableStateOf(false) }
    var isDone by remember { mutableStateOf(false) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Google Play Billing", fontWeight = FontWeight.Bold, fontSize = 14.sp) },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text("Google Play Console Platform Simulation", fontSize = 11.sp, color = Color.Gray)
                Text("App Item: Niljori Health Premium ($plan)", fontWeight = FontWeight.Bold, fontSize = 12.sp)
                Text("Payment Method: GPay (Visa ...9827)", fontSize = 11.sp)
                Text("User balance matches requirement.", fontSize = 10.sp, color = Color(0xFF2E7D32))

                if (isPaying) {
                    LinearProgressIndicator(modifier = Modifier.fillMaxWidth().height(4.dp))
                    Text("Contacting Google billing server...", fontSize = 10.sp, color = Color.Gray)
                }

                if (isDone) {
                    Text("Purchase Token Verified Successfully! ✅", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = Color(0xFF2E7D32))
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    coroutineScope.launch {
                        isPaying = true
                        delay(1500)
                        isPaying = false
                        isDone = true
                        delay(1000)
                        onSuccess()
                    }
                },
                enabled = !isPaying && !isDone
            ) {
                Text("One-Tap Buy")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Cancel") }
        }
    )
}

@Composable
fun AppleBillingSimulatorDialog(plan: String, onDismiss: () -> Unit, onSuccess: () -> Unit) {
    val coroutineScope = rememberCoroutineScope()
    var isPaying by remember { mutableStateOf(false) }
    var isDone by remember { mutableStateOf(false) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Apple App Store Checkout", fontWeight = FontWeight.Bold, fontSize = 14.sp) },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text("iOS Apple Pay Simulator Theme", fontSize = 11.sp, color = Color.Gray)
                Text("Service: Niljori Premium ($plan)", fontWeight = FontWeight.Bold, fontSize = 12.sp)
                Text("Apple ID: imtiaz@niljori.com", fontSize = 11.sp)

                if (isPaying) {
                    LinearProgressIndicator(modifier = Modifier.fillMaxWidth().height(4.dp))
                    Text("Simulating TouchID scan verification...", fontSize = 10.sp, color = Color.Gray)
                }

                if (isDone) {
                    Text("Apple Receipt Validated! ✅", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = Color(0xFF2E7D32))
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    coroutineScope.launch {
                        isPaying = true
                        delay(1500)
                        isPaying = false
                        isDone = true
                        delay(1000)
                        onSuccess()
                    }
                },
                enabled = !isPaying && !isDone
            ) {
                Text("Double Click to Pay")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Cancel") }
        }
    )
}
