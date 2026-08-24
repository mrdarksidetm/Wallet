package com.darkside.wallet.ui.settings

import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.darkside.wallet.ui.WalletViewModel
import com.darkside.wallet.ui.components.AppBackButton
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AboutScreen(
    viewModel: WalletViewModel,
    onBack: () -> Unit,
    onNavigateToPrivacyPolicy: () -> Unit
) {
    val context = LocalContext.current
    val colorScheme = MaterialTheme.colorScheme
    val scope = rememberCoroutineScope()
    val snackbarHostState = remember { SnackbarHostState() }
    val isErrorCollectorEnabled by viewModel.isErrorCollectorEnabled.collectAsStateWithLifecycle()

    var versionTapCount by remember { mutableIntStateOf(0) }
    var lastTapTime by remember { mutableLongStateOf(0L) }
    var showUpdateDialog by remember { mutableStateOf(false) }
    var showLicensesDialog by remember { mutableStateOf(false) }

    fun onVersionTap() {
        val now = System.currentTimeMillis()
        if (now - lastTapTime > 3000) {
            versionTapCount = 0
        }
        lastTapTime = now
        versionTapCount++

        if (isErrorCollectorEnabled) {
            scope.launch {
                snackbarHostState.showSnackbar("Error Collector (LogCat) is already active")
            }
            return
        }

        if (versionTapCount >= 7) {
            versionTapCount = 0
            viewModel.enableErrorCollector()
            scope.launch {
                snackbarHostState.showSnackbar("Error Collector (LogCat) Activated")
            }
        } else if (versionTapCount >= 3) {
            val remaining = 7 - versionTapCount
            scope.launch {
                snackbarHostState.showSnackbar("You are $remaining step${if (remaining > 1) "s" else ""} away from activating Error Collector")
            }
        }
    }

    fun openUrl(url: String) {
        try {
            val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
            context.startActivity(intent)
        } catch (e: Exception) {
            scope.launch {
                snackbarHostState.showSnackbar("Could not open link: $url")
            }
        }
    }

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
        topBar = {
            TopAppBar(
                title = { },
                navigationIcon = {
                    AppBackButton(onBack = onBack, modifier = Modifier.padding(start = 12.dp))
                },
                colors = TopAppBarDefaults.topAppBarColors(
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
            contentPadding = PaddingValues(horizontal = 20.dp, vertical = 12.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // App Icon Hero
            item {
                Box(
                    modifier = Modifier
                        .size(84.dp)
                        .clip(RoundedCornerShape(32.dp))
                        .background(colorScheme.primaryContainer),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Rounded.AccountBalanceWallet,
                        contentDescription = null,
                        tint = colorScheme.primary,
                        modifier = Modifier.size(48.dp)
                    )
                }
            }

            // App Title & Version Badge
            item {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text = "Wallet",
                        style = MaterialTheme.typography.headlineLarge.copy(
                            fontWeight = FontWeight.Black,
                            fontSize = 32.sp,
                            letterSpacing = (-1).sp
                        )
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(12.dp))
                            .background(colorScheme.primaryContainer.copy(alpha = 0.5f))
                            .clickable { onVersionTap() }
                            .padding(horizontal = 14.dp, vertical = 6.dp)
                    ) {
                        Text(
                            text = "Version 4.0.5",
                            style = MaterialTheme.typography.labelMedium.copy(
                                fontWeight = FontWeight.Bold,
                                color = colorScheme.primary
                            )
                        )
                    }
                    Spacer(modifier = Modifier.height(14.dp))
                    Text(
                        text = "Rebuild from ground up to support Android Community using modern Jetpack Compose Support",
                        style = MaterialTheme.typography.bodyMedium.copy(
                            color = colorScheme.onSurfaceVariant,
                            lineHeight = 20.sp
                        ),
                        textAlign = TextAlign.Center,
                        modifier = Modifier.padding(horizontal = 16.dp)
                    )
                }
            }

            // Developer Card
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(24.dp),
                    colors = CardDefaults.cardColors(containerColor = colorScheme.surfaceContainerLow),
                    border = androidx.compose.foundation.BorderStroke(0.5.dp, colorScheme.outlineVariant.copy(alpha = 0.35f))
                ) {
                    Column(modifier = Modifier.fillMaxWidth().padding(16.dp)) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable { openUrl("https://github.com/mrdarksidetm") }
                                .padding(vertical = 4.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(44.dp)
                                    .clip(CircleShape)
                                    .background(colorScheme.primaryContainer),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    "AY",
                                    style = MaterialTheme.typography.titleMedium.copy(
                                        fontWeight = FontWeight.Bold,
                                        color = colorScheme.primary
                                    )
                                )
                            }
                            Spacer(modifier = Modifier.width(14.dp))
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    "Abhijeet Yadav",
                                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                                )
                                Text(
                                    "Developer",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = colorScheme.onSurfaceVariant
                                )
                            }
                            Icon(
                                Icons.Rounded.ChevronRight,
                                contentDescription = null,
                                tint = colorScheme.onSurfaceVariant.copy(alpha = 0.5f)
                            )
                        }

                        Spacer(modifier = Modifier.height(12.dp))

                        // Pill buttons for GitHub and Email
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            PillButton(
                                label = "Github",
                                icon = Icons.Rounded.Code,
                                modifier = Modifier.weight(1f),
                                onClick = { openUrl("https://github.com/mrdarksidetm") }
                            )
                            PillButton(
                                label = "Email",
                                icon = Icons.Rounded.AlternateEmail,
                                modifier = Modifier.weight(1f),
                                onClick = { openUrl("mailto:contact.dsidetm@gmail.com") }
                            )
                        }
                    }
                }
            }

            // About & Links Segmented Group
            item {
                SettingsSegmentedGroup {
                    SettingsActionTile(
                        icon = Icons.Rounded.Update,
                        title = "Check for Updates",
                        subtitle = "v4.0.5 \"The Variable Atelier\"",
                        showDivider = true,
                        onClick = { showUpdateDialog = true }
                    )
                    SettingsActionTile(
                        icon = Icons.Rounded.Source,
                        title = "Open Source",
                        subtitle = "View source code repository on GitHub",
                        showDivider = true,
                        onClick = { openUrl("https://github.com/mrdarksidetm/wallet") }
                    )
                    SettingsActionTile(
                        icon = Icons.Rounded.Shield,
                        title = "Privacy Policy",
                        subtitle = "How we handle your data offline",
                        showDivider = true,
                        onClick = onNavigateToPrivacyPolicy
                    )
                    SettingsActionTile(
                        icon = Icons.Rounded.Gavel,
                        title = "Licenses",
                        subtitle = "Third-party open-source software libraries",
                        showDivider = false,
                        onClick = { showLicensesDialog = true }
                    )
                }
            }

            // Footer
            item {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 24.dp, bottom = 32.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "Made with ❤️",
                        style = MaterialTheme.typography.bodySmall.copy(
                            fontWeight = FontWeight.SemiBold,
                            color = colorScheme.onSurfaceVariant.copy(alpha = 0.7f)
                        )
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Rounded.Android,
                            contentDescription = null,
                            modifier = Modifier.size(16.dp),
                            tint = colorScheme.onSurfaceVariant.copy(alpha = 0.5f)
                        )
                        Text(
                            text = "X  Jetpack Compose",
                            style = MaterialTheme.typography.labelSmall.copy(
                                fontWeight = FontWeight.Bold,
                                color = colorScheme.onSurfaceVariant.copy(alpha = 0.5f)
                            )
                        )
                    }
                }
            }
        }
    }

    if (showUpdateDialog) {
        AlertDialog(
            onDismissRequest = { showUpdateDialog = false },
            shape = RoundedCornerShape(28.dp),
            icon = {
                Icon(
                    Icons.Rounded.AutoAwesome,
                    contentDescription = null,
                    tint = Color(0xFF00897B),
                    modifier = Modifier.size(28.dp)
                )
            },
            title = { Text("Up to Date", fontWeight = FontWeight.Bold) },
            text = {
                Text(
                    "You are already using the most refined Material 3 Expressive version of Wallet (v4.0.5).",
                    style = MaterialTheme.typography.bodyMedium
                )
            },
            confirmButton = {
                FilledTonalButton(onClick = { showUpdateDialog = false }) {
                    Text("Excellent")
                }
            }
        )
    }

    if (showLicensesDialog) {
        AlertDialog(
            onDismissRequest = { showLicensesDialog = false },
            shape = RoundedCornerShape(28.dp),
            title = { Text("Open Source Licenses", fontWeight = FontWeight.Bold) },
            text = {
                Column(modifier = Modifier.padding(top = 8.dp)) {
                    Text(
                        "• AndroidX Core, Lifecycle & Compose (Apache 2.0)\n" +
                        "• Material 3 Components & Expressive Motion (Apache 2.0)\n" +
                        "• AndroidX Room Database & SQLite (Apache 2.0)\n" +
                        "• Kotlin Coroutines & Flow (Apache 2.0)\n" +
                        "• Coil Image Loader (Apache 2.0)\n" +
                        "• Vico Charting Engine (Apache 2.0)",
                        style = MaterialTheme.typography.bodyMedium,
                        lineHeight = 22.sp
                    )
                }
            },
            confirmButton = {
                TextButton(onClick = { showLicensesDialog = false }) {
                    Text("Close")
                }
            }
        )
    }
}

@Composable
fun PillButton(
    label: String,
    icon: ImageVector,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    val colorScheme = MaterialTheme.colorScheme

    Box(
        modifier = modifier
            .clip(RoundedCornerShape(100.dp))
            .background(colorScheme.surfaceContainerHighest.copy(alpha = 0.6f))
            .clickable { onClick() }
            .padding(vertical = 10.dp, horizontal = 14.dp),
        contentAlignment = Alignment.Center
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = colorScheme.onSurfaceVariant,
                modifier = Modifier.size(16.dp)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = label,
                style = MaterialTheme.typography.labelMedium.copy(
                    fontWeight = FontWeight.Bold,
                    color = colorScheme.onSurfaceVariant
                )
            )
        }
    }
}
