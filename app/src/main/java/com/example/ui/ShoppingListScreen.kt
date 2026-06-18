package com.example.ui

import androidx.compose.animation.*
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.DeleteSweep
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material.icons.filled.DoneAll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.viewmodel.DietPlannerViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ShoppingListScreen(
    viewModel: DietPlannerViewModel,
    onBack: () -> Unit
) {
    val isBengali by viewModel.isBengali.collectAsState()
    val shoppingItems by viewModel.shoppingItems.collectAsState()
    val focusManager = LocalFocusManager.current

    // Manual input fields
    var customItemName by remember { mutableStateOf("") }
    var customItemQuantity by remember { mutableStateOf("") }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = if (isBengali) "বাজারের সামগ্রিক তালিকা" else "Diet Shopping List",
                        fontWeight = FontWeight.Bold,
                        fontSize = 18.sp,
                        color = Color(0xFF1E5E2F)
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(
                            imageVector = Icons.Default.ArrowBack,
                            contentDescription = "Back",
                            tint = Color(0xFF1E5E2F)
                        )
                    }
                },
                actions = {
                    IconButton(
                        onClick = { viewModel.clearShoppingListForSelectedDate() },
                        modifier = Modifier.testTag("clear_shopping_list")
                    ) {
                        Icon(
                            imageVector = Icons.Default.DeleteSweep,
                            contentDescription = "Clear List",
                            tint = Color(0xFFC62828)
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color.White,
                    titleContentColor = Color(0xFF1E5E2F)
                )
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .background(Color(0xFFF9FBF9))
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // High detail information guideline badge card
            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = Color(0xFFE8F5E9)),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier.padding(14.dp),
                    horizontalArrangement = Arrangement.spacedBy(10.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(36.dp)
                            .background(Color.White, CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Info,
                            contentDescription = null,
                            tint = Color(0xFF2E7D32),
                            modifier = Modifier.size(18.dp)
                        )
                    }
                    Text(
                        text = if (isBengali)
                            "জেনারেট করা ডায়েট প্ল্যানে ব্যবহৃত সুষম উপকরণসমূহ এখানে স্বয়ংক্রিয়ভাবে যুক্ত হয়। তাছাড়া আপনি নিজের প্রয়োজনীয় সামগ্রীও যোগ করতে পারেন।"
                        else
                            "Contains all nutritional materials parsed from your customized healthy food guidelines. Add custom items manually to prepare for your groceries.",
                        fontSize = 11.sp,
                        color = Color(0xFF2E7D32),
                        lineHeight = 15.sp,
                        modifier = Modifier.weight(1f)
                    )
                }
            }

            // Quick Manual item adder row panel
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                OutlinedTextField(
                    value = customItemName,
                    onValueChange = { customItemName = it },
                    label = { Text(if (isBengali) "সামগ্রী" else "Item Name", fontSize = 11.sp) },
                    placeholder = { Text("e.g. Oats", fontSize = 11.sp) },
                    singleLine = true,
                    modifier = Modifier
                        .weight(1.5f)
                        .testTag("custom_item_name_field"),
                    shape = RoundedCornerShape(12.dp)
                )

                OutlinedTextField(
                    value = customItemQuantity,
                    onValueChange = { customItemQuantity = it },
                    label = { Text(if (isBengali) "পরিমাণ" else "Qty", fontSize = 11.sp) },
                    placeholder = { Text("500g", fontSize = 11.sp) },
                    singleLine = true,
                    modifier = Modifier
                        .weight(1f)
                        .testTag("custom_item_qty_field"),
                    shape = RoundedCornerShape(12.dp)
                )

                IconButton(
                    onClick = {
                        if (customItemName.isNotBlank()) {
                            val qty = customItemQuantity.ifBlank { "As needed" }
                            viewModel.addManualShoppingItem(customItemName.trim(), qty.trim())
                            customItemName = ""
                            customItemQuantity = ""
                            focusManager.clearFocus()
                        }
                    },
                    modifier = Modifier
                        .size(46.dp)
                        .background(Color(0xFF2E7D32), CircleShape)
                        .testTag("add_custom_shopping_item_btn")
                ) {
                    Icon(
                        imageVector = Icons.Default.Add,
                        contentDescription = "Add custom item",
                        tint = Color.White,
                        modifier = Modifier.size(20.dp)
                    )
                }
            }

            // Main vertical list scroll representing the shopping list items
            if (shoppingItems.isEmpty()) {
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxWidth(),
                    contentAlignment = Alignment.Center
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.ShoppingCart,
                            contentDescription = "Empty visualizer",
                            tint = Color.LightGray,
                            modifier = Modifier.size(64.dp)
                        )
                        Text(
                            text = if (isBengali) "আপনার বাজারের ফর্দ খালি রয়েছে!" else "No grocery items currently added",
                            fontWeight = FontWeight.Bold,
                            color = Color.Gray,
                            fontSize = 14.sp
                        )
                        Text(
                            text = if (isBengali) "মেল ট্যাবে খাবার তৈরি বা অলটারনেট প্ল্যান করুন।" else "Generate or manual log food items to build your grocery lists.",
                            color = Color.LightGray,
                            fontSize = 11.sp
                        )
                    }
                }
            } else {
                LazyColumn(
                    modifier = Modifier.weight(1f).fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(shoppingItems) { item ->
                        Card(
                            shape = RoundedCornerShape(12.dp),
                            colors = CardDefaults.cardColors(
                                containerColor = if (item.isChecked) Color(0xFFF1F8E9) else Color.White
                            ),
                            border = BorderStroke(
                                width = 1.dp,
                                color = if (item.isChecked) Color(0xFFC8E6C9) else Color(0xFFECEFF1)
                            ),
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable {
                                    viewModel.toggleShoppingItemChecked(item.id, !item.isChecked)
                                }
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(horizontal = 14.dp, vertical = 10.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                                ) {
                                    Checkbox(
                                        checked = item.isChecked,
                                        onCheckedChange = { viewModel.toggleShoppingItemChecked(item.id, it) },
                                        colors = CheckboxDefaults.colors(checkedColor = Color(0xFF2E7D32)),
                                        modifier = Modifier.testTag("checkbox_${item.id}")
                                    )

                                    Column {
                                        Text(
                                            text = item.name,
                                            fontWeight = FontWeight.SemiBold,
                                            fontSize = 13.sp,
                                            color = if (item.isChecked) Color.Gray else Color.Black,
                                            textDecoration = if (item.isChecked) TextDecoration.LineThrough else TextDecoration.None
                                        )

                                        Text(
                                            text = item.quantity,
                                            fontSize = 11.sp,
                                            color = Color.Gray
                                        )
                                    }
                                }

                                if (item.isChecked) {
                                    Icon(
                                        imageVector = Icons.Default.DoneAll,
                                        contentDescription = "Bought",
                                        tint = Color(0xFF388E3C),
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
