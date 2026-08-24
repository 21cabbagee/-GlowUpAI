@file:OptIn(ExperimentalAnimationApi::class)

package com.glowup.ai

import android.graphics.Bitmap
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.*
import androidx.compose.animation.core.*
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
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.glowup.ai.ui.theme.GlowUpTheme
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.text.SimpleDateFormat
import java.util.*

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            GlowUpTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    GlowUpApp()
                }
            }
        }
    }
}

// Navigation destinations
enum class Screen {
    HOME, CAMERA, RESULT, HISTORY, PROFILE
}

// Data models
data class AnalysisHistory(
    val id: String = UUID.randomUUID().toString(),
    val date: Date = Date(),
    val score: Int,
    val skinClarity: Int,
    val texture: Int,
    val hydration: Int,
    val darkCircles: Int,
    val redness: Int,
    val photo: Bitmap? = null
)

@Composable
fun GlowUpApp() {
    var currentScreen by remember { mutableStateOf(Screen.HOME) }
    var capturedPhoto by remember { mutableStateOf<Bitmap?>(null) }
    var analysisHistory by remember { mutableStateOf<List<AnalysisHistory>>(emptyList()) }
    var showOnboarding by remember { mutableStateOf(true) }

    // Simulate saved state (in real app, load from SharedPreferences)
    LaunchedEffect(Unit) {
        delay(1000)
        showOnboarding = false
    }

    if (showOnboarding) {
        OnboardingFlow(onComplete = { showOnboarding = false })
    } else {
        MainContent(
            currentScreen = currentScreen,
            onNavigate = { screen -> currentScreen = screen },
            capturedPhoto = capturedPhoto,
            onPhotoTaken = { bitmap -> capturedPhoto = bitmap },
            analysisHistory = analysisHistory,
            onAnalysisComplete = { analysis ->
                analysisHistory = listOf(analysis) + analysisHistory
            }
        )
    }
}

@Composable
fun MainContent(
    currentScreen: Screen,
    onNavigate: (Screen) -> Unit,
    capturedPhoto: Bitmap?,
    onPhotoTaken: (Bitmap) -> Unit,
    analysisHistory: List<AnalysisHistory>,
    onAnalysisComplete: (AnalysisHistory) -> Unit
) {
    Scaffold(
        bottomBar = {
            if (currentScreen !in listOf(Screen.CAMERA, Screen.RESULT)) {
                BottomNavigationBar(
                    currentScreen = currentScreen,
                    onNavigate = onNavigate
                )
            }
        }
    ) { padding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            AnimatedContent(
                targetState = currentScreen,
                transitionSpec = {
                    fadeIn(animationSpec = tween(300)) with
                            fadeOut(animationSpec = tween(300))
                }
            ) { screen ->
                when (screen) {
                    Screen.HOME -> HomeScreenNew(
                        onTakeSelfieClick = { onNavigate(Screen.CAMERA) },
                        onChooseFromGallery = { bitmap ->
                            onPhotoTaken(bitmap)
                            onNavigate(Screen.RESULT)
                        },
                        recentAnalysis = analysisHistory.firstOrNull()
                    )
                    Screen.CAMERA -> CameraScreen(
                        onPhotoTaken = { bitmap ->
                            onPhotoTaken(bitmap)
                            onNavigate(Screen.RESULT)
                        },
                        onBack = { onNavigate(Screen.HOME) }
                    )
                    Screen.RESULT -> ResultScreenNew(
                        photo = capturedPhoto,
                        onRetake = { onNavigate(Screen.CAMERA) },
                        onBack = { onNavigate(Screen.HOME) },
                        onAnalysisComplete = onAnalysisComplete
                    )
                    Screen.HISTORY -> HistoryScreen(
                        history = analysisHistory,
                        onItemClick = { /* Navigate to detail */ }
                    )
                    Screen.PROFILE -> ProfileScreen(
                        history = analysisHistory,
                        onEditProfile = { /* Edit profile */ }
                    )
                }
            }
        }
    }
}

