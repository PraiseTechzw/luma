package app.lumalabs.luma.ui.screen

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Settings
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
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import app.lumalabs.luma.ui.navigation.Screen
import app.lumalabs.luma.ui.theme.DarkBackground
import app.lumalabs.luma.ui.theme.DarkSurfaceVariant
import app.lumalabs.luma.ui.theme.PrimaryAccent
import app.lumalabs.luma.ui.component.LumaBackground
import app.lumalabs.luma.ui.viewmodel.DashboardViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DashboardScreen(
    navController: NavController,
    viewModel: DashboardViewModel = hiltViewModel()
) {
    val reclaimableSize by viewModel.reclaimableSize.collectAsState()
    val isScanning by viewModel.isScanning.collectAsState()
    val similarSize by viewModel.similarSize.collectAsState()
    val screenshotsCount by viewModel.screenshotsCount.collectAsState()
    val chatSize by viewModel.chatSize.collectAsState()

    LumaBackground {
        Scaffold(
            containerColor = Color.Transparent,
            topBar = {
                TopAppBar(
                    title = { Text("Luma", style = MaterialTheme.typography.displayLarge) },
                    actions = {
                        Surface(
                            modifier = Modifier.padding(end = 16.dp),
                            shape = CircleShape,
                            color = Color(0xFF7B61FF).copy(alpha = 0.2f),
                            border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFF7B61FF).copy(alpha = 0.5f))
                        ) {
                            Text(
                                "AI Ready",
                                modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                                style = MaterialTheme.typography.labelMedium,
                                color = Color(0xFFA599FA)
                            )
                        }
                    },
                    colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.Transparent)
                )
            }
        ) { padding ->
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
                    .padding(horizontal = 16.dp)
            ) {
                // Hero Card
                item {
                    HeroCard(
                        reclaimableSizeDisplay = viewModel.formatSizeForDisplay(reclaimableSize),
                        onReviewClick = { viewModel.triggerScan() } // For now trigger scan
                    )
                    Spacer(modifier = Modifier.height(24.dp))
                }

                // Stats Row
                item {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        StatCard("Similar groups", similarSize, Color(0xFF6A97F2), Modifier.weight(1f))
                        StatCard("Screenshots", screenshotsCount, Color(0xFFFABB3D), Modifier.weight(1f))
                        StatCard("Chat media", chatSize, Color(0xFF4ED6C0), Modifier.weight(1f))
                    }
                    Spacer(modifier = Modifier.height(24.dp))
                }

                // Categories header
                item {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text("Categories to clean", style = MaterialTheme.typography.titleMedium)
                        TextButton(onClick = { }) {
                            Text("See all", color = Color(0xFF7B61FF))
                        }
                    }
                }

                // List Items
                item {
                    CategoryListItem(
                        title = "Similar & Duplicate Photos",
                        subtitle = "$similarSize groups · AI scored",
                        size = viewModel.formatSizeForDisplay(reclaimableSize * 2 / 3),
                        icon = Icons.Default.Search,
                        iconColor = Color(0xFF3B3B7A),
                        onclick = { navController.navigate(Screen.SimilarPhotos.route) }
                    )
                }
                item {
                    CategoryListItem(
                        title = "Screenshots",
                        subtitle = "$screenshotsCount files · oldest from 2021",
                        size = viewModel.formatSizeForDisplay(reclaimableSize / 6),
                        icon = Icons.Default.Info,
                        iconColor = Color(0xFF323D1B),
                        onclick = { navController.navigate(Screen.Screenshots.route) }
                    )
                }
                item {
                    CategoryListItem(
                        title = "Chat Media",
                        subtitle = "WhatsApp · Telegram · Instagram",
                        size = viewModel.formatSizeForDisplay(reclaimableSize / 6),
                        icon = Icons.Default.Email,
                        iconColor = Color(0xFF3B281B),
                        onclick = { navController.navigate(Screen.ChatMedia.route) }
                    )
                }
                
                item { Spacer(modifier = Modifier.height(24.dp)) }
            }
        }
    }
}

