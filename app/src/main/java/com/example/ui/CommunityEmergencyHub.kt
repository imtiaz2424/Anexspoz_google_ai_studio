package com.example.ui

import android.content.Context
import android.widget.Toast
import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.viewmodel.DietPlannerViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

// --- COMMUNITY POST DEFEINITION ---
data class CommunityPost(
    val id: String,
    val author: String,
    val role: String,
    val relativeTime: String,
    val content: String,
    val category: String,
    val categoryColor: Color,
    val likesCount: Int,
    val commentCount: Int,
    val isLiked: Boolean = false,
    val postEmoji: String = "🍲"
)

// --- EMERGENCY CONTACT ---
data class SpeedDialEmergencyContact(
    val id: String,
    val name: String,
    val phone: String,
    val isVerified: Boolean = true,
    val emoji: String = "👨"
)

// --- POPULAR COMMUNITY GROUP ---
data class CommunityGroup(
    val id: String,
    val title: String,
    val memberCount: String,
    val category: String,
    val isJoined: Boolean = false
)

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun InteractiveVisualFlowDiagram(
    isBengali: Boolean,
    onNavigateToTab: (Int) -> Unit,
    onOpenEmergencyHelp: () -> Unit,
    onOpenCommunityHub: () -> Unit,
    onOpenSelectMood: () -> Unit,
    onOpenFindRestaurants: () -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current

    // Node details matching the arrow-connected flow image!
    val flowNodes = listOf(
        FlowDiagramNode(
            title = if (isBengali) "হোম" else "Home",
            subtitle = if (isBengali) "ড্যাশবোর্ড স্পেক্স" else "Core Dashboard",
            emoji = "🏠",
            backgroundColor = Color(0xFFE8F5E9),
            borderColor = Color(0xFF81C784),
            textColor = Color(0xFF1B5E20),
            action = { onNavigateToTab(0) }
        ),
        FlowDiagramNode(
            title = if (isBengali) "মুড নির্বাচন" else "Select Mood",
            subtitle = if (isBengali) "অনুভূতি লগ করুন" else "Log daily vibe",
            emoji = "😊",
            backgroundColor = Color(0xFFECEFF1),
            borderColor = Color(0xFFB0BEC5),
            textColor = Color(0xFF37474F),
            action = onOpenSelectMood
        ),
        FlowDiagramNode(
            title = if (isBengali) "পরামর্শ" else "Get Suggestions",
            subtitle = if (isBengali) "খাদ্য ও পরামর্শ" else "Diet guidance",
            emoji = "💡",
            backgroundColor = Color(0xFFFFFDE7),
            borderColor = Color(0xFFFFF59D),
            textColor = Color(0xFFF57F17),
            action = { onNavigateToTab(1) }
        ),
        FlowDiagramNode(
            title = if (isBengali) "রেস্টুরেন্ট খুঁজুন" else "Find Restaurants",
            subtitle = if (isBengali) "আশেপাশের খাবার" else "Healthy locations",
            emoji = "📍",
            backgroundColor = Color(0xFFE3F2FD),
            borderColor = Color(0xFF90CAF9),
            textColor = Color(0xFF0D47A1),
            action = onOpenFindRestaurants
        ),
        FlowDiagramNode(
            title = if (isBengali) "অর্ডার ডেমো" else "Order Food",
            subtitle = if (isBengali) "খাদ্য লগ ও অর্ডার" else "Log nutrients",
            emoji = "🍔",
            backgroundColor = Color(0xFFFFF3E0),
            borderColor = Color(0xFFFFCC80),
            textColor = Color(0xFFE65100),
            action = { onNavigateToTab(1) }
        ),
        FlowDiagramNode(
            title = if (isBengali) "উন্নতি ট্র্যাক" else "Track Progress",
            subtitle = if (isBengali) "ওজন ও পানি সূচক" else "Weight & water",
            emoji = "📈",
            backgroundColor = Color(0xFFF3E5F5),
            borderColor = Color(0xFFCE93D8),
            textColor = Color(0xFF4A148C),
            action = { onNavigateToTab(3) }
        ),
        FlowDiagramNode(
            title = if (isBengali) "কমিউনিটি হাব" else "Join Community",
            subtitle = if (isBengali) "গ্রুপ ও ফিড আলোচনা" else "Group feed",
            emoji = "👥",
            backgroundColor = Color(0xFFE0F7FA),
            borderColor = Color(0xFF80DEEA),
            textColor = Color(0xFF006064),
            action = onOpenCommunityHub
        ),
        FlowDiagramNode(
            title = if (isBengali) "জরুরি সাহায্য" else "Request Help",
            subtitle = if (isBengali) "এসওএস রেসপন্স" else "Emergency SOS Help",
            emoji = "🚨",
            backgroundColor = Color(0xFFFFEBEE),
            borderColor = Color(0xFFEF9A9A),
            textColor = Color(0xFFB71C1C),
            action = onOpenEmergencyHelp
        )
    )

    Card(
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        border = BorderStroke(1.2.dp, MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f)),
        modifier = modifier
            .fillMaxWidth()
            .testTag("visual_flow_diagram_card")
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // Header matching diagram Anexsopz context
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                        Icon(
                            imageVector = Icons.Default.Schema,
                            contentDescription = "Flow Schema",
                            tint = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(18.dp)
                        )
                        Text(
                            text = if (isBengali) "নীলজরি হেলথ ও লাইফফ্লো ডায়াগ্রাম" else "Niljori Interactive LifeFlow",
                            fontWeight = FontWeight.ExtraBold,
                            fontSize = 13.5.sp,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                    }
                    Text(
                        text = if (isBengali) "Eat Well, Feel Safe! - ইন্টারঅ্যাক্টিভ রোডম্যাপ" else "Eat Well, Feel Safe! — Click any node to open",
                        fontSize = 10.sp,
                        color = MaterialTheme.colorScheme.secondary,
                        fontWeight = FontWeight.Medium
                    )
                }

                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(8.dp))
                        .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.08f))
                        .padding(horizontal = 8.dp, vertical = 3.dp)
                ) {
                    Text(
                        text = if (isBengali) "লাইভ মডেল" else "Interactive Node",
                        fontSize = 9.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary
                    )
                }
            }

            Divider(color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.4f))

            // Node visual flow with arrows
            ScrollableRowWithConnectors(
                nodes = flowNodes,
                isBengali = isBengali
            )

            Text(
                text = if (isBengali) 
                    "💡 টিপস: উপরের মডেল ডায়াগ্রামের যেকোনো ধাপে ক্লিক করে সরাসরি সেই অপশনে চলে যেতে পারেন।" 
                    else "💡 Guidance: Swipe horizontally and tap on any milestone to open that functional screen instantly.",
                fontSize = 10.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                lineHeight = 14.sp,
                style = MaterialTheme.typography.bodySmall,
                modifier = Modifier.padding(top = 4.dp)
            )
        }
    }
}