@Composable
fun BottomNavigationBar(
    currentScreen: Screen,
    onNavigate: (Screen) -> Unit
) {
    NavigationBar(
        containerColor = Color.White,
        tonalElevation = 8.dp
    ) {
        NavigationBarItem(
            icon = { Icon(Icons.Default.Home, contentDescription = "Home") },
            label = { Text("Home") },
            selected = currentScreen == Screen.HOME,
            onClick = { onNavigate(Screen.HOME) },
            colors = NavigationBarItemDefaults.colors(
                selectedIconColor = Color(0xFF7E57C2),
                selectedTextColor = Color(0xFF7E57C2),
                indicatorColor = Color(0xFFE8EAF6)
            )
        )
        NavigationBarItem(
            icon = { Icon(Icons.Default.DateRange, contentDescription = "History") },
            label = { Text("History") },
            selected = currentScreen == Screen.HISTORY,
            onClick = { onNavigate(Screen.HISTORY) },
            colors = NavigationBarItemDefaults.colors(
                selectedIconColor = Color(0xFF7E57C2),
                selectedTextColor = Color(0xFF7E57C2),
                indicatorColor = Color(0xFFE8EAF6)
            )
        )
        NavigationBarItem(
            icon = { Icon(Icons.Default.Person, contentDescription = "Profile") },
            label = { Text("Profile") },
            selected = currentScreen == Screen.PROFILE,
            onClick = { onNavigate(Screen.PROFILE) },
            colors = NavigationBarItemDefaults.colors(
                selectedIconColor = Color(0xFF7E57C2),
                selectedTextColor = Color(0xFF7E57C2),
                indicatorColor = Color(0xFFE8EAF6)
            )
        )
    }
}

