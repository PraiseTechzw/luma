package app.lumalabs.luma.ui.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import app.lumalabs.luma.ui.navigation.Screen
import app.lumalabs.luma.ui.theme.DarkSurfaceVariant
import app.lumalabs.luma.ui.theme.PrimaryAccent
import app.lumalabs.luma.ui.viewmodel.DashboardViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DashboardScreen(
    navController: NavController,
    viewModel: DashboardViewModel = hiltViewModel()
) {
    val reclaimableSize by viewModel.reclaimableSize.collectAsState()
    val isScanning by viewModel.isScanning.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Luma", style = MaterialTheme.typography.displayLarge) },
                actions = {
                    IconButton(onClick = { navController.navigate(Screen.Settings.route) }) {
                        Icon(Icons.Default.Settings, contentDescription = "Settings")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.Transparent)
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(20.dp)
        ) {
            // Storage Widget
            Text(
                "Total reclaimable",
                style = MaterialTheme.typography.labelSmall,
                color = Color.White.copy(alpha = 0.6f)
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                "${reclaimableSize / (1024 * 1024)} MB",
                style = MaterialTheme.typography.displayLarge.copy(
                    fontSize = 48.sp,
                    color = PrimaryAccent
                )
            )

            if (isScanning) {
                LinearProgressIndicator(
                    modifier = Modifier.fillMaxWidth().height(8.dp).padding(vertical = 12.dp),
                    color = PrimaryAccent,
                    trackColor = DarkSurfaceVariant
                )
            }

            Spacer(modifier = Modifier.height(32.dp))

            // Categories
            LazyVerticalGrid(
                columns = GridCells.Fixed(2),
                horizontalArrangement = Arrangement.spacedBy(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp),
                modifier = Modifier.weight(1f)
            ) {
                item {
                    CategoryCard("Similar Photos", "1.2 GB", onclick = { navController.navigate(Screen.SimilarPhotos.route) })
                }
                item {
                    CategoryCard("Screenshots", "42 items", onclick = { })
                }
                item {
                    CategoryCard("Chat Media", "850 MB", onclick = { })
                }
            }

            Button(
                onClick = { viewModel.triggerScan() },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                shape = RoundedCornerShape(50.dp),
                colors = ButtonDefaults.buttonColors(containerColor = PrimaryAccent)
            ) {
                Text("Scan now", style = MaterialTheme.typography.titleLarge)
            }
        }
    }
}

@Composable
fun CategoryCard(title: String, subtitle: String, onclick: () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .aspectRatio(1f)
            .clickable { onclick() },
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = DarkSurfaceVariant)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.Start
        ) {
            Text(title, style = MaterialTheme.typography.titleLarge, fontSize = 18.sp)
            Spacer(modifier = Modifier.height(4.dp))
            Text(subtitle, color = Color.White.copy(alpha = 0.5f))
        }
    }
}