@Composable
fun HeroCard(reclaimableSizeDisplay: String, onReviewClick: () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth(),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = Color.Transparent)
    ) {
        Box(
            modifier = Modifier
                .background(
                    Brush.verticalGradient(
                        listOf(Color(0xFF37367B), Color(0xFF24234B))
                    )
                )
                .padding(24.dp)
        ) {
            // Background Decoration
            Canvas(modifier = Modifier.fillMaxSize()) {
                drawCircle(
                    color = Color.White.copy(alpha = 0.05f),
                    radius = 300f,
                    center = Offset(size.width + 100f, -100f)
                )
            }

            Column {
                Text(
                    "LAST SCAN · TODAY 8:42 AM",
                    style = MaterialTheme.typography.labelSmall,
                    color = Color.White.copy(alpha = 0.6f),
                    letterSpacing = 1.sp
                )
                Spacer(modifier = Modifier.height(12.dp))
                Text(
                    reclaimableSizeDisplay,
                    style = MaterialTheme.typography.displayLarge.copy(fontSize = 48.sp),
                    color = Color.White
                )
                Text(
                    "reclaimable across 3 categories",
                    style = MaterialTheme.typography.bodyMedium,
                    color = Color(0xFFA599FA)
                )
                Spacer(modifier = Modifier.height(24.dp))
                
                // Progress
                LinearProgressIndicator(
                    progress = 0.6f,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(8.dp)
                        .clip(CircleShape),
                    color = Color(0xFFA599FA),
                    trackColor = Color.White.copy(alpha = 0.1f)
                )

                Spacer(modifier = Modifier.height(24.dp))

                Button(
                    onClick = onReviewClick,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(64.dp),
                    shape = RoundedCornerShape(20.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF7B61FF))
                ) {
                    Text("Review & Clean →", style = MaterialTheme.typography.titleMedium)
                }
            }
        }
    }
}

@Composable
fun StatCard(title: String, value: String, accentColor: Color, modifier: Modifier = Modifier) {
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(24.dp))
            .background(Color(0xFF14141A))
            .border(1.dp, Color.White.copy(alpha = 0.05f), RoundedCornerShape(24.dp))
            .padding(16.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(value, style = MaterialTheme.typography.titleLarge, color = accentColor)
            Spacer(modifier = Modifier.height(4.dp))
            Text(title, style = MaterialTheme.typography.bodySmall, color = Color.White.copy(alpha = 0.4f), fontSize = 10.sp)
        }
    }
}

@Composable
fun CategoryListItem(title: String, subtitle: String, size: String, icon: ImageVector, iconColor: Color, onclick: () -> Unit) {
    Surface(
        onClick = onclick,
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        shape = RoundedCornerShape(20.dp),
        color = Color(0xFF14141A),
        border = androidx.compose.foundation.BorderStroke(1.dp, Color.White.copy(alpha = 0.05f))
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .clip(RoundedCornerShape(14.dp))
                    .background(iconColor),
                contentAlignment = Alignment.Center
            ) {
                Icon(icon, contentDescription = null, tint = Color.White.copy(alpha = 0.8f))
            }
            Spacer(modifier = Modifier.width(16.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(title, style = MaterialTheme.typography.titleSmall)
                Text(subtitle, style = MaterialTheme.typography.bodySmall, color = Color.White.copy(alpha = 0.4f))
            }
            Text(size, style = MaterialTheme.typography.titleMedium, color = Color(0xFFFABB3D))
            Icon(Icons.Default.ChevronRight, contentDescription = null, tint = Color.White.copy(alpha = 0.2f))
        }
    }
}

@Composable
fun ProfessionalScanner() {
    val infiniteTransition = rememberInfiniteTransition()
    val scanProgress by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(2000, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        )
    )
    val pulseAlpha by infiniteTransition.animateFloat(
        initialValue = 0.2f,
        targetValue = 0.6f,
        animationSpec = infiniteRepeatable(
            animation = tween(1000, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        )
    )

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(60.dp)
            .clip(RoundedCornerShape(8.dp))
            .background(Color.White.copy(alpha = 0.05f))
    ) {
        // Scanning Line
        Canvas(modifier = Modifier.fillMaxSize()) {
            val y = size.height * scanProgress
            drawLine(
                brush = Brush.horizontalGradient(
                    colors = listOf(Color.Transparent, PrimaryAccent, Color.Transparent)
                ),
                start = Offset(0f, y),
                end = Offset(size.width, y),
                strokeWidth = 3.dp.toPx(),
                cap = StrokeCap.Round
            )
            
            // Glow effect
            drawRect(
                brush = Brush.verticalGradient(
                    colors = listOf(PrimaryAccent.copy(alpha = 0.1f), Color.Transparent),
                    startY = y - 20.dp.toPx(),
                    endY = y
                ),
                topLeft = Offset(0f, y - 20.dp.toPx()),
                size = androidx.compose.ui.geometry.Size(size.width, 20.dp.toPx())
            )
        }
    }
}

@Composable
fun CategoryCard(title: String, subtitle: String, onclick: () -> Unit) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .aspectRatio(0.9f)
            .clip(RoundedCornerShape(24.dp))
            .background(Color.White.copy(alpha = 0.03f))
            .border(1.dp, Color.White.copy(alpha = 0.05f), RoundedCornerShape(24.dp))
            .clickable { onclick() }
            .padding(20.dp)
    ) {
        Column(modifier = Modifier.align(Alignment.BottomStart)) {
            Text(title, style = MaterialTheme.typography.titleMedium, color = Color.White)
            Spacer(modifier = Modifier.height(4.dp))
            Text(subtitle, style = MaterialTheme.typography.bodySmall, color = Color.White.copy(alpha = 0.4f))
        }
    }
}