// ONBOARDING FLOW
@Composable
fun OnboardingFlow(onComplete: () -> Unit) {
    var currentPage by remember { mutableStateOf(0) }
    val pages = listOf(
        OnboardingPage(
            title = "Welcome to GlowUp",
            description = "Track your skin health journey with AI-powered analysis",
            icon = "✨"
        ),
        OnboardingPage(
            title = "Daily Selfie Tracking",
            description = "Take a daily selfie and get instant AI analysis of your skin health",
            icon = "📸"
        ),
        OnboardingPage(
            title = "Detailed Insights",
            description = "Get medical-grade breakdown of clarity, texture, hydration, and more",
            icon = "📊"
        )
    )

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    colors = listOf(Color(0xFFE8EAF6), Color(0xFFFFFFFF))
                )
            )
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Spacer(modifier = Modifier.height(60.dp))

            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.Center
            ) {
                Text(
                    text = pages[currentPage].icon,
                    fontSize = 80.sp
                )
                Spacer(modifier = Modifier.height(32.dp))
                Text(
                    text = pages[currentPage].title,
                    fontSize = 28.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF5E35B1),
                    textAlign = TextAlign.Center
                )
                Spacer(modifier = Modifier.height(16.dp))
                Text(
                    text = pages[currentPage].description,
                    fontSize = 16.sp,
                    color = Color(0xFF757575),
                    textAlign = TextAlign.Center,
                    modifier = Modifier.padding(horizontal = 32.dp)
                )
            }

            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Row(
                    horizontalArrangement = Arrangement.Center,
                    modifier = Modifier.padding(bottom = 24.dp)
                ) {
                    pages.indices.forEach { index ->
                        Box(
                            modifier = Modifier
                                .padding(horizontal = 4.dp)
                                .size(if (index == currentPage) 24.dp else 8.dp, 8.dp)
                                .clip(RoundedCornerShape(4.dp))
                                .background(
                                    if (index == currentPage) Color(0xFF7E57C2)
                                    else Color(0xFFE0E0E0)
                                )
                        )
                    }
                }

                Button(
                    onClick = {
                        if (currentPage < pages.size - 1) {
                            currentPage++
                        } else {
                            onComplete()
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFF7E57C2)
                    ),
                    shape = RoundedCornerShape(16.dp)
                ) {
                    Text(
                        text = if (currentPage < pages.size - 1) "Next" else "Get Started",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold
                    )
                }

                if (currentPage < pages.size - 1) {
                    TextButton(
                        onClick = onComplete,
                        modifier = Modifier.padding(top = 8.dp)
                    ) {
                        Text("Skip", color = Color(0xFF757575))
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}

data class OnboardingPage(
    val title: String,
    val description: String,
    val icon: String
)

// NEW HOME SCREEN - Cal.ai inspired
@Composable
fun HomeScreenNew(
    onTakeSelfieClick: () -> Unit,
    onChooseFromGallery: (Bitmap) -> Unit,
    recentAnalysis: AnalysisHistory?,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val imagePickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri ->
        uri?.let {
            try {
                val bitmap = if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.P) {
                    val source = android.graphics.ImageDecoder.createSource(context.contentResolver, it)
                    android.graphics.ImageDecoder.decodeBitmap(source)
                } else {
                    @Suppress("DEPRECATION")
                    android.provider.MediaStore.Images.Media.getBitmap(context.contentResolver, it)
                }
                onChooseFromGallery(bitmap)
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(Color(0xFFFAFAFA)),
        contentPadding = PaddingValues(20.dp),
        verticalArrangement = Arrangement.spacedBy(20.dp)
    ) {
        item {
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "GlowUp",
                fontSize = 32.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF5E35B1)
            )
            Text(
                text = "Your AI Skin Coach",
                fontSize = 14.sp,
                color = Color(0xFF9E9E9E),
                modifier = Modifier.padding(top = 4.dp)
            )
        }

        // Recent Score Card
        if (recentAnalysis != null) {
            item {
                RecentScoreCard(analysis = recentAnalysis)
            }
        }

        // Quick Actions
        item {
            Text(
                text = "Quick Actions",
                fontSize = 18.sp,
                fontWeight = FontWeight.SemiBold,
                color = Color(0xFF212121),
                modifier = Modifier.padding(top = 8.dp, bottom = 8.dp)
            )
        }

        item {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(140.dp)
                    .clickable(onClick = onTakeSelfieClick),
                shape = RoundedCornerShape(20.dp),
                elevation = CardDefaults.cardElevation(0.dp),
                colors = CardDefaults.cardColors(Color.White)
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(
                            Brush.horizontalGradient(
                                colors = listOf(Color(0xFF7E57C2), Color(0xFF9575CD))
                            )
                        )
                        .padding(24.dp),
                    contentAlignment = Alignment.CenterStart
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column {
                            Text(
                                "Take Today's Selfie",
                                fontSize = 22.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.White
                            )
                            Text(
                                "Quick AI analysis in seconds",
                                fontSize = 14.sp,
                                color = Color.White.copy(alpha = 0.9f),
                                modifier = Modifier.padding(top = 4.dp)
                            )
                        }
                        Text("📸", fontSize = 40.sp)
                    }
                }
            }
        }

        item {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(120.dp)
                    .clickable { imagePickerLauncher.launch("image/*") },
                shape = RoundedCornerShape(20.dp),
                elevation = CardDefaults.cardElevation(0.dp),
                colors = CardDefaults.cardColors(Color.White)
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(24.dp),
                    contentAlignment = Alignment.CenterStart
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column {
                            Text(
                                "Upload from Gallery",
                                fontSize = 18.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = Color(0xFF212121)
                            )
                            Text(
                                "Analyze existing photos",
                                fontSize = 13.sp,
                                color = Color(0xFF757575),
                                modifier = Modifier.padding(top = 4.dp)
                            )
                        }
                        Icon(
                            Icons.Default.UploadFile,
                            contentDescription = null,
                            tint = Color(0xFF7E57C2),
                            modifier = Modifier.size(32.dp)
                        )
                    }
                }
            }
        }

        // Daily Tip Card
        item {
            DailyTipCard()
        }

        item {
            Spacer(modifier = Modifier.height(80.dp))
        }
    }
}

