package app.lumalabs.luma.ui.screen

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import app.lumalabs.luma.data.repository.PreferenceRepository
import app.lumalabs.luma.ui.theme.PrimaryAccent
import app.lumalabs.luma.ui.component.LumaBackground

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    navController: NavController,
    preferenceRepository: PreferenceRepository = hiltViewModel()
) {
    LumaBackground {
        Scaffold(
            containerColor = Color.Transparent,
            topBar = {
                TopAppBar(
                    title = { Text("Settings", style = MaterialTheme.typography.titleLarge) },
                    navigationIcon = {
                        IconButton(onClick = { navController.popBackStack() }) {
                            Icon(Icons.Default.ArrowBack, contentDescription = "Back", tint = Color.White)
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
                .padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(24.dp)
        ) {
            item {
                Text("Automation", style = MaterialTheme.typography.labelSmall, color = PrimaryAccent)
                Spacer(modifier = Modifier.height(12.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text("Weekly Scan Reminder", style = MaterialTheme.typography.bodyLarge)
                    Switch(checked = true, onCheckedChange = { }, colors = SwitchDefaults.colors(checkedThumbColor = PrimaryAccent))
                }
            }

            item {
                Text("Scanning", style = MaterialTheme.typography.labelSmall, color = PrimaryAccent)
                Spacer(modifier = Modifier.height(12.dp))
                Column {
                    Text("Similarity Threshold", style = MaterialTheme.typography.bodyLarge)
                    Slider(
                        value = 0.92f,
                        onValueChange = { },
                        valueRange = 0.85f..0.99f,
                        colors = SliderDefaults.colors(thumbColor = PrimaryAccent, activeTrackColor = PrimaryAccent)
                    )
                }
            }

            item {
                Text("Deletion", style = MaterialTheme.typography.labelSmall, color = PrimaryAccent)
                Spacer(modifier = Modifier.height(12.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text("Auto-trash", style = MaterialTheme.typography.bodyLarge)
                    Switch(checked = false, onCheckedChange = { }, colors = SwitchDefaults.colors(checkedThumbColor = PrimaryAccent))
                }
            }
        }
    }
}
}
