package com.darkside.wallet.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.darkside.wallet.ui.components.AppBackButton
import com.darkside.wallet.ui.settings.SectionHeaderTitle
import com.darkside.wallet.ui.settings.SettingsActionTile
import com.darkside.wallet.ui.settings.SettingsSegmentedGroup

data class SearchableSetting(
    val title: String,
    val subtitle: String,
    val category: String,
    val keywords: List<String>,
    val icon: ImageVector,
    val isDestructive: Boolean = false,
    val onClick: () -> Unit
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    viewModel: WalletViewModel,
    onNavigateBack: () -> Unit,
    onNavigateToGeneral: () -> Unit = {},
    onNavigateToPersonalization: () -> Unit = {},
    onNavigateToPrivacySecurity: () -> Unit = {},
    onNavigateToBackupRestore: () -> Unit = {},
    onNavigateToAbout: () -> Unit = {},
    onNavigateToLogcat: () -> Unit = {},
    onNavigateToEditProfile: () -> Unit = {},
    onNavigateToCurrencySelection: () -> Unit = {},
    onNavigateToCategories: () -> Unit = {},
    onNavigateToFeedback: () -> Unit = {},
    onNavigateToPrivacyPolicy: () -> Unit = {},
    onNavigateToTermsOfUse: () -> Unit = {}
) {
    val colorScheme = MaterialTheme.colorScheme
    val isErrorCollectorEnabled by viewModel.isErrorCollectorEnabled.collectAsStateWithLifecycle()
    var searchQuery by remember { mutableStateOf("") }

    val allSettings = remember(isErrorCollectorEnabled) {
        listOf(
            SearchableSetting(
                title = "Edit Profile",
                subtitle = "Name, profile photo and avatar customization",
                category = "General",
                keywords = listOf("profile", "user", "name", "photo", "avatar", "edit", "account"),
                icon = Icons.Rounded.AccountCircle,
                onClick = onNavigateToEditProfile
            ),
            SearchableSetting(
                title = "Currency Settings",
                subtitle = "Default currency symbol, code and display formatting",
                category = "General",
                keywords = listOf("currency", "money", "dollar", "rupee", "euro", "symbol", "usd", "inr", "rate", "format"),
                icon = Icons.Rounded.CurrencyExchange,
                onClick = onNavigateToCurrencySelection
            ),
            SearchableSetting(
                title = "Categories",
                subtitle = "Manage income and expense tags, colors and symbols",
                category = "General",
                keywords = listOf("category", "categories", "tags", "labels", "organize", "spending"),
                icon = Icons.Rounded.Category,
                onClick = onNavigateToCategories
            ),
            SearchableSetting(
                title = "Send Feedback",
                subtitle = "Tell us your thoughts, ideas or report an issue",
                category = "General",
                keywords = listOf("feedback", "support", "contact", "developer", "email", "review"),
                icon = Icons.Rounded.RateReview,
                onClick = onNavigateToFeedback
            ),
            SearchableSetting(
                title = "Appearance & Preferences",
                subtitle = "Theme modes, dynamic color, typography and app craft",
                category = "Appearance",
                keywords = listOf("theme", "dark", "light", "dynamic", "color", "monochrome", "appearance", "preferences", "google sans flex", "typography", "font", "sliders"),
                icon = Icons.Rounded.Palette,
                onClick = onNavigateToPersonalization
            ),
            SearchableSetting(
                title = "Dynamic Color & Variants",
                subtitle = "Material You wallpaper-based palettes and scheme variants",
                category = "Appearance",
                keywords = listOf("dynamic", "material you", "color", "wallpaper", "monochrome", "vibrant", "expressive", "palette"),
                icon = Icons.Rounded.Draw,
                onClick = onNavigateToPersonalization
            ),
            SearchableSetting(
                title = "Typography & Sliders",
                subtitle = "Variable font weight, width, grade and optical size",
                category = "Appearance",
                keywords = listOf("font", "typography", "google sans", "weight", "width", "grade", "optical size", "text", "size"),
                icon = Icons.Rounded.FontDownload,
                onClick = onNavigateToPersonalization
            ),
            SearchableSetting(
                title = "Vibrate on Transaction",
                subtitle = "Haptic feedback on transaction creation",
                category = "Appearance",
                keywords = listOf("vibrate", "haptic", "feedback", "transaction vibration", "tactile"),
                icon = Icons.Rounded.Vibration,
                onClick = onNavigateToPersonalization
            ),
            SearchableSetting(
                title = "Biometric Lock",
                subtitle = "Protect your financial data with fingerprint authentication",
                category = "Privacy & Security",
                keywords = listOf("biometric", "fingerprint", "face", "lock", "security", "protect", "auth", "passcode"),
                icon = Icons.Rounded.Fingerprint,
                onClick = onNavigateToPrivacySecurity
            ),
            SearchableSetting(
                title = "Factory Reset",
                subtitle = "Permanently wipe all database records and reset app",
                category = "Privacy & Security",
                keywords = listOf("factory reset", "reset", "wipe", "delete", "clear all", "erase", "data shredder"),
                icon = Icons.Rounded.DeleteForever,
                isDestructive = true,
                onClick = onNavigateToPrivacySecurity
            ),
            SearchableSetting(
                title = "Privacy Policy",
                subtitle = "Offline-first data philosophy and storage principles",
                category = "Privacy & Security",
                keywords = listOf("privacy", "policy", "terms", "offline", "security", "data"),
                icon = Icons.Rounded.Shield,
                onClick = onNavigateToPrivacyPolicy
            ),
            SearchableSetting(
                title = "Terms of Use",
                subtitle = "Open-source licensing terms and usage guidelines",
                category = "Privacy & Security",
                keywords = listOf("terms", "license", "conditions", "legal", "open source"),
                icon = Icons.Rounded.Policy,
                onClick = onNavigateToTermsOfUse
            ),
            SearchableSetting(
                title = "Export to CSV",
                subtitle = "Export transactions to a CSV spreadsheet",
                category = "Backup & Restore",
                keywords = listOf("export", "csv", "spreadsheet", "excel", "backup", "transactions"),
                icon = Icons.Rounded.UploadFile,
                onClick = onNavigateToBackupRestore
            ),
            SearchableSetting(
                title = "Import from CSV",
                subtitle = "Import transactions from an existing CSV file",
                category = "Backup & Restore",
                keywords = listOf("import", "csv", "restore", "transactions", "spreadsheet"),
                icon = Icons.Rounded.DownloadForOffline,
                onClick = onNavigateToBackupRestore
            ),
            SearchableSetting(
                title = "Export to JSON",
                subtitle = "Backup transactions in portable JSON format",
                category = "Backup & Restore",
                keywords = listOf("export", "json", "data", "backup", "export json"),
                icon = Icons.Rounded.Storage,
                onClick = onNavigateToBackupRestore
            ),
            SearchableSetting(
                title = "Import from JSON",
                subtitle = "Restore transactions from a JSON data file",
                category = "Backup & Restore",
                keywords = listOf("import", "json", "data", "restore", "import json"),
                icon = Icons.Rounded.FolderOpen,
                onClick = onNavigateToBackupRestore
            ),
            SearchableSetting(
                title = "Backup Database",
                subtitle = "Complete archive of data, settings & attachments (.zip)",
                category = "Backup & Restore",
                keywords = listOf("backup", "database", "zip", "archive", "full backup", "export db"),
                icon = Icons.Rounded.Backup,
                onClick = onNavigateToBackupRestore
            ),
            SearchableSetting(
                title = "Restore Backup",
                subtitle = "Restore all data from a .zip backup archive",
                category = "Backup & Restore",
                keywords = listOf("restore", "database", "zip", "archive", "import db", "recover"),
                icon = Icons.Rounded.Restore,
                onClick = onNavigateToBackupRestore
            ),
            SearchableSetting(
                title = "About Wallet",
                subtitle = "Version info, developer details and open-source licenses",
                category = "About",
                keywords = listOf("about", "version", "developer", "abhijeet yadav", "licenses", "github", "open source"),
                icon = Icons.Rounded.Info,
                onClick = onNavigateToAbout
            )
        ) + if (isErrorCollectorEnabled) {
            listOf(
                SearchableSetting(
                    title = "Error Collector (Logcat)",
                    subtitle = "Runtime logs, diagnostics, and performance monitor",
                    category = "Error Collector",
                    keywords = listOf("error collector", "logcat", "logs", "dev", "developer", "debug", "diagnostics", "runtime", "errors", "performance"),
                    icon = Icons.Rounded.BugReport,
                    onClick = onNavigateToLogcat
                )
            )
        } else emptyList()
    }

    val filteredSettings = remember(searchQuery, allSettings) {
        if (searchQuery.isBlank()) emptyList()
        else {
            val q = searchQuery.trim().lowercase()
            allSettings.filter { setting ->
                setting.title.lowercase().contains(q) ||
                setting.subtitle.lowercase().contains(q) ||
                setting.category.lowercase().contains(q) ||
                setting.keywords.any { it.lowercase().contains(q) }
            }
        }
    }

    Scaffold(
        topBar = {
            MediumTopAppBar(
                title = {
                    Text(
                        "Settings",
                        style = MaterialTheme.typography.headlineLarge.copy(
                            fontWeight = FontWeight.ExtraBold,
                            fontSize = 34.sp,
                            letterSpacing = (-0.5).sp
                        )
                    )
                },
                navigationIcon = {
                    AppBackButton(onBack = onNavigateBack, modifier = Modifier.padding(start = 12.dp))
                },
                colors = TopAppBarDefaults.mediumTopAppBarColors(
                    containerColor = colorScheme.surface
                )
            )
        },
        containerColor = colorScheme.surface
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding),
            contentPadding = PaddingValues(start = 20.dp, end = 20.dp, top = 8.dp, bottom = 48.dp),
            verticalArrangement = Arrangement.spacedBy(24.dp)
        ) {
            // 1. Search Bar
            item {
                TextField(
                    value = searchQuery,
                    onValueChange = { searchQuery = it },
                    placeholder = {
                        Text(
                            "Search settings...",
                            style = MaterialTheme.typography.bodyMedium.copy(
                                color = colorScheme.onSurfaceVariant.copy(alpha = 0.8f),
                                fontSize = 15.sp
                            )
                        )
                    },
                    leadingIcon = {
                        Icon(
                            imageVector = Icons.Rounded.Search,
                            contentDescription = "Search",
                            tint = colorScheme.onSurfaceVariant,
                            modifier = Modifier.size(22.dp)
                        )
                    },
                    trailingIcon = {
                        if (searchQuery.isNotEmpty()) {
                            IconButton(onClick = { searchQuery = "" }) {
                                Icon(
                                    imageVector = Icons.Rounded.Close,
                                    contentDescription = "Clear",
                                    tint = colorScheme.onSurfaceVariant,
                                    modifier = Modifier.size(20.dp)
                                )
                            }
                        }
                    },
                    shape = RoundedCornerShape(24.dp),
                    colors = TextFieldDefaults.colors(
                        focusedContainerColor = colorScheme.surfaceContainerLow,
                        unfocusedContainerColor = colorScheme.surfaceContainerLow,
                        focusedIndicatorColor = Color.Transparent,
                        unfocusedIndicatorColor = Color.Transparent
                    ),
                    modifier = Modifier.fillMaxWidth()
                )
            }

            // 2. Content Area: Search Results or Main Segmented Menus
            if (searchQuery.isNotEmpty()) {
                item {
                    SectionHeaderTitle("SEARCH RESULTS (${filteredSettings.size})")
                }

                if (filteredSettings.isEmpty()) {
                    item {
                        Card(
                            shape = RoundedCornerShape(24.dp),
                            colors = CardDefaults.cardColors(containerColor = colorScheme.surfaceContainerLow),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 48.dp, horizontal = 24.dp),
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Icon(
                                    imageVector = Icons.Rounded.SearchOff,
                                    contentDescription = null,
                                    tint = colorScheme.onSurfaceVariant.copy(alpha = 0.5f),
                                    modifier = Modifier.size(48.dp)
                                )
                                Spacer(modifier = Modifier.height(12.dp))
                                Text(
                                    text = "No settings found",
                                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                                )
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = "Try searching for \"currency\", \"theme\", \"backup\", or \"profile\"",
                                    style = MaterialTheme.typography.bodyMedium.copy(
                                        color = colorScheme.onSurfaceVariant,
                                        fontSize = 13.sp
                                    )
                                )
                            }
                        }
                    }
                } else {
                    item {
                        SettingsSegmentedGroup {
                            filteredSettings.forEachIndexed { index, setting ->
                                SettingsActionTile(
                                    icon = setting.icon,
                                    title = setting.title,
                                    subtitle = "${setting.category} • ${setting.subtitle}",
                                    isDestructive = setting.isDestructive,
                                    showDivider = index < filteredSettings.size - 1,
                                    onClick = setting.onClick
                                )
                            }
                        }
                    }
                }
            } else {
                // Main Menus View with M3 Expressive ONE unified segmented group
                item {
                    Column {
                        SectionHeaderTitle("PREFERENCES & CONTROLS")
                        Spacer(modifier = Modifier.height(12.dp))

                        SettingsSegmentedGroup {
                            SettingsActionTile(
                                icon = Icons.Rounded.Tune,
                                title = "General",
                                subtitle = "Currency settings, user profile, and categories",
                                showDivider = true,
                                onClick = onNavigateToGeneral
                            )
                            SettingsActionTile(
                                icon = Icons.Rounded.Palette,
                                title = "Appearance",
                                subtitle = "Theme, dynamic color, typography, and behavior",
                                showDivider = true,
                                onClick = onNavigateToPersonalization
                            )
                            SettingsActionTile(
                                icon = Icons.Rounded.Shield,
                                title = "Privacy & Security",
                                subtitle = "Biometric lock, factory reset, and policy",
                                showDivider = true,
                                onClick = onNavigateToPrivacySecurity
                            )
                            SettingsActionTile(
                                icon = Icons.Rounded.CloudSync,
                                title = "Backup & Restore",
                                subtitle = "Export/import CSV, JSON, and database archive",
                                showDivider = true,
                                onClick = onNavigateToBackupRestore
                            )
                            SettingsActionTile(
                                icon = Icons.Rounded.Info,
                                title = "About",
                                subtitle = "v4.0.5 · Developer, licenses, and system info",
                                showDivider = isErrorCollectorEnabled,
                                onClick = onNavigateToAbout
                            )
                            if (isErrorCollectorEnabled) {
                                SettingsActionTile(
                                    icon = Icons.Rounded.BugReport,
                                    title = "Error Collector",
                                    subtitle = "Runtime analysis, performance logs, and debug tools",
                                    showDivider = false,
                                    onClick = onNavigateToLogcat
                                )
                            }
                        }
                    }
                }

                // Version Footer
                item {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 24.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "Wallet v4.0.5 (June 2026)",
                            style = MaterialTheme.typography.bodySmall.copy(
                                color = colorScheme.onSurfaceVariant.copy(alpha = 0.6f),
                                fontWeight = FontWeight.Medium
                            )
                        )
                    }
                }
            }
        }
    }
}