data class FlowDiagramNode(
    val title: String,
    val subtitle: String,
    val emoji: String,
    val backgroundColor: Color,
    val borderColor: Color,
    val textColor: Color,
    val action: () -> Unit
)

@Composable
fun ScrollableRowWithConnectors(
    nodes: List<FlowDiagramNode>,
    isBengali: Boolean
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .horizontalScroll(rememberScrollState())
            .padding(vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        nodes.forEachIndexed { index, node ->
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                // Node item
                Card(
                    modifier = Modifier
                        .width(140.dp)
                        .clickable { node.action() },
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = node.backgroundColor),
                    border = BorderStroke(1.2.dp, node.borderColor)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(10.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(32.dp)
                                .background(Color.White.copy(alpha = 0.6f), CircleShape),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(node.emoji, fontSize = 16.sp)
                        }
                        
                        Text(
                            text = node.title,
                            fontWeight = FontWeight.Bold,
                            fontSize = 11.sp,
                            color = node.textColor,
                            textAlign = TextAlign.Center,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                        
                        Text(
                            text = node.subtitle,
                            fontSize = 8.5.sp,
                            color = Color.DarkGray,
                            textAlign = TextAlign.Center,
                            lineHeight = 11.sp,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    }
                }

                // Connection arrow
                if (index < nodes.size - 1) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier.padding(horizontal = 4.dp)
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                            contentDescription = "connects",
                            tint = Color.Gray.copy(alpha = 0.5f),
                            modifier = Modifier.size(18.dp)
                        )
                        Text(
                            text = if (isBengali) "পরবর্তী" else "Next",
                            fontSize = 7.sp,
                            color = Color.Gray
                        )
                    }
                }
            }
        }
    }
}