@Composable
fun RecentScoreCard(analysis: AnalysisHistory) {
    val dateFormat = remember { SimpleDateFormat("MMM dd, yyyy", Locale.getDefault()) }

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(Color.White),
        elevation = CardDefaults.cardElevation(0.dp)
    ) {
        Column(
            modifier = Modifier.padding(20.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        "Latest Score",
                        fontSize = 14.sp,
                        color = Color(0xFF9E9E9E)
                    )
                    Text(
                        dateFormat.format(analysis.date),
                        fontSize = 12.sp,
                        color = Color(0xFFBDBDBD)
                    )
                }

                Box(
                    contentAlignment = Alignment.Center,
                    modifier = Modifier.size(80.dp)
                ) {
                    CircularProgressIndicator(
                        progress = analysis.score / 100f,
                        modifier = Modifier.fillMaxSize(),
                        color = when {
                            analysis.score >= 75 -> Color(0xFF4CAF50)
                            analysis.score >= 50 -> Color(0xFFFF9800)
                            else -> Color(0xFFF44336)
                        },
                        strokeWidth = 8.dp,
                        trackColor = Color(0xFFEEEEEE)
                    )
                    Text(
                        "${analysis.score}",
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Bold,
                        color = when {
                            analysis.score >= 75 -> Color(0xFF4CAF50)
                            analysis.score >= 50 -> Color(0xFFFF9800)
                            else -> Color(0xFFF44336)
                        }
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))
            HorizontalDivider(color = Color(0xFFF5F5F5))
            Spacer(modifier = Modifier.height(16.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                QuickMetric("Clarity", analysis.skinClarity, 10)
                QuickMetric("Texture", analysis.texture, 10)
                QuickMetric("Hydration", analysis.hydration, 10)
            }
        }
    }
}

@Composable
fun QuickMetric(label: String, value: Int, maxValue: Int) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(
            "$value/$maxValue",
            fontSize = 16.sp,
            fontWeight = FontWeight.Bold,
            color = when {
                value >= (maxValue * 0.75) -> Color(0xFF4CAF50)
                value >= (maxValue * 0.50) -> Color(0xFFFF9800)
                else -> Color(0xFFF44336)
            }
        )
        Text(
            label,
            fontSize = 12.sp,
            color = Color(0xFF9E9E9E)
        )
    }
}

@Composable
fun DailyTipCard() {
    val tips = remember {
        listOf(
            "💧 Stay hydrated! Drink 8 glasses of water daily.",
            "🌙 Get 7-8 hours of sleep for better skin recovery.",
            "☀️ Always apply SPF before going outdoors.",
            "🥗 Eat foods rich in Vitamin C for glowing skin.",
            "🧘 Reduce stress with meditation and exercise."
        )
    }
    val todayTip = remember { tips.random() }

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(Color(0xFFFFF8E1)),
        elevation = CardDefaults.cardElevation(0.dp)
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text("💡", fontSize = 24.sp)
            Spacer(modifier = Modifier.width(12.dp))
            Column {
                Text(
                    "Daily Tip",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFFF57C00)
                )
                Text(
                    todayTip,
                    fontSize = 13.sp,
                    color = Color(0xFF424242),
                    modifier = Modifier.padding(top = 4.dp)
                )
            }
        }
    }
}

