package com.darkside.wallet.ui.settings

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import androidx.compose.foundation.background
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.ContentCopy
import androidx.compose.material.icons.rounded.DeleteSweep
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.darkside.wallet.ui.WalletViewModel
import com.darkside.wallet.ui.components.AppBackButton
import com.darkside.wallet.utils.LogEntry
import com.darkside.wallet.utils.LogLevel
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LogcatScreen(
    viewModel: WalletViewModel,
    onBack: () -> Unit
) {
    val context = LocalContext.current
    val colorScheme = MaterialTheme.colorScheme
    val scope = rememberCoroutineScope()
    val snackbarHostState = remember { SnackbarHostState() }

    val allLogs by viewModel.logs.collectAsStateWithLifecycle()
    var selectedFilter by remember { mutableStateOf<LogLevel?>(null) }

    val filteredLogs = remember(allLogs, selectedFilter) {
        if (selectedFilter == null) allLogs
        else allLogs.filter { it.level == selectedFilter }
    }

    fun copyLogsToClipboard() {
        val text = filteredLogs.joinToString("\n") { log ->
            "[${log.formattedTime}] [${log.level.name}] ${log.message}${if (log.stackTrace != null) "\n" + log.stackTrace else ""}"
        }
        val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
        val clip = ClipData.newPlainText("Wallet Logs", text)
        clipboard.setPrimaryClip(clip)
        scope.launch {
            snackbarHostState.showSnackbar("Logs copied to clipboard")
        }
    }

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
        topBar = {
            MediumTopAppBar(
                title = {
                    Text(
                        "Error Collector",
                        style = MaterialTheme.typography.headlineLarge.copy(
                            fontWeight = FontWeight.ExtraBold,
                            fontSize = 34.sp,
                            letterSpacing = (-0.5).sp
                        )
                    )
                },
                navigationIcon = {
                    AppBackButton(onBack = onBack, modifier = Modifier.padding(start = 12.dp))
                },
                actions = {
                    IconButton(onClick = { viewModel.clearLogs() }) {
                        Icon(
                            Icons.Rounded.DeleteSweep,
                            contentDescription = "Clear Logs"
                        )
                    }
                    IconButton(onClick = { copyLogsToClipboard() }) {
                        Icon(
                            Icons.Rounded.ContentCopy,
                            contentDescription = "Copy Logs"
                        )
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                },
                colors = TopAppBarDefaults.mediumTopAppBarColors(
                    containerColor = colorScheme.surface
                )
            )
        },
        containerColor = colorScheme.surface
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            // Filter Chips
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .horizontalScroll(rememberScrollState())
                    .padding(horizontal = 16.dp, vertical = 8.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                FilterChip(
                    selected = selectedFilter == null,
                    onClick = { selectedFilter = null },
                    label = { Text("All") }
                )
                FilterChip(
                    selected = selectedFilter == LogLevel.INFO,
                    onClick = { selectedFilter = if (selectedFilter == LogLevel.INFO) null else LogLevel.INFO },
                    label = { Text("Info") }
                )
                FilterChip(
                    selected = selectedFilter == LogLevel.PERFORMANCE,
                    onClick = { selectedFilter = if (selectedFilter == LogLevel.PERFORMANCE) null else LogLevel.PERFORMANCE },
                    label = { Text("Performance") }
                )
                FilterChip(
                    selected = selectedFilter == LogLevel.WARNING,
                    onClick = { selectedFilter = if (selectedFilter == LogLevel.WARNING) null else LogLevel.WARNING },
                    label = { Text("Warning") }
                )
                FilterChip(
                    selected = selectedFilter == LogLevel.ERROR,
                    onClick = { selectedFilter = if (selectedFilter == LogLevel.ERROR) null else LogLevel.ERROR },
                    label = { Text("Error") },
                    colors = FilterChipDefaults.filterChipColors(
                        selectedContainerColor = colorScheme.errorContainer,
                        selectedLabelColor = colorScheme.onErrorContainer
                    )
                )
            }

            HorizontalDivider(color = colorScheme.outlineVariant.copy(alpha = 0.35f))

            if (filteredLogs.isEmpty()) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        "No logs collected yet.",
                        style = MaterialTheme.typography.bodyMedium,
                        color = colorScheme.onSurfaceVariant
                    )
                }
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    items(filteredLogs.reversed(), key = { it.id }) { log ->
                        LogItemCard(log = log)
                    }
                }
            }
        }
    }
}

@Composable
fun LogItemCard(log: LogEntry) {
    val colorScheme = MaterialTheme.colorScheme

    val (badgeColor, badgeTextColor) = when (log.level) {
        LogLevel.INFO -> colorScheme.primaryContainer to colorScheme.onPrimaryContainer
        LogLevel.PERFORMANCE -> Color(0xFFE1BEE7) to Color(0xFF6A1B9A)
        LogLevel.WARNING -> Color(0xFFFFE082) to Color(0xFFE65100)
        LogLevel.ERROR -> colorScheme.errorContainer to colorScheme.onErrorContainer
    }

    Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = colorScheme.surfaceContainerLow),
        border = androidx.compose.foundation.BorderStroke(0.5.dp, colorScheme.outlineVariant.copy(alpha = 0.35f)),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = log.formattedTime,
                    style = MaterialTheme.typography.bodySmall.copy(
                        fontFamily = FontFamily.Monospace,
                        fontSize = 11.sp
                    ),
                    color = colorScheme.onSurfaceVariant
                )

                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(6.dp))
                        .background(badgeColor)
                        .padding(horizontal = 8.dp, vertical = 2.dp)
                ) {
                    Text(
                        text = log.level.name,
                        style = MaterialTheme.typography.labelSmall.copy(
                            fontWeight = FontWeight.Bold,
                            fontSize = 10.sp
                        ),
                        color = badgeTextColor
                    )
                }
            }

            Spacer(modifier = Modifier.height(6.dp))

            Text(
                text = log.message,
                style = MaterialTheme.typography.bodyMedium.copy(
                    fontFamily = FontFamily.Monospace,
                    fontSize = 13.sp
                ),
                color = colorScheme.onSurface
            )

            if (log.stackTrace != null) {
                Spacer(modifier = Modifier.height(6.dp))
                Text(
                    text = log.stackTrace,
                    style = MaterialTheme.typography.bodySmall.copy(
                        fontFamily = FontFamily.Monospace,
                        fontSize = 11.sp,
                        color = colorScheme.error
                    )
                )
            }
        }
    }
}
