package app.lumalabs.luma.ui.screen

import android.app.Activity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.IntentSenderRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import app.lumalabs.luma.ui.theme.DarkSurfaceVariant
import app.lumalabs.luma.ui.theme.PrimaryAccent
import app.lumalabs.luma.ui.viewmodel.DashboardViewModel
import app.lumalabs.luma.ui.component.LumaBackground
import coil.compose.AsyncImage
import coil.request.ImageRequest

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SimilarPhotosScreen(
    navController: NavController,
    viewModel: DashboardViewModel = hiltViewModel()
) {
    val clusters by viewModel.similarClusters.collectAsState()
    val isScanning by viewModel.isScanning.collectAsState()
    val context = LocalContext.current

    var pendingUrisToTrash by remember { mutableStateOf<List<android.net.Uri>?>(null) }

    val trashLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartIntentSenderForResult()
    ) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            pendingUrisToTrash?.let { viewModel.removeResults(it) }
            pendingUrisToTrash = null
        }
    }

    LumaBackground {
        Scaffold(
            containerColor = Color.Transparent,
            topBar = {
            TopAppBar(
                title = { Text("Similar Photos", style = MaterialTheme.typography.titleLarge) },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) { 
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back") 
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.Transparent)
            )
        }
    ) { padding ->
        if (isScanning && clusters.isEmpty()) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
                    .padding(horizontal = 20.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Text(
                    "Scanning in progress...",
                    style = MaterialTheme.typography.titleLarge,
                    color = Color.White.copy(alpha = 0.5f)
                )
                Spacer(modifier = Modifier.height(16.dp))
                CircularProgressIndicator(color = PrimaryAccent)
            }
        } else if (clusters.isEmpty()) {
            Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Text("No similar photos found", color = Color.Gray)
            }
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
                    .padding(horizontal = 16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                clusters.forEach { (clusterId, results) ->
                    item(key = clusterId) {
                        PhotoClusterCard(
                            uris = results.map { it.photoUri },
                            bestUri = results.find { it.isBest }?.photoUri ?: results.first().photoUri,
                            onKeepBest = {
                                viewModel.keepBest(clusterId) { pendingIntent, uris ->
                                    pendingUrisToTrash = uris
                                    val request = IntentSenderRequest.Builder(pendingIntent).build()
                                    trashLauncher.launch(request)
                                }
                            },
                            onManualReview = { /* Todo */ }
                        )
                    }
                }
            }
        }
        }
    }
}

@Composable
fun PhotoClusterCard(
    uris: List<String>,
    bestUri: String,
    onKeepBest: () -> Unit,
    onManualReview: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = DarkSurfaceVariant)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text("${uris.size} Similar Photos", fontWeight = FontWeight.Bold)
            Spacer(modifier = Modifier.height(12.dp))
            
            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                items(uris) { uri ->
                    Box {
                        AsyncImage(
                            model = ImageRequest.Builder(LocalContext.current)
                                .data(uri)
                                .crossfade(true)
                                .build(),
                            contentDescription = null,
                            modifier = Modifier
                                .size(120.dp)
                                .clip(RoundedCornerShape(8.dp))
                                .background(Color.White.copy(alpha = 0.05f)),
                            contentScale = ContentScale.Crop
                        )
                        if (uri == bestUri) {
                            Surface(
                                color = PrimaryAccent,
                                shape = RoundedCornerShape(4.dp),
                                modifier = Modifier.align(Alignment.BottomEnd).padding(4.dp)
                            ) {
                                Text(
                                    "BEST", 
                                    fontSize = 10.sp, 
                                    modifier = Modifier.padding(horizontal = 4.dp, vertical = 2.dp),
                                    color = Color.White
                                )
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                TextButton(onClick = onManualReview) {
                    Text("Review Manually", color = PrimaryAccent)
                }
                Button(
                    onClick = onKeepBest,
                    colors = ButtonDefaults.buttonColors(containerColor = PrimaryAccent),
                    shape = RoundedCornerShape(50.dp)
                ) {
                    Text("Keep Best")
                }
            }
        }
    }
}