// HISTORY SCREEN
@Composable
fun HistoryScreen(
    history: List<AnalysisHistory>,
    onItemClick: (AnalysisHistory) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFFAFAFA))
    ) {
        Surface(
            modifier = Modifier.fillMaxWidth(),
            color = Color.White,
            shadowElevation = 2.dp
        ) {
            Column(modifier = Modifier.padding(20.dp)) {
                Text(
                    "Your Journey",
                    fontSize = 28.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF212121)
                )
                Text(
                    "${history.size} analysis${if (history.size != 1) "es" else ""} recorded",
                    fontSize = 14.sp,
                    color = Color(0xFF9E9E9E),
                    modifier = Modifier.padding(top = 4.dp)
                )
            }
        }

        if (history.isEmpty()) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier.padding(40.dp)
                ) {
                    Text("📊", fontSize = 64.sp)
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(
                        "No analyses yet",
                        fontSize = 20.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = Color(0xFF212121)
                    )
                    Text(
                        "Take your first selfie to start tracking",
                        fontSize = 14.sp,
                        color = Color(0xFF9E9E9E),
                        textAlign = TextAlign.Center,
                        modifier = Modifier.padding(top = 8.dp)
                    )
                }
            }
        } else {
            LazyColumn(
                contentPadding = PaddingValues(20.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(history) { analysis ->
                    HistoryCard(analysis = analysis, onClick = { onItemClick(analysis) })
                }
                item {
                    Spacer(modifier = Modifier.height(80.dp))
                }
            }
        }
    }
}

@Composable
fun HistoryCard(analysis: AnalysisHistory, onClick: () -> Unit) {
    val dateFormat = remember { SimpleDateFormat("MMM dd, yyyy • HH:mm", Locale.getDefault()) }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(Color.White),
        elevation = CardDefaults.cardElevation(0.dp)
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(60.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(
                        when {
                            analysis.score >= 75 -> Color(0xFFE8F5E9)
                            analysis.score >= 50 -> Color(0xFFFFF3E0)
                            else -> Color(0xFFFFEBEE)
                        }
                    ),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    "${analysis.score}",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = when {
                        analysis.score >= 75 -> Color(0xFF4CAF50)
                        analysis.score >= 50 -> Color(0xFFFF9800)
                        else -> Color(0xFFF44336)
                    }
                )
            }

            Spacer(modifier = Modifier.width(16.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    dateFormat.format(analysis.date),
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Medium,
                    color = Color(0xFF212121)
                )
                Text(
                    when {
                        analysis.score >= 75 -> "Excellent skin health"
                        analysis.score >= 50 -> "Good condition"
                        else -> "Needs attention"
                    },
                    fontSize = 12.sp,
                    color = Color(0xFF9E9E9E),
                    modifier = Modifier.padding(top = 4.dp)
                )
            }

            Icon(
                Icons.Default.ChevronRight,
                contentDescription = null,
                tint = Color(0xFFBDBDBD),
                modifier = Modifier.size(20.dp)
            )
        }
    }
}

// PROFILE SCREEN
@Composable
fun ProfileScreen(
    history: List<AnalysisHistory>,
    onEditProfile: () -> Unit
) {
    val avgScore = if (history.isNotEmpty()) history.map { it.score }.average().toInt() else 0
    val streak = 7 // Mock streak data
    val totalAnalyses = history.size

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFFAFAFA)),
        contentPadding = PaddingValues(20.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 20.dp)
            ) {
                Box(
                    modifier = Modifier
                        .size(100.dp)
                        .clip(CircleShape)
                        .background(
                            Brush.linearGradient(
                                colors = listOf(Color(0xFF7E57C2), Color(0xFF9575CD))
                            )
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        "👤",
                        fontSize = 48.sp
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                Text(
                    "Your Profile",
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF212121)
                )
                Text(
                    "Member since Aug 2026",
                    fontSize = 14.sp,
                    color = Color(0xFF9E9E9E),
                    modifier = Modifier.padding(top = 4.dp)
                )
            }
        }

        // Stats Cards
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                StatCard(
                    label = "Avg Score",
                    value = "$avgScore",
                    icon = "⭐",
                    modifier = Modifier.weight(1f)
                )
                StatCard(
                    label = "Day Streak",
                    value = "$streak",
                    icon = "🔥",
                    modifier = Modifier.weight(1f)
                )
                StatCard(
                    label = "Total",
                    value = "$totalAnalyses",
                    icon = "📊",
                    modifier = Modifier.weight(1f)
                )
            }
        }

        // Settings Section
        item {
            Text(
                "Settings",
                fontSize = 18.sp,
                fontWeight = FontWeight.SemiBold,
                color = Color(0xFF212121),
                modifier = Modifier.padding(top = 16.dp, bottom = 8.dp)
            )
        }

        item {
            SettingsCard {
                SettingsItem(icon = Icons.Default.Notifications, label = "Notifications", onClick = {})
                HorizontalDivider(color = Color(0xFFF5F5F5))
                SettingsItem(icon = Icons.Default.Security, label = "Privacy", onClick = {})
                HorizontalDivider(color = Color(0xFFF5F5F5))
                SettingsItem(icon = Icons.Default.Language, label = "Language", onClick = {})
                HorizontalDivider(color = Color(0xFFF5F5F5))
                SettingsItem(icon = Icons.Default.Help, label = "Help & Support", onClick = {})
            }
        }

        // About Section
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(Color.White),
                elevation = CardDefaults.cardElevation(0.dp)
            ) {
                Column(
                    modifier = Modifier.padding(20.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        "GlowUp v1.0.0",
                        fontSize = 12.sp,
                        color = Color(0xFF9E9E9E)
                    )
                    Text(
                        "Made with ❤️ for better skin",
                        fontSize = 12.sp,
                        color = Color(0xFF9E9E9E),
                        modifier = Modifier.padding(top = 4.dp)
                    )
                }
            }
        }

        item {
            Spacer(modifier = Modifier.height(80.dp))
        }
    }
}