// --- COMMUNITY HUB COMPONENT ---
@OptIn(ExperimentalLayoutApi::class, ExperimentalMaterial3Api::class)
@Composable
fun CommunityHubScreen(
    isBengali: Boolean,
    viewModel: DietPlannerViewModel,
    onBack: () -> Unit
) {
    val context = LocalContext.current
    var activeSubTab by remember { mutableStateOf("feed") } // "feed", "groups", "notifications"
    var showCreatePostDialog by remember { mutableStateOf(false) }

    // Sample dynamic feed data
    val defaultPosts = remember {
        mutableStateListOf(
            CommunityPost(
                id = "p1",
                author = "Rahim Mia",
                role = if (isBengali) "প্রো ডায়েট মেম্বার" else "Pro Diet Member",
                relativeTime = "2m ago",
                content = if (isBengali) 
                    "আমি ডায়েট প্ল্যান মেনে ৭ দিনে ২ কেজি ওজন কমিয়ে ফেলেছি! ধন্যবাদ নীলজরি টিম।"
                    else "I followed the customized Bengal diet plan for 7 days and shed 2 kgs already! Feeling super dynamic.",
                category = "Mood Mental",
                categoryColor = Color(0xFF673AB7),
                likesCount = 14,
                commentCount = 5,
                postEmoji = "🥗"
            ),
            CommunityPost(
                id = "p2",
                author = "Dr. Anisur Rahman",
                role = if (isBengali) "সাক্ষ্যপ্রাপ্ত পুষ্টিবিদ" else "Certified Nutritionist",
                relativeTime = "1h ago",
                content = if (isBengali) 
                    "সকালে খালি পেটে কুসুম গরম পানিতে লেবু ও এক চিমটি লবণ মিশিয়ে খাওয়া বিপাকক্রিয়া বাড়াতে সাহায্য করে।"
                    else "Sip lukewarm water with lemon juice first thing in the morning to scale up your metabolism rates.",
                category = "Nutrition Tip",
                categoryColor = Color(0xFF2E7D32),
                likesCount = 42,
                commentCount = 12,
                postEmoji = "🍋"
            ),
            CommunityPost(
                id = "p3",
                author = "Farhana Islam",
                role = if (isBengali) "ফিটনেস লাভার" else "Fitness Enthusiast",
                relativeTime = "3h ago",
                content = if (isBengali) 
                    "আজ সকালে ৩ কিমি দৌড়ালাম। কার কেমন এক্সারসাইজ গোল আজকে মেন্টেন করা হচ্ছে?"
                    else "Enthusiastically smashed a 3km jogging track! Are we logging our fitness minutes on Tracker today?",
                category = "Cardio Action",
                categoryColor = Color(0xFF0288D1),
                likesCount = 28,
                commentCount = 7,
                postEmoji = "🏃‍♀️"
            )
        )
    }

    // Sample groups data
    val defaultGroups = remember {
        mutableStateListOf(
            CommunityGroup("g1", if (isBengali) "সুস্থ খাবার গ্যাং" else "Healthy Foodies", "4.2k members", "Nutrition", false),
            CommunityGroup("g2", if (isBengali) "পানি টার্গেট চ্যাম্পস" else "Water Hydrowarriors", "1.8k members", "Hydration", true),
            CommunityGroup("g3", if (isBengali) "কার্ডিও স্প্রিন্টার্স" else "Cardio Sprinters Elite", "3.1k members", "Running", false)
        )
    }

    // Notification updates
    val notificationItems = listOf(
        Pair(if (isBengali) "সামিন রহমান আপনার পোস্টে কমেন্ট করেছেন।" else "Samin Rahman commented on your meal post.", "5m ago"),
        Pair(if (isBengali) "সাপ্তাহিক ওজনের নতুন লক্ষ্য অর্জিত হয়েছে!" else "Congrats! Your weekly weight milestone has been unlocked.", "2h ago"),
        Pair(if (isBengali) "নিউট্রিশনিস্ট অনিক আপনার প্লেট রিভিও করেছেন।" else "Nutritionist Anik reviewed your customized breakfast logs.", "1d ago")
    )

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = if (isBengali) "কমিউনিটি ফিড ও হাব" else "Community Hub",
                        fontWeight = FontWeight.ExtraBold,
                        color = MaterialTheme.colorScheme.primary,
                        fontSize = 18.sp
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(imageVector = Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                },
                actions = {
                    IconButton(onClick = { showCreatePostDialog = true }) {
                        Icon(imageVector = Icons.Default.Add, contentDescription = "Create post", tint = MaterialTheme.colorScheme.primary)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = MaterialTheme.colorScheme.surface)
            )
        },
        floatingActionButton = {
            if (activeSubTab == "feed") {
                FloatingActionButton(
                    onClick = { showCreatePostDialog = true },
                    containerColor = MaterialTheme.colorScheme.primary,
                    contentColor = Color.White
                ) {
                    Icon(imageVector = Icons.Default.Edit, contentDescription = "Write Post")
                }
            }
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .background(Color(0xFFF9FAFC))
        ) {
            // Tab Toggle Selector
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color.White)
                    .padding(vertical = 4.dp, horizontal = 12.dp)
            ) {
                listOf(
                    Triple("feed", if (isBengali) "ফিড আলোচনা" else "Feed Lounge", Icons.Default.Forum),
                    Triple("groups", if (isBengali) "পপুলার গ্রুপ" else "Clubs & Groups", Icons.Default.Groups),
                    Triple("notifications", if (isBengali) "আপডেট নোটিফিকেশন" else "Alert Updates", Icons.Default.Notifications)
                ).forEach { (tabId, tabName, iconObj) ->
                    val isSelected = activeSubTab == tabId
                    Button(
                        onClick = { activeSubTab = tabId },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = if (isSelected) MaterialTheme.colorScheme.primary else Color.Transparent,
                            contentColor = if (isSelected) Color.White else Color.Gray
                        ),
                        shape = RoundedCornerShape(10.dp),
                        modifier = Modifier
                            .weight(1f)
                            .padding(2.dp)
                            .height(38.dp),
                        contentPadding = PaddingValues(horizontal = 4.dp)
                    ) {
                        Row(
                            horizontalArrangement = Arrangement.spacedBy(4.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(imageVector = iconObj, contentDescription = tabId, modifier = Modifier.size(14.dp))
                            Text(tabName, fontSize = 10.5.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }

            Divider(color = Color(0xFFECEFF1))

            // Sub tab sections
            when (activeSubTab) {
                "feed" -> {
                    LazyColumn(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(horizontal = 14.dp),
                        verticalArrangement = Arrangement.spacedBy(10.dp),
                        contentPadding = PaddingValues(top = 10.dp, bottom = 80.dp)
                    ) {
                        item {
                            // Quick Post Prompt Card
                            Card(
                                shape = RoundedCornerShape(16.dp),
                                colors = CardDefaults.cardColors(containerColor = Color.White),
                                border = BorderStroke(1.dp, Color(0xFFECEFF1)),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clickable { showCreatePostDialog = true }
                            ) {
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(14.dp),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                                ) {
                                    Box(
                                        modifier = Modifier
                                            .size(40.dp)
                                            .background(
                                                MaterialTheme.colorScheme.primaryContainer,
                                                CircleShape
                                            ),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Text("👤", fontSize = 18.sp)
                                    }
                                    Text(
                                        text = if (isBengali) "আজকের অনুভূতি বা ডায়েট অভিজ্ঞতা লিখুন..." else "What's on your mind? Post wellness updates...",
                                        color = Color.Gray,
                                        fontSize = 11.5.sp,
                                        fontWeight = FontWeight.Medium,
                                        modifier = Modifier.weight(1f)
                                    )
                                    Icon(
                                        imageVector = Icons.Default.PhotoCamera,
                                        contentDescription = "Attach",
                                        tint = Color.Gray.copy(alpha = 0.8f)
                                    )
                                }
                            }
                        }

                        items(defaultPosts) { post ->
                            var liked by remember { mutableStateOf(post.isLiked) }
                            var likesVal by remember { mutableStateOf(post.likesCount) }

                            Card(
                                shape = RoundedCornerShape(16.dp),
                                colors = CardDefaults.cardColors(containerColor = Color.White),
                                border = BorderStroke(1.dp, Color(0xFFECEFF1).copy(alpha = 0.8f)),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Column(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(14.dp),
                                    verticalArrangement = Arrangement.spacedBy(8.dp)
                                ) {
                                    // Author Row
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                                    ) {
                                        Box(
                                            modifier = Modifier
                                                .size(36.dp)
                                                .background(
                                                    post.categoryColor.copy(alpha = 0.1f),
                                                    CircleShape
                                                ),
                                            contentAlignment = Alignment.Center
                                        ) {
                                            Text(post.postEmoji, fontSize = 18.sp)
                                        }

                                        Column(modifier = Modifier.weight(1f)) {
                                            Text(
                                                text = post.author,
                                                fontWeight = FontWeight.Bold,
                                                fontSize = 12.5.sp,
                                                color = Color(0xFF263238)
                                            )
                                            Text(
                                                text = post.role,
                                                fontSize = 9.sp,
                                                color = Color.Gray,
                                                fontWeight = FontWeight.SemiBold
                                            )
                                        }

                                        Text(
                                            text = post.relativeTime,
                                            fontSize = 9.sp,
                                            color = Color.Gray
                                        )
                                    }

                                    // Content Text
                                    Text(
                                        text = post.content,
                                        fontSize = 11.5.sp,
                                        color = Color(0xFF37474F),
                                        lineHeight = 15.sp
                                    )

                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        // Category chip
                                        Box(
                                            modifier = Modifier
                                                .clip(RoundedCornerShape(6.dp))
                                                .background(post.categoryColor.copy(alpha = 0.08f))
                                                .padding(horizontal = 6.dp, vertical = 2.dp)
                                        ) {
                                            Text(
                                                text = post.category,
                                                fontWeight = FontWeight.Bold,
                                                fontSize = 8.5.sp,
                                                color = post.categoryColor
                                            )
                                        }

                                        // Action reactions Row
                                        Row(
                                            horizontalArrangement = Arrangement.spacedBy(14.dp),
                                            verticalAlignment = Alignment.CenterVertically
                                        ) {
                                            Row(
                                                verticalAlignment = Alignment.CenterVertically,
                                                horizontalArrangement = Arrangement.spacedBy(4.dp),
                                                modifier = Modifier.clickable {
                                                    liked = !liked
                                                    likesVal += if (liked) 1 else -1
                                                }
                                            ) {
                                                Icon(
                                                    imageVector = if (liked) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
                                                    contentDescription = "Like",
                                                    tint = if (liked) Color.Red else Color.Gray,
                                                    modifier = Modifier.size(16.dp)
                                                )
                                                Text("$likesVal", fontSize = 10.5.sp, color = if (liked) Color.Red else Color.Gray, fontWeight = FontWeight.Bold)
                                            }

                                            Row(
                                                verticalAlignment = Alignment.CenterVertically,
                                                horizontalArrangement = Arrangement.spacedBy(4.dp),
                                                modifier = Modifier.clickable {
                                                    Toast.makeText(context, if (isBengali) "লোডিং কথোপকথন..." else "Loading replies thread...", Toast.LENGTH_SHORT).show()
                                                }
                                            ) {
                                                Icon(
                                                    imageVector = Icons.Default.Comment,
                                                    contentDescription = "Comment",
                                                    tint = Color.Gray,
                                                    modifier = Modifier.size(16.dp)
                                                )
                                                Text("${post.commentCount}", fontSize = 10.5.sp, color = Color.Gray, fontWeight = FontWeight.Bold)
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }

                "groups" -> {
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(14.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Text(
                            text = if (isBengali) "সক্রিয় ক্লাব বা গ্রুপ গুলো" else "Trending Digital Health Clubs",
                            fontWeight = FontWeight.Bold,
                            fontSize = 13.sp,
                            color = MaterialTheme.colorScheme.onSurface
                        )

                        defaultGroups.forEachIndexed { idx, group ->
                            Card(
                                shape = RoundedCornerShape(14.dp),
                                colors = CardDefaults.cardColors(containerColor = Color.White),
                                border = BorderStroke(1.dp, Color(0xFFECEFF1))
                            ) {
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
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
                                                .size(36.dp)
                                                .clip(CircleShape)
                                                .background(MaterialTheme.colorScheme.primaryContainer),
                                            contentAlignment = Alignment.Center
                                        ) {
                                            Text(if (idx == 0) "🥦" else if (idx == 1) "💧" else "🏃", fontSize = 18.sp)
                                        }

                                        Column {
                                            Text(
                                                text = group.title,
                                                fontWeight = FontWeight.Bold,
                                                fontSize = 12.5.sp,
                                                color = Color(0xFF263238)
                                            )
                                            Text(
                                                text = "${group.memberCount} • ${group.category}",
                                                fontSize = 9.sp,
                                                color = Color.Gray
                                            )
                                        }
                                    }

                                    Button(
                                        onClick = {
                                            defaultGroups[idx] = group.copy(isJoined = !group.isJoined)
                                            Toast.makeText(
                                                context, 
                                                if (group.isJoined) 
                                                    (if (isBengali) "গ্রুপ ত্যাগ করেছেন" else "Left Group")
                                                else 
                                                    (if (isBengali) "গ্রুপে যোগদান করেছেন!" else "Joined Group Successfully!"), 
                                                Toast.LENGTH_SHORT
                                            ).show()
                                        },
                                        colors = ButtonDefaults.buttonColors(
                                            containerColor = if (group.isJoined) MaterialTheme.colorScheme.outlineVariant else MaterialTheme.colorScheme.primary
                                        ),
                                        shape = RoundedCornerShape(8.dp),
                                        contentPadding = PaddingValues(horizontal = 12.dp, vertical = 2.dp),
                                        modifier = Modifier.height(28.dp)
                                    ) {
                                        Text(
                                            text = if (group.isJoined) {
                                                if (isBengali) "যুক্ত আছেন" else "Joined"
                                            } else {
                                                if (isBengali) "যুক্ত হোন" else "Join Guild"
                                            },
                                            fontSize = 9.5.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = if (group.isJoined) Color.DarkGray else Color.White
                                        )
                                    }
                                }
                            }
                        }
                    }
                }

                "notifications" -> {
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(14.dp),
                        verticalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        notificationItems.forEach { (msg, timeStr) ->
                            Card(
                                shape = RoundedCornerShape(12.dp),
                                colors = CardDefaults.cardColors(containerColor = Color.White),
                                border = BorderStroke(1.dp, Color(0xFFECEFF1))
                            ) {
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(12.dp),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                                ) {
                                    Box(
                                        modifier = Modifier
                                            .size(10.dp)
                                            .clip(CircleShape)
                                            .background(MaterialTheme.colorScheme.primary)
                                    )
                                    Column(modifier = Modifier.weight(1f)) {
                                        Text(msg, fontSize = 11.sp, color = Color.DarkGray, fontWeight = FontWeight.Medium)
                                        Text(timeStr, fontSize = 8.5.sp, color = Color.LightGray)
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    // Compose Post Dialog
    if (showCreatePostDialog) {
        var postContentText by remember { mutableStateOf("") }
        var postCategorySelect by remember { mutableStateOf("General") }

        AlertDialog(
            onDismissRequest = { showCreatePostDialog = false },
            title = {
                Text(
                    text = if (isBengali) "নতুন পোস্ট লিখুন" else "Create Community Post",
                    fontWeight = FontWeight.Bold,
                    fontSize = 14.sp
                )
            },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    OutlinedTextField(
                        value = postContentText,
                        onValueChange = { postContentText = it },
                        placeholder = { Text(if (isBengali) "আপনার চমৎকার অভিজ্ঞতা শেয়ার করুন..." else "Saturate your wellness story...") },
                        modifier = Modifier
                            .fillMaxWidth()
                            .heightIn(min = 100.dp),
                        textStyle = LocalTextStyle.current.copy(fontSize = 12.sp)
                    )

                    // Simple chips for category
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        listOf("General", "Mood Mental", "Nutrition").forEach { cat ->
                            val selected = postCategorySelect == cat
                            FilterChip(
                                selected = selected,
                                onClick = { postCategorySelect = cat },
                                label = { Text(cat, fontSize = 9.sp) }
                            )
                        }
                    }
                }
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        if (postContentText.isNotBlank()) {
                            defaultPosts.add(
                                0,
                                CommunityPost(
                                    id = "p_custom_${System.currentTimeMillis()}",
                                    author = if (isBengali) "আমি (আমার প্রোফাইল)" else "Me (My Profile)",
                                    role = if (isBengali) "সদস্য" else "Active Member",
                                    relativeTime = "Just now",
                                    content = postContentText,
                                    category = postCategorySelect,
                                    categoryColor = if (postCategorySelect == "Mood Mental") Color(0xFF673AB7) else if (postCategorySelect == "Nutrition") Color(0xFF2E7D32) else Color(0xFF607D8B),
                                    likesCount = 0,
                                    commentCount = 0,
                                    isLiked = false,
                                    postEmoji = "✍️"
                                )
                            )
                            showCreatePostDialog = false
                            Toast.makeText(context, if (isBengali) "পোস্ট সফল হয়েছে!" else "Post successfully published!", Toast.LENGTH_SHORT).show()
                        }
                    }
                ) {
                    Text(if (isBengali) "পোস্ট করুন" else "Publish")
                }
            },
            dismissButton = {
                TextButton(onClick = { showCreatePostDialog = false }) {
                    Text(if (isBengali) "বাতিল" else "Cancel")
                }
            }
        )
    }
}

// --- EMERGENCY HELP SCREEN ---
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EmergencyHelpScreen(
    isBengali: Boolean,
    viewModel: DietPlannerViewModel,
    onBack: () -> Unit
) {
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()
    var isEmergencyPulsing by remember { mutableStateOf(false) }
    var holdDurationMillis by remember { mutableStateOf(0L) }
    var scaleAnimAmount by remember { mutableStateOf(1f) }
    var showAddFriendDialog by remember { mutableStateOf(false) }

    // Pulsing circle scale animation
    val infiniteTransition = rememberInfiniteTransition(label = "pulse")
    val pulseScale by infiniteTransition.animateFloat(
        initialValue = 1f,
        targetValue = 1.25f,
        animationSpec = infiniteRepeatable(
            animation = tween(1200, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "pulse"
    )

    // Pre-configured speed dial list from diagram
    val customContacts = remember {
        mutableStateListOf(
            SpeedDialEmergencyContact("c1", "Carter Philips", "+91 3586 541 624", true, "👨"),
            SpeedDialEmergencyContact("c2", "Kaylynn Workman", "+91 3586 541 624", false, "👩"),
            SpeedDialEmergencyContact("c3", "Alfredo Dokidis", "+91 3586 541 624", true, "👴"),
            SpeedDialEmergencyContact("c4", "Livia Torff", "+91 3586 541 624", true, "👮")
        )
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = if (isBengali) "এসওএস জরুরি সহায়তা কেন্দ্র" else "SOS Emergency Help",
                        fontWeight = FontWeight.ExtraBold,
                        color = Color(0xFFD32F2F),
                        fontSize = 18.sp
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(imageVector = Icons.Default.ArrowBack, contentDescription = "Back", tint = Color(0xFFD32F2F))
                    }
                },
                actions = {
                    IconButton(onClick = { showAddFriendDialog = true }) {
                        Icon(imageVector = Icons.Default.PersonAdd, contentDescription = "Add Contact", tint = Color(0xFFD32F2F))
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = MaterialTheme.colorScheme.surface)
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .background(Color(0xFFFFFBFA))
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Main title and location specifications
            Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.fillMaxWidth()) {
                Text(
                    text = if (isBengali) "জরুরি সাহায্য প্রয়োজন?" else "Emergency Help\nNeeded ?",
                    fontWeight = FontWeight.Black,
                    fontSize = 26.sp,
                    color = Color(0xFFB71C1C),
                    textAlign = TextAlign.Center,
                    lineHeight = 32.sp
                )
                Spacer(modifier = Modifier.height(4.dp))
                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                    Icon(imageVector = Icons.Default.LocationOn, contentDescription = "Location", tint = Color(0xFFD32F2F), modifier = Modifier.size(14.dp))
                    Text(
                        text = "Current GPS: Dhaka Central Zone, Bangladesh (Active)",
                        fontSize = 10.sp,
                        color = Color.Gray,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            // Pulsing button calling trigger
            Box(
                modifier = Modifier
                    .size(240.dp)
                    .padding(8.dp),
                contentAlignment = Alignment.Center
            ) {
                // Background pulse rings
                Box(
                    modifier = Modifier
                        .size(190.dp)
                        .scale(pulseScale)
                        .clip(CircleShape)
                        .background(Color(0xFFFFCDD2).copy(alpha = 0.45f))
                )
                Box(
                    modifier = Modifier
                        .size(150.dp)
                        .scale(pulseScale * 0.9f)
                        .clip(CircleShape)
                        .background(Color(0xFFEF9A9A).copy(alpha = 0.35f))
                )

                // Actual Tappable emergency button
                Surface(
                    onClick = {
                        isEmergencyPulsing = true
                        coroutineScope.launch {
                            delay(1200)
                            isEmergencyPulsing = false
                            Toast.makeText(context, if (isBengali) "🚨 সিগনাল প্রেরিত! আমাদের ডিফেন্ডার ইউনিট কল করছে..." else "🚨 Distress signal sent! Emergency services dialing...", Toast.LENGTH_LONG).show()
                        }
                    },
                    modifier = Modifier
                        .size(120.dp)
                        .testTag("big_pulse_call_btn"),
                    shape = CircleShape,
                    color = Color(0xFFD32F2F),
                    shadowElevation = 8.dp
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center,
                        modifier = Modifier.padding(12.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Phone,
                            contentDescription = "Dial Phone",
                            tint = Color.White,
                            modifier = Modifier.size(42.dp)
                        )
                    }
                }
            }

            Text(
                text = if (isBengali) "কল করতে মাঝখানের বাটনে আলতো চাপুন" else "Tap or Hold the button to broadcast signal",
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold,
                color = Color.Gray
            )

            // Features badges row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                listOf(
                    Pair("🛡️ Loc Tracking", Color(0xFF2E7D32)),
                    Pair("⚡ One-Touch", Color(0xFF0288D1)),
                    Pair("🚨 SOS Alerts", Color(0xFFD32F2F))
                ).forEach { (badgeStr, colorVal) ->
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .border(1.dp, colorVal.copy(alpha = 0.3f), RoundedCornerShape(8.dp))
                            .background(colorVal.copy(alpha = 0.05f))
                            .padding(vertical = 6.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = badgeStr,
                            fontSize = 8.5.sp,
                            fontWeight = FontWeight.Bold,
                            color = colorVal
                        )
                    }
                }
            }

            // Quick contacts list (Grid/Column of friends and family)
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = if (isBengali) "আমার বিশ্বস্ত সহযোগী স্পিড-ডায়াল" else "Speed Dial Friends / Family",
                        fontWeight = FontWeight.Bold,
                        fontSize = 12.sp,
                        color = Color.DarkGray
                    )
                    TextButton(onClick = { showAddFriendDialog = true }) {
                        Text("+ Add New", fontSize = 11.sp, color = Color(0xFFD32F2F), fontWeight = FontWeight.Bold)
                    }
                }

                customContacts.forEach { contact ->
                    Card(
                        shape = RoundedCornerShape(12.dp),
                        colors = CardDefaults.cardColors(containerColor = Color.White),
                        border = BorderStroke(1.dp, Color(0xFFFFCDD2).copy(alpha = 0.4f))
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
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
                                        .size(36.dp)
                                        .clip(CircleShape)
                                        .background(Color(0xFFFFEBEE)),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(contact.emoji, fontSize = 18.sp)
                                }
                                Column {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Text(
                                            text = contact.name,
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 12.5.sp,
                                            color = Color(0xFF263238)
                                        )
                                        if (contact.isVerified) {
                                            Spacer(modifier = Modifier.width(4.dp))
                                            Icon(
                                                imageVector = Icons.Default.CheckCircle,
                                                contentDescription = "verified",
                                                tint = Color(0xFF2E7D32),
                                                modifier = Modifier.size(12.dp)
                                            )
                                        }
                                    }
                                    Text(contact.phone, fontSize = 9.5.sp, color = Color.Gray)
                                }
                            }

                            // Quick Actions Phone or Message
                            Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                                IconButton(
                                    onClick = {
                                        Toast.makeText(context, if (isBengali) "${contact.name} কে কল করা হচ্ছে..." else "Calling ${contact.name} instantly...", Toast.LENGTH_SHORT).show()
                                    },
                                    modifier = Modifier
                                        .size(28.dp)
                                        .background(Color(0xFFE8F5E9), CircleShape)
                                ) {
                                    Icon(imageVector = Icons.Default.Call, contentDescription = "call", tint = Color(0xFF2E7D32), modifier = Modifier.size(14.dp))
                                }

                                IconButton(
                                    onClick = {
                                        Toast.makeText(context, if (isBengali) "${contact.name} কে রেডি মেসেজ পাঠানো হয়েছে!" else "Signal text sent to ${contact.name}!", Toast.LENGTH_SHORT).show()
                                    },
                                    modifier = Modifier
                                        .size(28.dp)
                                        .background(Color(0xFFE3F2FD), CircleShape)
                                ) {
                                    Icon(imageVector = Icons.Default.Sms, contentDescription = "sms", tint = Color(0xFF0288D1), modifier = Modifier.size(14.dp))
                                }
                            }
                        }
                    }
                }
            }

            // Contact us details from bottom card of second image
            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                border = BorderStroke(1.dp, Color(0xFFECEFF1).copy(alpha = 0.8f)),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(14.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Text(
                        text = if (isBengali) "হেলপলাইন ও সাপোর্ট" else "Support Helpline (Contact Us)",
                        fontWeight = FontWeight.Bold,
                        fontSize = 11.5.sp,
                        color = Color(0xFF263238)
                    )

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Icon(imageVector = Icons.Default.SupportAgent, contentDescription = "helpline", tint = Color(0xFFD32F2F))
                        Column {
                            Text(text = if (isBengali) "ফোন (Phone Number)" else "Phone Support", fontSize = 10.sp, color = Color.Gray)
                            Text(text = "+91 98790 48212", fontWeight = FontWeight.Bold, fontSize = 11.5.sp, color = Color(0xFFD32F2F))
                        }
                    }

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Icon(imageVector = Icons.Default.Email, contentDescription = "email", tint = Color.Gray)
                        Column {
                            Text(text = "E-Mail Address", fontSize = 10.sp, color = Color.Gray)
                            Text(text = "support@hashkrio.com", fontWeight = FontWeight.Bold, fontSize = 11.5.sp, color = Color.DarkGray)
                        }
                    }
                }
            }
        }
    }

    if (showAddFriendDialog) {
        var friendNameText by remember { mutableStateOf("") }
        var friendPhoneText by remember { mutableStateOf("") }
        var selectedRoleEmoji by remember { mutableStateOf("👨") }

        AlertDialog(
            onDismissRequest = { showAddFriendDialog = false },
            title = {
                Text(
                    text = if (isBengali) "নতুন বিশ্বস্ত সহযোগী যুক্ত করুন" else "Add Emergency Contact",
                    fontWeight = FontWeight.Bold,
                    fontSize = 14.sp
                )
            },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    OutlinedTextField(
                        value = friendNameText,
                        onValueChange = { friendNameText = it },
                        label = { Text(if (isBengali) "সহযোগীর নাম" else "Contact Name") },
                        modifier = Modifier.fillMaxWidth(),
                        textStyle = LocalTextStyle.current.copy(fontSize = 12.sp)
                    )

                    OutlinedTextField(
                        value = friendPhoneText,
                        onValueChange = { friendPhoneText = it },
                        label = { Text(if (isBengali) "ফোন নম্বর" else "Phone Number") },
                        modifier = Modifier.fillMaxWidth(),
                        textStyle = LocalTextStyle.current.copy(fontSize = 12.sp)
                    )

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        listOf("👨", "👩", "👴", "👮", "🏥").forEach { emoji ->
                            val selected = selectedRoleEmoji == emoji
                            Box(
                                modifier = Modifier
                                    .size(36.dp)
                                    .clip(CircleShape)
                                    .background(if (selected) Color(0xFFFFCDD2) else Color(0xFFECEFF1))
                                    .clickable { selectedRoleEmoji = emoji },
                                contentAlignment = Alignment.Center
                            ) {
                                Text(emoji, fontSize = 16.sp)
                            }
                        }
                    }
                }
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        if (friendNameText.isNotBlank() && friendPhoneText.isNotBlank()) {
                            customContacts.add(
                                SpeedDialEmergencyContact(
                                    id = "c_custom_${System.currentTimeMillis()}",
                                    name = friendNameText,
                                    phone = friendPhoneText,
                                    isVerified = true,
                                    emoji = selectedRoleEmoji
                                )
                            )
                            showAddFriendDialog = false
                            Toast.makeText(context, if (isBengali) "সহযোগী সফলভাবে যুক্ত হয়েছে!" else "VIP emergency contact added!", Toast.LENGTH_SHORT).show()
                        }
                    }
                ) {
                    Text(if (isBengali) "যোগ করুন" else "Add VIP")
                }
            },
            dismissButton = {
                TextButton(onClick = { showAddFriendDialog = false }) {
                    Text(if (isBengali) "বাতিল" else "Cancel")
                }
            }
        )
    }
}
