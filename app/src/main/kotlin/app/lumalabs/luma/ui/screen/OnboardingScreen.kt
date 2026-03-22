package app.lumalabs.luma.ui.screen

import android.Manifest
import android.os.Build
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import app.lumalabs.luma.ui.theme.DarkBackground
import app.lumalabs.luma.ui.theme.DarkSurfaceVariant
import app.lumalabs.luma.ui.theme.PrimaryAccent

@Composable
fun OnboardingScreen(onComplete: () -> Unit) {
    var step by remember { mutableStateOf(1) }
    
    val mediaPermissionLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { granted ->
        if (granted) step = 2
    }

    val notificationPermissionLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) {
        step = 3
    }

    val storagePermissionLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) {
        onComplete()
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(DarkBackground)
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            "Welcome to Luma",
            style = MaterialTheme.typography.displayLarge,
            color = PrimaryAccent,
            textAlign = TextAlign.Center
        )
        Spacer(modifier = Modifier.height(16.dp))
        Text(
            "To help you reclaim storage, we need a few permissions to scan your library correctly.",
            style = MaterialTheme.typography.bodyLarge,
            color = Color.White.copy(alpha = 0.7f),
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(48.dp))

        PermissionItem(
            title = "Read Media",
            description = "Needed to scan for similar photos and screenshots.",
            isGranted = step > 1,
            isCurrent = step == 1,
            onRequest = {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                    mediaPermissionLauncher.launch(Manifest.permission.READ_MEDIA_IMAGES)
                } else {
                    mediaPermissionLauncher.launch(Manifest.permission.READ_EXTERNAL_STORAGE)
                }
            }
        )

        Spacer(modifier = Modifier.height(16.dp))

        PermissionItem(
            title = "Notifications",
            description = "Reminders to clean your storage weekly.",
            isGranted = step > 2,
            isCurrent = step == 2,
            onRequest = {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                    notificationPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
                } else {
                    step = 3
                }
            }
        )

        Spacer(modifier = Modifier.height(16.dp))

        PermissionItem(
            title = "Storage Management",
            description = "Allows moving items to trash and organization.",
            isGranted = false,
            isCurrent = step == 3,
            onRequest = {
                storagePermissionLauncher.launch(Manifest.permission.MANAGE_EXTERNAL_STORAGE)
            }
        )
    }
}

@Composable
fun PermissionItem(
    title: String,
    description: String,
    isGranted: Boolean,
    isCurrent: Boolean,
    onRequest: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (isCurrent) DarkSurfaceVariant else DarkSurfaceVariant.copy(alpha = 0.5f)
        )
    ) {
        Row(
            modifier = Modifier
                .padding(20.dp)
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(title, style = MaterialTheme.typography.titleLarge, fontSize = 18.sp)
                Text(
                    description,
                    style = MaterialTheme.typography.labelSmall,
                    color = Color.White.copy(alpha = 0.5f)
                )
            }
            if (isGranted) {
                Text("Granted", color = PrimaryAccent, fontWeight = FontWeight.Bold)
            } else if (isCurrent) {
                Button(
                    onClick = onRequest,
                    colors = ButtonDefaults.buttonColors(containerColor = PrimaryAccent),
                    contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp)
                ) {
                    Text("Allow")
                }
            }
        }
    }
}