@Composable
fun StatCard(
    label: String,
    value: String,
    icon: String,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier,
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(Color.White),
        elevation = CardDefaults.cardElevation(0.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(icon, fontSize = 28.sp)
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                value,
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF212121)
            )
            Text(
                label,
                fontSize = 12.sp,
                color = Color(0xFF9E9E9E),
                modifier = Modifier.padding(top = 4.dp)
            )
        }
    }
}

@Composable
fun SettingsCard(content: @Composable ColumnScope.() -> Unit) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(Color.White),
        elevation = CardDefaults.cardElevation(0.dp)
    ) {
        Column(
            modifier = Modifier.padding(vertical = 8.dp),
            content = content
        )
    }
}

@Composable
fun SettingsItem(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    label: String,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(horizontal = 20.dp, vertical = 16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            icon,
            contentDescription = null,
            tint = Color(0xFF7E57C2),
            modifier = Modifier.size(24.dp)
        )
        Spacer(modifier = Modifier.width(16.dp))
        Text(
            label,
            fontSize = 15.sp,
            color = Color(0xFF212121),
            modifier = Modifier.weight(1f)
        )
        Icon(
            Icons.Default.ChevronRight,
            contentDescription = null,
            tint = Color(0xFFBDBDBD),
            modifier = Modifier.size(20.dp)
        )
    }
}

