package com.mrdarksidetm.wallet.ui

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(onBack: () -> Unit) {
    val settingsItems = listOf(
        SettingsItem("Personalization", "Themes and color options", Icons.Default.Palette),
        SettingsItem("Preferences", "Layouts, effects, and formats", Icons.Default.Brush),
        SettingsItem("Data & Backup", "Cloud backup and data management", Icons.Default.Cloud),
        SettingsItem("Security & Notifications", "Security and notification settings", Icons.Default.Notifications),
        SettingsItem("Community & Support", "Help, feedback, and community", Icons.Default.Language),
        SettingsItem("Privacy & Policy", "App information and version", Icons.Default.Info),
        SettingsItem("Achievements", "Track your milestones and rewards", Icons.Default.EmojiEvents, isBeta = true),
        SettingsItem("Feedback & Beta", "Something went wrong, have any thoughts?", Icons.Default.Feedback),
        SettingsItem("Share Paisa", "", Icons.Default.Share)
    )

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Settings") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize(),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(settingsItems) { item ->
                Surface(
                    shape = MaterialTheme.shapes.large,
                    color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
                    onClick = { /* Navigate to detail */ }
                ) {
                    ListItem(
                        headlineContent = { 
                            Row {
                                Text(item.title)
                                if (item.isBeta) {
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Badge { Text("Beta") }
                                }
                            }
                        },
                        supportingContent = { if (item.subtitle.isNotEmpty()) Text(item.subtitle) },
                        leadingContent = { Icon(item.icon, contentDescription = null) },
                        colors = ListItemDefaults.colors(containerColor = androidx.compose.ui.graphics.Color.Transparent)
                    )
                }
            }
        }
    }
}

data class SettingsItem(
    val title: String,
    val subtitle: String,
    val icon: ImageVector,
    val isBeta: Boolean = false
)