// RESULT SCREEN - Enhanced
@Composable
fun ResultScreenNew(
    photo: Bitmap?,
    onRetake: () -> Unit,
    onBack: () -> Unit,
    onAnalysisComplete: (AnalysisHistory) -> Unit,
    modifier: Modifier = Modifier
) {
    var isAnalyzing by remember { mutableStateOf(false) }
    var analysisResult by remember { mutableStateOf<SkinAnalysisResult?>(null) }
    var errorMessage by remember { mutableStateOf<String?>(null) }
    var showCelebration by remember { mutableStateOf(false) }

    val currentScore = analysisResult?.skinScore ?: 0
    val scoreColor = when {
        currentScore >= 75 -> Color(0xFF4CAF50)
        currentScore >= 50 -> Color(0xFFFF9800)
        else -> Color(0xFFF44336)
    }

    // Celebration animation when score appears
    LaunchedEffect(analysisResult) {
        if (analysisResult != null) {
            showCelebration = true
            delay(1500)
            showCelebration = false

            // Save to history
            onAnalysisComplete(
                AnalysisHistory(
                    score = currentScore,
                    skinClarity = 8,
                    texture = 7,
                    hydration = 9,
                    darkCircles = 6,
                    redness = 8,
                    photo = photo
                )
            )
        }
    }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(Color(0xFFFAFAFA))
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(20.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Back button
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Start
            ) {
                IconButton(onClick = onBack) {
                    Icon(
                        Icons.Default.ArrowBack,
                        contentDescription = "Back",
                        tint = Color(0xFF212121)
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Photo
            photo?.let {
                Card(
                    modifier = Modifier.size(240.dp),
                    shape = RoundedCornerShape(24.dp),
                    elevation = CardDefaults.cardElevation(8.dp)
                ) {
                    Image(
                        bitmap = it.asImageBitmap(),
                        contentDescription = "Your selfie",
                        modifier = Modifier.fillMaxSize()
                    )
                }
            }

            Spacer(modifier = Modifier.height(32.dp))

            when {
                analysisResult != null -> {
                    // Score animation
                    AnimatedVisibility(
                        visible = !showCelebration,
                        enter = fadeIn() + scaleIn()
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text(
                                "Your Skin Score",
                                fontSize = 20.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = Color(0xFF212121)
                            )

                            Spacer(modifier = Modifier.height(24.dp))

                            Box(
                                contentAlignment = Alignment.Center,
                                modifier = Modifier.size(180.dp)
                            ) {
                                CircularProgressIndicator(
                                    progress = currentScore / 100f,
                                    modifier = Modifier.fillMaxSize(),
                                    color = scoreColor,
                                    strokeWidth = 16.dp,
                                    trackColor = Color(0xFFEEEEEE),
                                    strokeCap = StrokeCap.Round
                                )
                                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                    Text(
                                        "$currentScore",
                                        fontSize = 56.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = scoreColor
                                    )
                                    Text(
                                        "/100",
                                        fontSize = 18.sp,
                                        color = Color(0xFF9E9E9E)
                                    )
                                }
                            }

                            Spacer(modifier = Modifier.height(16.dp))

                            Text(
                                when {
                                    currentScore >= 75 -> "✨ Excellent!"
                                    currentScore >= 50 -> "👍 Good"
                                    else -> "💪 Keep Going"
                                },
                                fontSize = 20.sp,
                                fontWeight = FontWeight.Medium,
                                color = Color(0xFF212121)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(32.dp))

                    // Detailed metrics
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(20.dp),
                        colors = CardDefaults.cardColors(Color.White),
                        elevation = CardDefaults.cardElevation(0.dp)
                    ) {
                        Column(modifier = Modifier.padding(24.dp)) {
                            Text(
                                "Detailed Analysis",
                                fontSize = 18.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF212121)
                            )
                            Spacer(modifier = Modifier.height(20.dp))

                            SkinMetricRow("Skin Clarity", 8, 10)
                            Spacer(modifier = Modifier.height(16.dp))

                            SkinMetricRow("Texture Quality", 7, 10)
                            Spacer(modifier = Modifier.height(16.dp))

                            SkinMetricRow("Hydration Level", 9, 10)
                            Spacer(modifier = Modifier.height(16.dp))

                            SkinMetricRow("Dark Circles", 6, 10)
                            Spacer(modifier = Modifier.height(16.dp))

                            SkinMetricRow("Redness Control", 8, 10)
                            Spacer(modifier = Modifier.height(16.dp))

                            HorizontalDivider(color = Color(0xFFF5F5F5))
                            Spacer(modifier = Modifier.height(16.dp))

                            SkinMetricRow("Overall Health", currentScore, 100)
                        }
                    }

                    Spacer(modifier = Modifier.height(24.dp))
                }

                isAnalyzing -> {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier.padding(vertical = 60.dp)
                    ) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(64.dp),
                            color = Color(0xFF7E57C2),
                            strokeWidth = 6.dp
                        )
                        Spacer(modifier = Modifier.height(24.dp))
                        Text(
                            "Analyzing your skin...",
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Medium,
                            color = Color(0xFF212121)
                        )
                        Text(
                            "This may take a few seconds",
                            fontSize = 14.sp,
                            color = Color(0xFF9E9E9E),
                            modifier = Modifier.padding(top = 8.dp)
                        )
                    }
                }

                else -> {
                    Button(
                        onClick = {
                            isAnalyzing = true
                            errorMessage = null
                            GlobalScope.launch {
                                photo?.let { bitmap ->
                                    val apiService = ApiService()
                                    val result = apiService.analyzePhoto(bitmap)
                                    withContext(Dispatchers.Main) {
                                        isAnalyzing = false
                                        if (result.isSuccess) {
                                            analysisResult = result.getOrNull()
                                        } else {
                                            errorMessage = result.exceptionOrNull()?.message
                                        }
                                    }
                                }
                            }
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(60.dp),
                        colors = ButtonDefaults.buttonColors(Color(0xFF7E57C2)),
                        shape = RoundedCornerShape(16.dp),
                        elevation = ButtonDefaults.buttonElevation(4.dp)
                    ) {
                        Text(
                            "✨ Analyze My Skin",
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }

            errorMessage?.let {
                Spacer(modifier = Modifier.height(16.dp))
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(Color(0xFFFFEBEE)),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text(
                        "⚠️ $it",
                        fontSize = 14.sp,
                        color = Color(0xFFC62828),
                        modifier = Modifier.padding(16.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                OutlinedButton(
                    onClick = {
                        analysisResult = null
                        errorMessage = null
                        onRetake()
                    },
                    modifier = Modifier.weight(1f).height(52.dp),
                    shape = RoundedCornerShape(12.dp),
                    border = BorderStroke(1.dp, Color(0xFF7E57C2))
                ) {
                    Icon(
                        Icons.Default.CameraAlt,
                        contentDescription = null,
                        modifier = Modifier.size(20.dp),
                        tint = Color(0xFF7E57C2)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Retake", fontSize = 16.sp, color = Color(0xFF7E57C2))
                }
                Button(
                    onClick = onBack,
                    modifier = Modifier.weight(1f).height(52.dp),
                    colors = ButtonDefaults.buttonColors(Color(0xFF4CAF50)),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Icon(
                        Icons.Default.Home,
                        contentDescription = null,
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Home", fontSize = 16.sp)
                }
            }

            Spacer(modifier = Modifier.height(100.dp))
        }

        // Celebration overlay
        if (showCelebration) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.Black.copy(alpha = 0.3f)),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    "🎉",
                    fontSize = 100.sp,
                    modifier = Modifier.scale(
                        animateFloatAsState(
                            targetValue = 1.5f,
                            animationSpec = spring(
                                dampingRatio = Spring.DampingRatioMediumBouncy,
                                stiffness = Spring.StiffnessLow
                            )
                        ).value
                    )
                )
            }
        }
    }
}

@Composable
fun SkinMetricRow(label: String, score: Int, maxScore: Int) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            label,
            fontSize = 15.sp,
            color = Color(0xFF424242),
            modifier = Modifier.weight(1.2f)
        )

        Box(
            modifier = Modifier
                .weight(2f)
                .height(8.dp)
                .clip(RoundedCornerShape(4.dp))
                .background(Color(0xFFF5F5F5))
        ) {
            Box(
                modifier = Modifier
                    .fillMaxHeight()
                    .fillMaxWidth((score.toFloat() / maxScore))
                    .background(
                        when {
                            score >= (maxScore * 0.75) -> Color(0xFF4CAF50)
                            score >= (maxScore * 0.50) -> Color(0xFFFF9800)
                            else -> Color(0xFFF44336)
                        }
                    )
            )
        }

        Text(
            "$score/$maxScore",
            fontSize = 14.sp,
            fontWeight = FontWeight.Bold,
            color = when {
                score >= (maxScore * 0.75) -> Color(0xFF4CAF50)
                score >= (maxScore * 0.50) -> Color(0xFFFF9800)
                else -> Color(0xFFF44336)
            },
            modifier = Modifier.width(70.dp),
            textAlign = TextAlign.End
        )
    }
}
